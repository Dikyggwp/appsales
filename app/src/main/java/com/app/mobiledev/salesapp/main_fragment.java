package com.app.mobiledev.salesapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.app.mobiledev.salesapp.api.set_ip;
import com.app.mobiledev.salesapp.cek_gps.GpsUtils;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.service.DemoApplication;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class main_fragment extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener  {
    private FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    Fragment fragment = null;
    Toolbar toolbar_abs;
    private boolean isGPS = false;
    private String nama;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private   int index=0;
    private String userid;
    static set_ip ip = new set_ip();
    Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public static final int NOTIF_ID = 56;
    SessionManager sesi;
    private NotificationManager mNotificationManager;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        toolbar_abs = findViewById(R.id.toolbar_abs);
        setSupportActionBar(toolbar_abs);
        sesi = new SessionManager(this);
        nama = sesi.getUser();
        toolbar_abs.setTitle(""+nama);
        setSupportActionBar(toolbar_abs);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fragment = new fragment_home();
            callFragment(fragment);
        }
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });
        helper.requestPermissionsGps(main_fragment.this);
        //cekGps();
    }

    private void createNotification(String val,String materi) {
        // Construct pending intent to serve as action for notification item
        Intent intent = new Intent(this, main_fragment.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Create notification
        String longText = ""+val;

        Notification noti =
                new NotificationCompat.Builder(this, DemoApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_info)
                        .setSound(alarmsound)
                        .setContentTitle("SWS MOBILE")
                        .setContentText(""+materi)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                        .setContentIntent(pIntent)
                        .build();
        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, noti);
        Log.d("NOTIF_CREATE", "createNotification: "+ noti);
    }

    private void callFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment)
                .commit();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            int id = menuItem.getItemId();

            if (id == R.id.home) {
                fragment = new fragment_home();
                callFragment(fragment);
            }

            else if (id == R.id.report) {
                fragment = new fragment_report();
                callFragment(fragment);
            }

            else if (id == R.id.menu) {
                fragment = new fragment_profil();
                callFragment(fragment);
            }

            return true;

        }catch (RuntimeException e){
            Log.d("EROOR", "onNavigationItemSelected: "+e);
        }

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void cekGps(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i("About GPS", "GPS is Enabled in your devide");
            } else {
                Toast.makeText(this, "Mohon aktifkan GPS", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(main_fragment.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Konfirmasi")
                .setContentText("Yakin keluar aplikasi?")
                .setConfirmText("Ya")
                .setCancelText("Tidak")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }).show();
    }

    public void showMsg(String title, String msg, int type) {
        new SweetAlertDialog(main_fragment.this, type)
                .setTitleText(title)
                .setContentText(msg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

}
