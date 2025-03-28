package com.xionce.doctorvetServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Maneja el mensaje FCM aquí
        if (remoteMessage.getNotification() != null) {

            Integer pet_id = null;

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                pet_id = Integer.parseInt(remoteMessage.getData().get("id_pet"));
            }

            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), pet_id);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        if (!DoctorVetApp.get().existsLocalUserNotificationToken())
            return;

        DoctorVetApp.get().deleteUserNotificationToken(DoctorVetApp.get().getLocalUserNotificationToken(), new DoctorVetApp.VolleyCallback() {
            @Override
            public void onSuccess(Boolean result) {
                DoctorVetApp.get().postUserNotificationToken(token, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        DoctorVetApp.get().setLocalUserNotificationToken(token);
                    }
                });
            }
        });
    }

    private void showNotification(String title, String body, @Nullable Integer pet_id) {

        PendingIntent pendingIntent = null;

        android.util.Log.w("PETID", "showNotificationExecuted PETID:" + String.valueOf(pet_id));
        if (pet_id != null) {
            Intent intent = new Intent(this, ViewPetActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet_id);

            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.logotype_fin_1)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
