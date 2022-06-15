package com.app.mobiledev.salesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class splashScreen extends AppCompatActivity {

    String userid;
    private static int LamaTampilSplash = 1000;
    SessionManager sesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sesi = new SessionManager(this);
        userid = sesi.getIDUser();

        helper.requestPermissions(splashScreen.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        if (userid.equals("") || userid.equals(null)) {
                            Intent lo = new Intent(splashScreen.this, login.class);
                            startActivity(lo);
                            finish();
                        } else {
                            Intent intent = new Intent(splashScreen.this, main_fragment.class);
                            startActivity(intent);
                            finish();
                        }
                    this.selesai();
            }
            private void selesai() {
                finish();
            }
        },LamaTampilSplash);
    };

}