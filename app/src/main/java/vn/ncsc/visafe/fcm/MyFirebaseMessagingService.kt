package vn.ncsc.visafe.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vn.ncsc.visafe.R
import vn.ncsc.visafe.dns.sys.ViSafeVpnService
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(
            "onMessageReceived",
            "data: " + remoteMessage.data
                    + " title: " + remoteMessage.notification?.title
                    + " content: " + remoteMessage.notification?.body
        )
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        sendNotification(title, message)
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e("onNewToken", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    @SuppressLint("LongLogTag")
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.e("sendRegistrationToServer", "($token)")
        token?.let {
            SharePreferenceKeyHelper.getInstance(application).putString(PreferenceKey.TOKEN_FCM, it)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            (Date().time / 1000L % Int.MAX_VALUE).toInt() /* Request code */,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_noti)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setPriority(2)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentIntent(pendingIntent)
            .setVisibility(Notification.VISIBILITY_SECRET)
        startForeground(0, notificationBuilder.notification)

        val notification: Notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_SHOW_LIGHTS
        notification.ledOnMS = 300
        notification.ledOffMS = 1000
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notification from Visafe",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notification)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}