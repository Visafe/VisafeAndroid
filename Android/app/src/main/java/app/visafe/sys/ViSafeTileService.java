
package app.visafe.sys;

import android.content.ComponentName;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import app.visafe.ui.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ViSafeTileService extends TileService {

    @Override
    public void onStartListening() {
        VpnState vpnState = VpnController.getInstance().getState(this);

        Tile tile = getQsTile();
        if (tile == null) {
            return;
        }

        if (vpnState.activationRequested) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }

        tile.updateTile();
    }

    @Override
    public void onClick() {
        VpnState vpnState = VpnController.getInstance().getState(this);

        if (vpnState.activationRequested) {
            VpnController.getInstance().stop(this);
        } else {
            if (VpnService.prepare(this) == null) {
                // Start VPN service when VPN permission has been granted.
                VpnController.getInstance().start(this);
            } else {
                // Open Main activity when VPN permission has not been granted.
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityAndCollapse(intent);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Update tile state on boot.
        TileService.requestListeningState(this,
                new ComponentName(this, ViSafeTileService.class));
        return super.onBind(intent);
    }
}
