package com.app.mobiledev.salesapp.outlet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.aktifitas.Aktivitas;
import com.app.mobiledev.salesapp.aktifitas.aktifitasAdapter;
import com.app.mobiledev.salesapp.aktifitas.aktifitasModel;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.plan.PlanActivity;
import com.app.mobiledev.salesapp.po.PO;
import com.app.mobiledev.salesapp.po.modalProduk;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Outlet extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView rcoutlet;
    private List<OutletMdl> mListData;
    SessionManager sesi;
    String userid,kodearea;
    EditText edcari;
    TextView tvadd;
    ModalAddOutlet ModalAddOutlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        ModalAddOutlet = new ModalAddOutlet();
        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        kodearea = sesi.getKodeArea();

        tvadd=findViewById(R.id.tvadd);
        edcari=findViewById(R.id.edcari);
        rcoutlet=findViewById(R.id.rcoutlet);

        mListData = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(Outlet.this);
        rcoutlet.setLayoutManager(layoutManager);
        edcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                get_outlet();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        tvadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_modal_add();
            }
        });
        get_outlet();
    }

    private void show_modal_add() {
        ModalAddOutlet.ctx = Outlet.this;
        Bundle bundl = new Bundle();
        bundl.putString("new","ModalAddOutlet");
        ModalAddOutlet.setArguments(bundl);
        ModalAddOutlet.show(getSupportFragmentManager(), "ModalAddOutlet");
    }

    public void get_outlet() {
        //mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mListData.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            OutletMdl model = new OutletMdl();
                            String nama =data.getString("outlet");
                            String alamat =data.getString("alamat");
                            model.setNama(nama);
                            model.setAlamat(alamat);
                            mListData.add(model);
                        }
                        OutletAdp mAdapter;
                        mAdapter = new OutletAdp(mListData, Outlet.this);
                        mAdapter.notifyDataSetChanged();

                        rcoutlet.setLayoutManager(new LinearLayoutManager(Outlet.this));
                        rcoutlet.setItemAnimator(new DefaultItemAnimator());
                        rcoutlet.setAdapter(mAdapter);

                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    //mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(Outlet.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mProgressDialog.dismiss();
                Toasty.error(Outlet.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("userid", userid);
                params.put("kodearea", kodearea);
                params.put("keyword", edcari.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Outlet.this);
        requestQueue.add(stringRequest);
    }
}