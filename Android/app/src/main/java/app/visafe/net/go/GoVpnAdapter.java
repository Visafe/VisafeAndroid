
package app.visafe.net.go;

import android.content.Context;
import android.content.res.Resources;
import android.net.VpnService;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.visafe.R;
import app.visafe.sys.CountryCode;
import app.visafe.sys.ViSafeVpnService;
import app.visafe.sys.PersistentState;
import app.visafe.sys.VpnController;
import app.visafe.sys.firebase.AnalyticsWrapper;
import app.visafe.sys.firebase.LogWrapper;
import app.visafe.sys.firebase.RemoteConfig;
import doh.Transport;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import protect.Protector;
import tun2socks.Tun2socks;

/**
 * This is a VpnAdapter that captures all traffic and routes it through a go-tun2socks instance with
 * custom logic for ViSafe.
 */
public class GoVpnAdapter {
  private static final String LOG_TAG = "GoVpnAdapter";

  // This value must match the hardcoded MTU in outline-go-tun2socks.
  // TODO: Make outline-go-tun2socks's MTU configurable.
  private static final int VPN_INTERFACE_MTU = 1500;
  private static final int DNS_DEFAULT_PORT = 53;

  // IPv4 VPN constants
  private static final String IPV4_TEMPLATE = "10.111.222.%d";
  private static final int IPV4_PREFIX_LENGTH = 24;

  // Choir salt file name
  private static final String CHOIR_FILENAME = "choir-salt";

  // The VPN service and tun2socks must agree on the layout of the network.  By convention, we
  // assign the following values to the final byte of an address within a subnet.
  private enum LanIp {
    GATEWAY(1), ROUTER(2), DNS(3);

    // Value of the final byte, to be substituted into the template.
    private final int value;

    LanIp(int value) {
      this.value = value;
    }

    String make(String template) {
      return String.format(Locale.ROOT, template, value);
    }
  }

  public static final String FAKE_DNS_IP = LanIp.DNS.make(IPV4_TEMPLATE);

  // Service context in which the VPN is running.
  private final ViSafeVpnService vpnService;

  // TUN device representing the VPN.
  private ParcelFileDescriptor tunFd;

  // The ViSafe session object from go-tun2socks.  Initially null.
  private intra.Tunnel tunnel;
  private GoViSafeListener listener;

  public static GoVpnAdapter establish(@NonNull ViSafeVpnService vpnService) {
    ParcelFileDescriptor tunFd = establishVpn(vpnService);
    if (tunFd == null) {
      return null;
    }
    return new GoVpnAdapter(vpnService, tunFd);
  }

  private GoVpnAdapter(ViSafeVpnService vpnService, ParcelFileDescriptor tunFd) {
    this.vpnService = vpnService;
    this.tunFd = tunFd;
  }

  public synchronized void start() {
    connectTunnel();
  }

  private void connectTunnel() {
    if (tunnel != null) {
      return;
    }
    // VPN parameters
    final String fakeDns = FAKE_DNS_IP + ":" + DNS_DEFAULT_PORT;

    // Strip leading "/" from ip:port string.
    listener = new GoViSafeListener(vpnService);
    String dohURL = PersistentState.getServerUrl(vpnService);

    try {
      LogWrapper.log(Log.INFO, LOG_TAG, "Starting go-tun2socks");
      Transport transport = makeDohTransport(dohURL);
      // connectViSafeTunnel makes a copy of the file descriptor.
      tunnel = Tun2socks.connectIntraTunnel(tunFd.getFd(), fakeDns,
          transport, getProtector(), listener);
    } catch (Exception e) {
      LogWrapper.logException(e);
      VpnController.getInstance().onConnectionStateChanged(vpnService, ViSafeVpnService.State.FAILING);
      return;
    }

    if (RemoteConfig.getChoirEnabled()) {
      enableChoir();
    }
  }

  // Set up failure reporting with Choir.
  private void enableChoir() {
    CountryCode countryCode = new CountryCode(vpnService);
    @NonNull String country = countryCode.getNetworkCountry();
    if (country.isEmpty()) {
      country = countryCode.getDeviceCountry();
    }
    if (country.isEmpty()) {
      // Country code is mandatory for Choir.
      Log.i(LOG_TAG, "No country code found");
      return;
    }
    String file = vpnService.getFilesDir() + File.separator + CHOIR_FILENAME;
    try {
      tunnel.enableSNIReporter(file, "ViSafe.metrics.gstatic.com", country);
    } catch (Exception e) {
      // Choir setup failure is logged but otherwise ignored, because it does not prevent ViSafe
      // from functioning correctly.
      LogWrapper.logException(e);
    }
  }

