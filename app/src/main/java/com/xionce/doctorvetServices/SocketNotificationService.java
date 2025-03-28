package com.xionce.doctorvetServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketNotificationService extends Service {

    private static final String TAG = "SocketNotificationServ";
    private static final String CHANNEL_ID = "DoctorVetForegroundServiceChannel";
    private Socket socket;
    private ArrayList<Agenda> agenda_array = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        socket = DoctorVetApp.get().getSocket();
        if (socket != null)
            socket.on("server_notification_message", serverNotificationMessage);

        android.util.Log.i("tag", "SocketServerNotification init");


        // Crear la notificación
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MY_CHANNEL_ID")
//                .setSmallIcon(R.drawable.ic_account_circle_dark)
//                .setContentTitle("Título de la Notificación")
//                .setContentText("Este es el texto de la notificación.")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        // Mostrar la notificación
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify(1, builder.build());


        //        NotificationChannel channel = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            channel = new NotificationChannel(
//                    "MY_CHANNEL_ID",
//                    "My Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//
//            channel.setDescription("Descripción del canal");
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }


        // Crear un canal de notificación para el foreground service
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }

        // Configurar la conexión de Socket.IO
//        try {
//            mSocket = IO.socket("http://yourserver.com");
//            mSocket.on(Socket.EVENT_CONNECT, onConnect);
//            mSocket.on("custom-event", onCustomEvent);
//            mSocket.connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private Emitter.Listener serverNotificationMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            android.util.Log.i("tag", "SocketServerNotification message");

            JSONObject incomingData = null;
            try {
                incomingData = new JSONObject(args[0].toString());
                String table_name = incomingData.getString("table_name");
                String operation = incomingData.getString("operation");

                if (table_name.equalsIgnoreCase("agenda"))
                    manageMemTable(operation, incomingData);

            } catch (JSONException e) {
                DoctorVetApp.get().handle_error(e, "SocketNotificationService", DoctorVetApp.SHOW_ERROR_MESSAGE);
            }
        }
    };

    private void manageMemTable(String operation, JSONObject incomingData) {
        try {
            Agenda agenda = MySqlGson.getGson().fromJson(incomingData.getString("data"), Agenda.class);

            switch (operation.toLowerCase()) {
                case "created":
                    agenda_array.add(agenda);
                    break;
                case "deleted":
                    agendaArrayRemove(agenda.getId());
                    break;
                case "updated":
                    agendaArrayRemove(agenda.getId());
                    agenda_array.add(agenda);
                    break;
            }
        } catch (JSONException e) {
            DoctorVetApp.get().handle_error(e, "SocketNotificationService", DoctorVetApp.SHOW_ERROR_MESSAGE);
        }
    }

    private void agendaArrayRemove(Integer id) {
        agenda_array.removeIf(new Predicate<Agenda>() {
            @Override
            public boolean test(Agenda agenda) {
                return agenda.getId().equals(id);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Socket Service")
//                .setContentText("Manteniendo la conexión con el servidor")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            //mSocket.disconnect();
            socket.off("server_message", serverNotificationMessage);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
