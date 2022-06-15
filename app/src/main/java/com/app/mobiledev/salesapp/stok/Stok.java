package com.app.mobiledev.salesapp.stok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.outlet.ModalAddOutlet;
import com.app.mobiledev.salesapp.outlet.Outlet;
import com.app.mobiledev.salesapp.outlet.OutletAdp;
import com.app.mobiledev.salesapp.outlet.OutletMdl;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Stok extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView rvstok;
    private List<StokMdl> mListData;
    SessionManager sesi;
    String userid;
    EditText edcari;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.show();

        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        edcari=findViewById(R.id.edcari);
        rvstok=findViewById(R.id.rvstok);

        mListData = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(Stok.this);
        rvstok.setLayoutManager(layoutManager);
        edcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                get_stok();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        get_stok();
    }
    
    public void get_stok() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_stok, new Response.Listener<String>() {
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
                            StokMdl model = new StokMdl();
                            String kdbrg =data.getString("Kode_Barang");
                            String brg =data.getString("Nama_Barang");
                            String gd =data.getString("gud");
                            String stk =data.getString("Tersedia");
                            String hrg =data.getString("Harga_Satuan");
                            model.setNama(brg);
                            model.setKode(kdbrg);
                            model.setStok(stk);
                            model.setHrg(hrg);
                            model.setGdg(gd);
                            mListData.add(model);
                        }
                        StokAdp mAdapter;
                        mAdapter = new StokAdp(mListData, Stok.this);
                        mAdapter.notifyDataSetChanged();

                        rvstok.setLayoutManager(new LinearLayoutManager(Stok.this));
                        rvstok.setItemAnimator(new DefaultItemAnimator());
                        rvstok.setAdapter(mAdapter);

                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(Stok.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(Stok.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("keyword", edcari.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Stok.this);
        requestQueue.add(stringRequest);
    }
}