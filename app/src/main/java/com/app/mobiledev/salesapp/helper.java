package com.app.mobiledev.salesapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class helper {

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;
    public static final int QRcodeWidth = 500 ;
    public static final double latitude_bj=-7.795774;
    public static final double longtitude_bj=110.409442;
    public static  final String PESAN_KONEKSI="Koneksi internet bermasalah";
    public static  final String PESAN_SERVER="Terdapat kesalahan pada server";

    public static final String Name = "sales_edit";
    public static final int REQUEST_READ_PHONE_STATE = 8;
    SharedPreferences sharedpreferences;
    static Locale localeID = new Locale("in", "ID");
    static NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @SuppressLint("DefaultLocale")
    public static String rp(Double value){
        if (value % 1 > 0){
            return String.format("%,.2f",value);
        } else {
            return String.format("%,.0f",value);
        }
    }
//20190831
    public static String format_tgl(String tgl,String data){
        if (!tgl.equals("")) {
            return tgl.substring(8, 10).replace("/","").replace("-","") + "/" + tgl.substring(5, 7).replace("/","").replace("-","") + "/" + tgl.substring(0, 4).replace("/","").replace("-","");
        } else {
            return "";
        }
    }


    public static String format_tgl(String tgl){
        if (!tgl.equals("")) {
            return tgl.substring(6, 8) + "/" + tgl.substring(4, 6) + "/" + tgl.substring(0, 4);
        } else {
            return "";
        }
    }

//    public  static void update_versi(final String versi, final Context mctx){
//        AndroidNetworking.post(api.URL_get_url_version)
//                .addBodyParameter( "versi", versi)
//                .addBodyParameter("key", api.key)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Boolean success = response.getBoolean("status");
//
//                            if(success){
//                                Intent load= new Intent(mctx,login.class);
//                                mctx.startActivity(load);
//                            }else{
//                                Intent load1= new Intent(mctx,update_layout.class);
//                                mctx.startActivity(load1);
//                            }
//
//                            Log.d("CEK_UPDATE", "onResponse: "+success);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            helper.showMsg(mctx,"informasi",""+helper.PESAN_SERVER);
//                            Log.d("JSONUPDATE", "onResponse: "+e);
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.d("EROOR_UPDATE", "onError: "+anError);
//                        helper.showMsg(mctx,"informasi",""+helper.PESAN_KONEKSI);
//
//                    }
//                });
//
//    }








    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    public static void showMsg(Context context, String title, String msg) {
        showMsg(context,title,msg,NORMAL_TYPE);
    }


    public static void showMsg(Context context, String title, String msg, SweetAlertDialog.OnSweetClickListener callback){
        showMsg(context,title,msg,helper.NORMAL_TYPE,callback);
    }


    public static void showMsg(Context context, String title, String msg, int type) {
        new SweetAlertDialog(context, type)
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

    public static void showMsg(Context context, String title, String msg, int type, SweetAlertDialog.OnSweetClickListener callback) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setConfirmClickListener(callback)
                .setContentText(msg).show();
    }

        public static String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            showMsg(context,"Informasi","hidupkan wifi",NORMAL_TYPE);
        }
        return macAddress;
    }


    public static String getKodeIme(Context ctx){
        String ts = Context.TELEPHONY_SERVICE;
        String imei="";
        TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(ts);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("CEK_IMEI", "getKodeIme: "+imei);

        }
         imei = mTelephonyMgr.getDeviceId();
        return  imei;

    }

  public static  void requestPermissions(Context ctx) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) ctx,
                        Manifest.permission.READ_PHONE_STATE);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions((AppCompatActivity)ctx,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {
            ActivityCompat.requestPermissions((AppCompatActivity)ctx,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
    }



    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }


    public static void requestPermissionsGps(Context ctx) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)ctx,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions((AppCompatActivity) ctx,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions((AppCompatActivity) ctx,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    public static void snackBar(View v, String pesan){
        Snackbar snackbar = Snackbar
                .make(v, pesan, Snackbar.LENGTH_LONG);
        snackbar.show();
    }










}
