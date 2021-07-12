package vn.ncsc.visafe.dns.sys

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import vn.ncsc.visafe.ui.MainActivity

class AutoStarter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED != intent!!.action) {
            return
        }
        val controller = VpnController.instance
        val state = controller.getState(context)
        if (state.activationRequested == true && !state.on) {
            if (VpnService.prepare(context) != null) {
                // prepare() returns a non-null intent if VPN permission has not been granted.
                val startIntent = Intent(context, MainActivity::class.java)
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(startIntent)
                return
            }
        }
    }
}