package com.app.mobiledev.salesapp.so;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.app.mobiledev.salesapp.stok.Stok;
import com.app.mobiledev.salesapp.stok.StokAdp;
import com.app.mobiledev.salesapp.stok.StokMdl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SO extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView rvstok;
    private List<StokMdl> mListData;
    SessionManager sesi;
    String userid;
    EditText edcari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");

        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        edcari=findViewById(R.id.edcari);
        rvstok=findViewById(R.id.rvstok);

        mListData = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(SO.this);
        rvstok.setLayoutManager(layoutManager);

        get_so();
    }

    public void get_so() {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_po, new Response.Listener<String>() {
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
                            String nama =data.getString("outlet");
                            String alamat =data.getString("alamat");
                            String total =data.getString("total");
                            model.setNama(nama);
                            model.setKode(alamat);
                            model.setStok(total);
                            mListData.add(model);
                        }
                        StokAdp mAdapter;
                        mAdapter = new StokAdp(mListData, SO.this);
                        mAdapter.notifyDataSetChanged();

                        rvstok.setLayoutManager(new LinearLayoutManager(SO.this));
                        rvstok.setItemAnimator(new DefaultItemAnimator());
                        rvstok.setAdapter(mAdapter);

                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(SO.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(SO.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("userid", userid);
                params.put("keyword", edcari.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SO.this);
        requestQueue.add(stringRequest);
    }
}