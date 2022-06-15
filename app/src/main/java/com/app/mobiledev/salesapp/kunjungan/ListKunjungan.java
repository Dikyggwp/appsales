package com.app.mobiledev.salesapp.kunjungan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.plan.PlanActivity;
import com.app.mobiledev.salesapp.plan.PlanAdapter;
import com.app.mobiledev.salesapp.plan.PlanMdl;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListKunjungan extends AppCompatActivity {

    SessionManager sesi;
    String userid;
    RecyclerView rvplan;
    private ProgressDialog mProgressDialog;
    private List<kunjunganMdl> mListData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView tvmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kunjungan);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");

        sesi = new SessionManager(this);
        userid = sesi.getIDUser();

        rvplan=findViewById(R.id.rvplan);
        tvmode=findViewById(R.id.tvmode);
        mSwipeRefreshLayout=findViewById(R.id.swiperefresh);
        mListData = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ListKunjungan.this);
        rvplan.setLayoutManager(layoutManager);

        getData();
        mSwipeRefreshLayout .setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                getData();
            }
        });
        if (checkInternet()){
            tvmode.setVisibility(View.GONE);
        }else{
            tvmode.setVisibility(View.VISIBLE);
            tvmode.setText("MODE OFFLINE");
            tvmode.setBackgroundColor(getResources().getColor(R.color.btnDanger));
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        getData();
    }
    public boolean checkInternet(){
        boolean connectStatus;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= ConnectionManager != null ? ConnectionManager.getActiveNetworkInfo() : null;
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }
    public void getData(){
        mProgressDialog.show();
        final StringRequest request = new StringRequest(Request.Method.POST, api.get_plan_today,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ListKunjungan.this, "VolleyError"+response, Toast.LENGTH_SHORT).show();
                        iniData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        //Toast.makeText(ListKunjungan.this, "VolleyError"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_user", userid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private void iniData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mListData.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectData = jsonArray.getJSONObject(i);
                    String tgl = objectData.getString("jadwal");
                    String outlet = objectData.getString("outlet");
                    String outlet_unplan = objectData.getString("outlet_unplan");
                    String stts = objectData.getString("status");
                    String id_agenda = objectData.getString("id_agenda");
                    String alsn = objectData.getString("alasan_unplan");
                    kunjunganMdl mdl = new kunjunganMdl();
                    mdl.setOutlet(outlet);
                    mdl.setOutlet_up(outlet_unplan);
                    mdl.setAlasan_unplan(alsn);
                    mdl.setTgl(tgl);
                    mdl.setStatus(stts);
                    mdl.setId(id_agenda);
                    mListData.add(mdl);
                }
                kunjunganAdp mAdapter = new kunjunganAdp(mListData, ListKunjungan.this, ListKunjungan.this);
                rvplan.setAdapter(mAdapter);
            } else {
                //Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }
}