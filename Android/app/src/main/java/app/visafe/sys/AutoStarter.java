
package app.visafe.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.util.Log;
import app.visafe.sys.firebase.LogWrapper;
import app.visafe.sys.firebase.RemoteConfig;
import app.visafe.ui.MainActivity;

/**
 * Broadcast receiver that runs on boot, and also when the app is restarted due to an update.
 */
public class AutoStarter extends BroadcastReceiver {
  private static final String LOG_TAG = "AutoStarter";

  @Override
  public void onReceive(final Context context, Intent intent) {
    if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      return;
    }
    LogWrapper.log(Log.DEBUG, LOG_TAG, "Boot event");
    final VpnController controller = VpnController.getInstance();
    VpnState state = controller.getState(context);
    if (state.activationRequested && !state.on) {
      LogWrapper.log(Log.DEBUG, LOG_TAG, "Autostart enabled");
      if (VpnService.prepare(context) != null) {
        // prepare() returns a non-null intent if VPN permission has not been granted.
        LogWrapper.log(Log.WARN, LOG_TAG, "VPN permission not granted.  Starting UI.");
        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
        return;
      }
      // Delay start until after the remote configuration has been updated, or failed to update.
      RemoteConfig.update().addOnCompleteListener(success -> controller.start(context));
    }
  }
}
