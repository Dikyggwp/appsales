package com.app.mobiledev.salesapp.kunjungan;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.BuildConfig;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.service_location.LocationUpdate;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.kyanogen.signatureview.SignatureView;
import com.mindorks.paracamera.Camera;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class kunjunganUnplan extends AppCompatActivity implements OnMapReadyCallback {
    private Camera camera;
    private ImageView image,imagettd;
    private Bitmap imageFoto;
    int i = 0;
    private TextView tvLokasi,tvttd,edoutlet;
    private EditText edcatatan;
    private double lat=0;
    private double lon=0;
    private Button btnAbsen;
    private SweetAlertDialog mProgressDialog;
    private String encodedimage="",encodettd="";
    //===maps//
    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 34;
    private Boolean mLocationPermissionGranted=false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private Marker geoFenceMarker;
    private String outlet,iduser,id,idoutlet,alasan;
    SignatureView signatureView;
    SessionManager sesi;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi_masuk);
        signatureView = (SignatureView) findViewById(R.id.signature_view);

        Intent iin = getIntent();
        Bundle sa = iin.getExtras();
        try {
            alasan = sa.get("alasan")!=null?sa.get("alasan").toString():null;
            outlet = sa.get("outlet")!=null?sa.get("outlet").toString():null;
            id = sa.get("id")!=null?sa.get("id").toString():null;
            idoutlet = sa.get("idoutlet")!=null?sa.get("idoutlet").toString():null;
        }catch (Exception e) {
            alasan ="";
            outlet ="";
            id ="";
            idoutlet ="";
        }
        Toast.makeText(this, ""+alasan, Toast.LENGTH_SHORT).show();

        image=findViewById(R.id.image);
        imagettd=findViewById(R.id.imagettd);
        edoutlet=findViewById(R.id.edoutlet);
        tvttd=findViewById(R.id.tvttd);
        tvLokasi=findViewById(R.id.tvLokasi);
        edcatatan=findViewById(R.id.edcatatan);
        btnAbsen=findViewById(R.id.btnAbsen);
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#FF5722"));
        mProgressDialog.setTitleText("Loading, Upload Kunjungan");
        mProgressDialog.setCancelable(true);
        edoutlet.setText(outlet);

        sesi = new SessionManager(this);
        iduser = sesi.getIDUser();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera = new Camera.Builder()
                        .resetToCorrectOrientation(true)
                        .setTakePhotoRequestCode(0)
                        .setDirectory("pics")
                        .setName("ali_" + System.currentTimeMillis())
                        .setImageFormat(Camera.IMAGE_JPEG)
                        .setCompression(75)
                        .setImageHeight(1000)
                        .build(kunjunganUnplan.this);
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tvttd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_signature();
            }
        });
        btnAbsen.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                String lokasi = tvLokasi.getText().toString();
                try {
                    if(imageFoto==null){
                        helper.showMsg(kunjunganUnplan.this,"Informasi","Picture not taken");
                        mProgressDialog.dismiss();
                    }else{
                        if(lokasi.equals("null")||lokasi.equals(null)||lokasi.equals("")){
                            helper.showMsg(kunjunganUnplan.this,"","Lokasi anda belum  terdeteksi \n pastikan gps anda aktif\n coba restart aplikasi",helper.WARNING_TYPE);
                            mProgressDialog.dismiss();
                        }else{
                            absen();
                        }
                    }

                }catch (NullPointerException e){
                    helper.showMsg(kunjunganUnplan.this,"","Lokasi anda belum  terdeteksi \n pastikan gps anda aktif\n coba restart aplikasi",helper.WARNING_TYPE);
                    mProgressDialog.dismiss();
                }
            }
        });
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    try{
                        mMap.clear();
                        LatLng mylokasi = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylokasi));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        getLokasi(location.getLatitude(), location.getLongitude());
                        cekFakeGps();
                    }catch (Exception e){
                        Log.d("LOCATION_CALL_BACK", "onLocationResult: "+e);
                    }

                }
            }
        };
        cekFakeGps();

    }

    private void dialog_signature() {
        final Dialog dialog = new Dialog(kunjunganUnplan.this);
        dialog.setContentView(R.layout.ttd);
        dialog.setTitle("Tanda Tangan Customer");
        Button bersihkan, simpan;
        signatureView = dialog.findViewById(R.id.signature_view);
        bersihkan = dialog.findViewById(R.id.bersihkan);
        simpan = dialog.findViewById(R.id.simpan);
        bersihkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                bitmap = signatureView.getSignatureBitmap();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes);
                encodettd = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                if (bitmap != null ) {
                    imagettd.setImageBitmap(bitmap);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                Bitmap bitmap = camera.getCameraBitmap();
                if (bitmap != null ) {
                    image.setImageBitmap(bitmap);
                    imageFoto=bitmap;
                    imageFoto=Bitmap.createScaledBitmap(imageFoto, 500, 500, false);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageFoto.compress(Bitmap.CompressFormat.PNG, 50, bytes);
                    encodedimage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                }
                else { Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show(); }
            }
        }catch (Exception e){
            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
        }

    }

    public void getLokasi(double llat,double llon) {
        try {
        if(llat == 0){
            Log.d("CEK_LOKASI", "getLokasi: "+llat);
        }else {
            lat = llat;
            lon = llon;
            Log.d("LOKASI", "getLokasi: "+llat);
            Geocoder geocoder = new Geocoder(kunjunganUnplan.this, Locale.getDefault());
            String result = null;
                List<Address > addressList = geocoder.getFromLocation(lat, lon, 1);
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
                    tvLokasi.setText(""+address.getAddressLine(0));
                    result = sb.toString();
                }

        }
        } catch (IOException e) {
            Log.e("Location Address Loader", "Unable connect to Geocoder", e);
        }catch (NullPointerException e){
            Log.e("Location Address Loader", "Unable connect to Geocoder", e);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void absen() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.upload_KunjunganUnplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        new SweetAlertDialog(kunjunganUnplan.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Informasi")
                                .setContentText(""+data)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    finish();
                                    startActivityForResult(new Intent(kunjunganUnplan.this, ListKunjungan.class), 33);
                                    sweetAlertDialog.dismiss();
                                })
                                .show();
                    } else {
                        Log.d("JSONERORABSEN", "false: ");
                        helper.showMsg(kunjunganUnplan.this,"Informasi",""+data,helper.WARNING_TYPE);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSONERORABSEN", "onResponse: "+e);
                    helper.showMsg(kunjunganUnplan.this, "Peringatan", "" + helper.PESAN_SERVER+e, helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
            }
        },
                error -> {
                    mProgressDialog.dismiss();
                    Log.d("DATA_JSONEXCEPION", "onResponse: "+error);
                    helper.showMsg(kunjunganUnplan.this, "Peringatan", ""+helper.PESAN_KONEKSI, helper.ERROR_TYPE);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put( "img", "data:image/png;base64,"+encodedimage);
                params.put( "ttd", "data:image/png;base64,"+encodettd);
                params.put("outlet",outlet);
                params.put("idoutlet",idoutlet);
                params.put("id_user",iduser);
                params.put("id_plan",id);
                params.put("location", lat+","+lon);
                params.put("alamat", tvLokasi.getText().toString());
                params.put("catatan", edcatatan.getText().toString());
                params.put("alasan", alasan);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
               mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                Log.d("ENABLE_LOCATION", "onMapReady: ");
            }else{
                new SweetAlertDialog(kunjunganUnplan.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Versi Android anda Tidak cocok")
                        .setContentText("Yakin keluar aplikasi?")
                        .setConfirmText("Ya, keluar")
                        .setCancelText("Tidak")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                                moveTaskToBack(true);
                            }
                        }).show();
            }
        }

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(kunjunganUnplan.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
        updateLocationUI();
    }
    private Geofence createGeofence(LatLng latLng, float radius ) {
        Log.d("cek_GEOFANCING", "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }
    public boolean isMockLocationEnabled() {
        boolean isMockLocation = false;
        try {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            } else {
                // in marshmallow this will always return true
                isMockLocation = !Settings.Secure.getString(getApplicationContext().getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return isMockLocation;
        }
        return isMockLocation;
    }

    public void cekFakeGps() {
        try {
            LocationUpdate gt = new LocationUpdate(kunjunganUnplan.this);
            Location l = gt.getLocation();
            if (l.isFromMockProvider()){
                new SweetAlertDialog(kunjunganUnplan.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Terindikasi pelanggaran")
                        .setContentText("terdeteksi aplikasi fake gps")
                        .setConfirmText("close")
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
        } catch (NullPointerException e){

        }
    }

}
