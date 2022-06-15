package com.app.mobiledev.salesapp.helperPackage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class helper extends AsyncTask {

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int NOTIF_ID = 56;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;
    static SessionManager sessionManager;
    public static final int QRcodeWidth = 500;
    public static final double latitude_bj = -7.795774;
    public static final double longtitude_bj = 110.409442;
    public static final String PESAN_KONEKSI = "Tidak dapat terhubung ke server";
    public static final String PESAN_SERVER = "Terdapat kesalahan pada server";
    public static final String Name = "sales_edit";
    public static final int REQUEST_READ_PHONE_STATE = 8;
    SharedPreferences sharedpreferences;
    static Locale localeID = new Locale("in", "ID");
    static NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static LayoutInflater inflater;
    private static  View dialogView;
    private static AlertDialog.Builder dialog;

    @SuppressLint("DefaultLocale")
    public static String rp(Double value) {
        if (value % 1 > 0) {
            return String.format("%,.2f", value);
        } else {
            return String.format("%,.0f", value);
        }
    }

    public static String format_tgl(String tgl, String data) {
        if (!tgl.equals("")) {
            return tgl.substring(8, 10).replace("/", "").replace("-", "") + "/"
                    + tgl.substring(5, 7).replace("/", "").replace("-", "")
                    + "/" + tgl.substring(0, 4).replace("/", "").replace("-", "");
        } else {
            return "";
        }
    }

    public static String format_tgl(String tgl) {
        if (!tgl.equals("")) {
            return tgl.substring(6, 8) + "/" + tgl.substring(4, 6) + "/" + tgl.substring(0, 4);
        } else {
            return "";
        }
    }

    public static  void disabledEditText(EditText edit){
        edit.setFocusable(false);
        edit.setCursorVisible(false);
        edit.setKeyListener(null);
    }


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
        showMsg(context, title, msg, NORMAL_TYPE);
    }

    public static void showMsg(Context context, String title, String msg, SweetAlertDialog.OnSweetClickListener callback) {
        showMsg(context, title, msg, helper.NORMAL_TYPE, callback);
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
            showMsg(context, "Informasi", "hidupkan wifi", NORMAL_TYPE);
        }
        return macAddress;
    }

    public static void requestPermissions(Context ctx) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) ctx,
                        Manifest.permission.READ_PHONE_STATE);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions((AppCompatActivity) ctx,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {
            ActivityCompat.requestPermissions((AppCompatActivity) ctx,
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
                ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) ctx,
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


    public static void snackBar(View v, String pesan) {
        Snackbar snackbar = Snackbar
                .make(v, pesan, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String newVersion = null;
        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.app.mobiledev.sws")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText();
            Log.d("CEK_VER_APP", "doInBackground: "+ newVersion);
            return newVersion;
        } catch (Exception e) {
            return newVersion;
        }
    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("CEK_VER_APP", "playstore version " + o);
    }


    public static BitmapDescriptor markerMap(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_maps);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static  void DialogKoneksi(Context ctx) {
        dialog = new AlertDialog.Builder(ctx);
        try {
            inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
            dialogView = inflater.inflate(R.layout.dialog_koneksi_putus, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            dialog.show();


        }catch (NullPointerException e){
            Log.d("NULLPointer", "DialogForm: "+e);
        }

    }

    public static String getDateNow(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        return  formattedDate;
    }

    public static String  getLokasi(double llat, double llon, Context mctx) {
        String alamat="";
        try {
            if(llat == 0){
                Log.d("CEK_LOKASI", "getLokasi: "+llat);
            }else {
                Log.d("LOKASI", "getLokasi: "+llat);
                Geocoder geocoder = new Geocoder(mctx, Locale.getDefault());
                String result = null;

                List<Address> addressList = geocoder.getFromLocation(llat, llon, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)); //.append("\n");
                    }
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                    Log.d("getLOKASI", "getLokasi: "+address.getAddressLine(0));
                    alamat=address.getAddressLine(0);
                    result = sb.toString();
                }

            }
        } catch (IOException e) {
            Log.e("Location Address Loader", "Unable connect to Geocoder", e);
        }catch (NullPointerException e){
            Log.e("Location Address Loader", "Unable connect to Geocoder", e);
        }

        return alamat;
    }


    public static String  split_date_time(String tipe_split,String date){
        //parameter tipe_split =date/time
        String result="";
        String tgl="";
        String waktu="";
        try {
            StringTokenizer tk = new StringTokenizer(date);
            tgl = tk.nextToken();  // <---  yyyy-mm-dd
            waktu = tk.nextToken();

            if(tipe_split.equals("date")){
                result=tgl;
            }
            if(tipe_split.equals("time")){
                result=waktu;
            }

        }catch (NullPointerException e){
            Log.d("NULL_POINTER", "split_date_time: "+e);
        }catch (NoSuchElementException e){
            Log.d("NULL_POINTER", "split_date_time: "+e);
        }
        return result;
    }

    public static double ParseCekDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

}
