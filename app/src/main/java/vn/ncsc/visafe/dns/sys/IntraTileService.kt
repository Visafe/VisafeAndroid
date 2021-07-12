package vn.ncsc.visafe.dns.sys

import android.content.ComponentName
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.dns.sys.VpnController.Companion.instance

@RequiresApi(Build.VERSION_CODES.N)
class IntraTileService : TileService() {
    override fun onStartListening() {
        val vpnState = instance.getState(this)
        val tile = qsTile ?: return
        if (vpnState.activationRequested == true) {
            tile.state = Tile.STATE_ACTIVE
        } else {
            tile.state = Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    override fun onClick() {
        val vpnState = instance.getState(this)
        if (vpnState.activationRequested == true) {
            instance.stop(this)
        } else {
            if (VpnService.prepare(this) == null) {
                // Start VPN service when VPN permission has been granted.
                instance.start(this)
            } else {
                // Open Main activity when VPN permission has not been granted.
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityAndCollapse(intent)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Update tile state on boot.
        requestListeningState(
            this,
            ComponentName(this, IntraTileService::class.java)
        )
        return super.onBind(intent)
    }
}