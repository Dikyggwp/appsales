package com.app.mobiledev.salesapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.set_ip;
import com.app.mobiledev.salesapp.main_fragment;
import com.app.mobiledev.salesapp.sesion.SessionManager;

public class services_notif extends Service {
    private boolean isRunning;
    private Thread backgroundThread;
    private String iduser="";
    public static final String CHANNEL_ID = "com.app.mobiledev.apphris.ANDROID";
    private String nik="";
    private String url="";
    private NotificationManager mNotificationManager;
    Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    long timestamp;
    static set_ip ip = new set_ip();
    private SessionManager sessionmanager;
    public static final int NOTIF_ID = 56;
    public services_notif() {

    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        Log.d("start_service", "onCreate: ");
        sessionmanager = new SessionManager(getApplicationContext());
        this.backgroundThread = new Thread(myTask);

        try {

        }catch (NullPointerException e){
            Log.d("NULLPOINTER", "onCreate: "+e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    private Runnable myTask = new Runnable() {
        @Override
        public void run() {

            //TODO - how to handle socket events here?
            //How do I do something like mSocket.on(Socket.EVENT_CONNECT,onConnect); here?
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( !this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }

        return START_STICKY;
    }
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void push_notif(String title, String detail){
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.hris);
        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getApplicationContext().getPackageName() + "/raw/hris");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent open_form = new Intent(getApplicationContext(), main_fragment.class);
        open_form.putExtra("service_notif", "TRUE");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, open_form, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("");
        bigText.setBigContentTitle("HRIS MOBILE");
        bigText.setSummaryText("");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(""+title);
        mBuilder.setContentText(""+detail);
        mBuilder.setSound(alarmsound);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(true);
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.setStyle(bigText);
        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "HRIS_MOBILE";
            NotificationChannel channel = new NotificationChannel(channelId , ""+title, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(alarmsound,audioAttributes);
            channel.setDescription(""+detail);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.cancel("NOTIF", NOTIF_ID);
            mBuilder.setChannelId(channelId);
            startForeground(NOTIF_ID, mBuilder.build());

        }else{
            startService(new Intent(getApplicationContext(), services_notif.class));
            startForeground(NOTIF_ID, mBuilder.build());
        }


    }

}
