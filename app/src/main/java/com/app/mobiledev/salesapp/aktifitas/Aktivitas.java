package com.app.mobiledev.salesapp.aktifitas;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Aktivitas extends AppCompatActivity {

    private SweetAlertDialog mProgressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String SHARED_PREF_NAME = "com.app.mobiledev.appsales";
    private String SP_IDUSER = "iduser";
    private String SP_NAMA = "nama";
    private String email,nama,iduser;
    private TextView Tvlogbook,tvpresentasi;
    AktifitasModal di;
    private RecyclerView rcaktifitas;
    private List<aktifitasModel> aktifitasModels;
    ImageView imgNoProduct;
    SignatureView signatureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktifitas);
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mProgressDialog.setTitleText("Loading..Mohon tunggu");
        mProgressDialog.setCancelable(true);
        signatureView = findViewById(R.id.signature_view);

        di = new AktifitasModal();
        sp = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        iduser = sp.getString(SP_IDUSER, "");
        nama = sp.getString(SP_NAMA, "");
        Tvlogbook = findViewById(R.id.Tvlogbook);
        rcaktifitas = findViewById(R.id.rcaktifitas);
        imgNoProduct = findViewById(R.id.image_no_product);
        tvpresentasi = findViewById(R.id.tvpresentasi);
        Tvlogbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("logbook");
            }
        });
        tvpresentasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_signature();
            }
        });
        aktifitasModels = new ArrayList<>();
        get_aktifitas();
    }
    private void dialog_signature() {
        final Dialog dialog = new Dialog(Aktivitas.this);
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
                send_aktivitas(bitmap, "");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void openDialog(String konten) {
        di.ctx = Aktivitas.this;
        Bundle bundl = new Bundle();
        bundl.putString("konten",konten);
        di.setArguments(bundl);
        di.show(getSupportFragmentManager(), "Aktifitas");
    }
    private void send_aktivitas(final Bitmap bitmap, final String edaktifitas) {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.add_produk, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(Aktivitas.this,""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(Aktivitas.this,""+data, Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(Aktivitas.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(Aktivitas.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes);
                String encodedImage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("kyano", iduser);
                params.put("ttd", "data:image/png;base64," + encodedImage);
                params.put("status", edaktifitas);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Aktivitas.this);
        requestQueue.add(stringRequest);
    }
    public void get_aktifitas() {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_getaktifitas, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    aktifitasModels.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            aktifitasModel model = new aktifitasModel();
                            String tgl =data.getString("datecreated");
                            String aktifitas =data.getString("aktifitas");
                            model.setTgl(tgl);
                            model.setAktifitas(aktifitas);
                            aktifitasModels.add(model);
                        }
                        aktifitasAdapter mAdapter;
                        mAdapter = new aktifitasAdapter(aktifitasModels, Aktivitas.this);
                        mAdapter.notifyDataSetChanged();

                            rcaktifitas.setLayoutManager(new LinearLayoutManager(Aktivitas.this));
                            rcaktifitas.setItemAnimator(new DefaultItemAnimator());
                            rcaktifitas.setAdapter(mAdapter);

                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(Aktivitas.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(Aktivitas.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("kyano", iduser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Aktivitas.this);
        requestQueue.add(stringRequest);
    }
}
