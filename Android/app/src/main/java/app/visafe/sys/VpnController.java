
package app.visafe.sys;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

// Singleton class to maintain state related to VPN Tunnel service.
public class VpnController {

  private static VpnController dnsVpnServiceState;

  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  public static synchronized VpnController getInstance() {
    if (dnsVpnServiceState == null) {
      dnsVpnServiceState = new VpnController();
    }
    return dnsVpnServiceState;
  }

  private ViSafeVpnService viSafeVpnService = null;
  private ViSafeVpnService.State connectionState = null;
  private QueryTracker tracker = null;

  private VpnController() {}

  void setViSafeVpnService(ViSafeVpnService viSafeVpnService) {
    this.viSafeVpnService = viSafeVpnService;
  }

  public @Nullable
  ViSafeVpnService getViSafeVpnService() {
    return this.viSafeVpnService;
  }

  public synchronized void onConnectionStateChanged(Context context, ViSafeVpnService.State state) {
    if (viSafeVpnService == null) {
      // User clicked disable while the connection state was changing.
      return;
    }
    connectionState = state;
    stateChanged(context);
  }

  private void stateChanged(Context context) {
    Intent broadcast = new Intent(InternalNames.DNS_STATUS.name());
    LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
  }

  public synchronized QueryTracker getTracker(Context context) {
    if (tracker == null) {
      tracker = new QueryTracker(context);
    }
    return tracker;
  }

  public synchronized void start(Context context) {
    if (viSafeVpnService != null) {
      return;
    }
    PersistentState.setVpnEnabled(context, true);
    stateChanged(context);
    Intent startServiceIntent = new Intent(context, ViSafeVpnService.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(startServiceIntent);
    } else {
      context.startService(startServiceIntent);
    }
  }

  synchronized void onStartComplete(Context context, boolean succeeded) {
    if (!succeeded) {
      // VPN setup only fails if VPN permission has been revoked.  If this happens, clear the
      // user intent state and reset to the default state.
      stop(context);
    } else {
      stateChanged(context);
    }
  }

  public synchronized void stop(Context context) {
    PersistentState.setVpnEnabled(context, false);
    connectionState = null;
    if (viSafeVpnService != null) {
      viSafeVpnService.signalStopService(true);
    }
    viSafeVpnService = null;
    stateChanged(context);
  }

  public synchronized VpnState getState(Context context) {
    boolean requested = PersistentState.getVpnEnabled(context);
    boolean on = viSafeVpnService != null && viSafeVpnService.isOn();
    return new VpnState(requested, on, connectionState);
  }

}
