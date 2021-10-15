
package app.visafe.net.go;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import app.visafe.net.doh.Prober;
import app.visafe.sys.VpnController;
import doh.Transport;
import protect.Protector;
import tun2socks.Tun2socks;

/**
 * Implements a Probe using the Go-based DoH client.
 */
public class GoProber extends Prober {

  private final Context context;

  public GoProber(Context context) {
    this.context = context;
  }

  @Override
  public void probe(String url, Callback callback) {
    new Thread(() -> {
      String dohIPs = GoVpnAdapter.getIpString(context, url);
      try {
        // Protection isn't needed for Lollipop+, or if the VPN is not active.
        Protector protector = VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP ? null :
            VpnController.getInstance().getViSafeVpnService();
        Transport transport = Tun2socks.newDoHTransport(url, dohIPs, protector, null, null);
        if (transport == null) {
          callback.onCompleted(false);
          return;
        }
        byte[] response = transport.query(QUERY_DATA);
        if (response != null && response.length > 0) {
          callback.onCompleted(true);
          return;
        }
        callback.onCompleted(false);
      } catch (Exception e) {
        callback.onCompleted(false);
      }
    }).start();
  }
}