  private static ParcelFileDescriptor establishVpn(ViSafeVpnService vpnService) {
    try {
      VpnService.Builder builder = vpnService.newBuilder()
          .setSession("Visafe Private VPN")
          .setMtu(VPN_INTERFACE_MTU)
          .addAddress(LanIp.GATEWAY.make(IPV4_TEMPLATE), IPV4_PREFIX_LENGTH)
          .addRoute("0.0.0.0", 0)
          .addDnsServer(LanIp.DNS.make(IPV4_TEMPLATE));
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        builder.addDisallowedApplication(vpnService.getPackageName());
      }
      if (VERSION.SDK_INT >= VERSION_CODES.Q) {
        builder.setMetered(false); // There's no charge for using ViSafe.
      }
      return builder.establish();
    } catch (Exception e) {
      LogWrapper.logException(e);
      return null;
    }
  }

  private @Nullable Protector getProtector() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      // We don't need socket protection in these versions because the call to
      // "addDisallowedApplication" effectively protects all sockets in this app.
      return null;
    }
    return vpnService;
  }

  public synchronized void close() {
    if (tunnel != null) {
      tunnel.disconnect();
    }
    if (tunFd != null) {
      try {
        tunFd.close();
      } catch (IOException e) {
        LogWrapper.logException(e);
      }
    }
    tunFd = null;
  }

  private doh.Transport makeDohTransport(@Nullable String url) throws Exception {
    @NonNull String realUrl = PersistentState.expandUrl(vpnService, url);
    String dohIPs = getIpString(vpnService, realUrl);
    String host = new URL(realUrl).getHost();
    long startTime = SystemClock.elapsedRealtime();
    final doh.Transport transport;
    try {
      transport = Tun2socks.newDoHTransport(realUrl, dohIPs, getProtector(), null, listener);
    } catch (Exception e) {
      AnalyticsWrapper.get(vpnService).logBootstrapFailed(host);
      throw e;
    }
    int delta = (int) (SystemClock.elapsedRealtime() - startTime);
    AnalyticsWrapper.get(vpnService).logBootstrap(host, delta);
    return transport;
  }

  /**
   * Updates the DOH server URL for the VPN.  If Go-DoH is enabled, DNS queries will be handled in
   * Go, and will not use the Java DoH implementation.  If Go-DoH is not enabled, this method
   * has no effect.
   */
  public synchronized void updateDohUrl() {
    if (tunFd == null) {
      // Adapter is closed.
      return;
    }
    if (tunnel == null) {
      // Attempt to re-create the tunnel.  Creation may have failed originally because the DoH
      // server could not be reached.  This will update the DoH URL as well.
      connectTunnel();
      return;
    }
    // Overwrite the DoH Transport with a new one, even if the URL has not changed.  This function
    // is called on network changes, and it's important to switch to a fresh transport because the
    // old transport may be using sockets on a deleted interface, which may block until they time
    // out.
    String url = PersistentState.getServerUrl(vpnService);
    try {
      tunnel.setDNS(makeDohTransport(url));
    } catch (Exception e) {
      LogWrapper.logException(e);
      tunnel.disconnect();
      tunnel = null;
      VpnController.getInstance().onConnectionStateChanged(vpnService, ViSafeVpnService.State.FAILING);
    }
  }

  // Returns the known IPs for this URL as a string containing a comma-separated list.
  static String getIpString(Context context, String url) {
    Resources res = context.getResources();
    String[] urls = res.getStringArray(R.array.urls);
    String[] ips = res.getStringArray(R.array.ips);
    String ret = "";
    for (int i = 0; i < urls.length; ++i) {
      // TODO: Consider relaxing this equality condition to a match on just the domain.
      if (urls[i].equals(url)) {
        ret = ips[i];
        break;
      }
    }

    try {
      String domain = new URL(url).getHost();
      String extraIPs = RemoteConfig.getExtraIPs(domain);
      if (ret.isEmpty()) {
        ret = extraIPs;
      } else if (!extraIPs.isEmpty()) {
        ret += "," + extraIPs;
      }
    } catch (MalformedURLException e) {
      LogWrapper.logException(e);
    }

    return ret;
  }
}
