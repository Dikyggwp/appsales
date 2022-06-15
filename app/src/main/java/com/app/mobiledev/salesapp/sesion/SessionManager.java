package com.app.mobiledev.salesapp.sesion;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.mobiledev.salesapp.login;
import com.app.mobiledev.salesapp.main_fragment;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "LOGIN";
    public static final String ID_KARYAWAN = "id_karyawan";
    public static final String NAMA = "nama";
    public static final String USER = "user";
    public static final String AREA = "area";
    public static final String KODE_AREA = "kodearea";
    public static String kode_area;
    public static String area;
    public static String user;
    public static String nama;
    public static String id_karyawan;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String idkaryawan, String email, String nama, String kodearea, String area){
        editor.putString(ID_KARYAWAN, idkaryawan);
        editor.putString(USER, email);
        editor.putString(NAMA, nama);
        editor.putString(AREA, area);
        editor.putString(KODE_AREA, kodearea);
        editor.apply();
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }
    public String getUser(){
        user=sharedPreferences.getString(USER, "");
        return user;
    }
    public String getKodeArea(){
        kode_area=sharedPreferences.getString(KODE_AREA, "");
        return kode_area;
    }
    public String getArea(){
        area=sharedPreferences.getString(AREA, "");
        return area;
    }
    public String getIDUser() {
        id_karyawan = sharedPreferences.getString(ID_KARYAWAN, "");
        return id_karyawan;
    }


}

