package com.app.mobiledev.salesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class login extends AppCompatActivity {
    EditText edemail,edpassword;
    Button btncek;
    private SweetAlertDialog mProgressDialog;
    LinearLayout panelemail;
    SessionManager sesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sesi = new SessionManager(this);

        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#FF5722"));
        mProgressDialog.setTitleText("Loading");
        panelemail = findViewById(R.id.panelemal);
        edemail = findViewById(R.id.edemail);
        edpassword = findViewById(R.id.edpassword);
        btncek = findViewById(R.id.btncek);

        btncek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cek_user();
            }
        });
    }
    private void cek_user() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        String username="";
                        String id_user="";
                        String nama="";
                        String kodearea="";
                        String area="";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject row = jsonArray.getJSONObject(i);
                            username = row.getString("username");
                            id_user = row.getString("id_pegawai");
                            kodearea = row.getString("kode_area");
                            area = row.getString("area");
                            nama = row.getString("nama_pegawai");
                        }
                        sesi.createSession(id_user,username,nama,kodearea,area);
                        Intent intent = new Intent(login.this, main_fragment.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String data = jsonObject.getString("data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(login.this, "Koneksi ke server terganggu", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(login.this, "Internet lambat", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", edemail.getText().toString());
                params.put("password", edpassword.getText().toString());
                params.put("key", api.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        new SweetAlertDialog(login.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Konfirmasi keluar")
                .setContentText("Yakin keluar aplikasi?")
                .setConfirmText("Ya, keluar")
                .setCancelText("Tidak")
                .setConfirmClickListener(sweetAlertDialog -> {
                    finish();
                    moveTaskToBack(true);
                }).show();
    }
}
