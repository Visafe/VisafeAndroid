package vn.ncsc.visafe.dns.sys;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import vn.ncsc.visafe.R;
import vn.ncsc.visafe.ui.MainActivity;

public class MyFirebaseService extends FirebaseMessagingService {
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    public static int numMessages = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Log.d("FROM", remoteMessage.getFrom());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sendNotification(notification, data);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bundle bundle = new Bundle();
        bundle.putString(FCM_PARAM, data.get(FCM_PARAM));
        Notification.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.warning_channel_name);
            String description = getString(R.string.warning_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            notificationManager.createNotificationChannel(channel);
//        notificationManager.cancelAll();
            builder = new Notification.Builder(this, getString(R.string.notification_channel_id));
        } else {
            builder = new Notification.Builder(this);
            builder.setVibrate(new long[]{100, 200, 300, 400, 500});
            // Deprecated in API 26.
            builder = builder.setPriority(Notification.PRIORITY_MAX);
        }

        PendingIntent mainActivityIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(getColor(R.color.accent_color))
                .setLights(Color.RED, 1000, 300)
                .setSmallIcon(R.drawable.ic_logo_noti)
                .setDefaults(Notification.DEFAULT_ALL)
                .setNumber(++numMessages)
                .setFullScreenIntent(mainActivityIntent, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_ERROR);
        }
        notificationManager.notify(0, builder.getNotification());
    }
}