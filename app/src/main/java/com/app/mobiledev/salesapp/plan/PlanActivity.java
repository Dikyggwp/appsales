package com.app.mobiledev.salesapp.plan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.aktifitas.Logbook;
import com.app.mobiledev.salesapp.aktifitas.LogbookAdapter;
import com.app.mobiledev.salesapp.aktifitas.LogbookModel;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.app.mobiledev.salesapp.helper.showMsg;

public class PlanActivity extends AppCompatActivity {

    Button btnproses;
    ImageButton btnCalender;
    SessionManager sesi;
    String userid,kodearea;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText edtgl;
    RecyclerView rvplan,rvDate;
    private ProgressDialog mProgressDialog;
    private List<PlanMdl> mListData;
    private List<PlanDateMdl> listDate;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout pnlnotinput,pnlinput;
    AutoCompleteTextView edoutlet;
    private ArrayList<String> salesList;
    TextView tvalamat,tvidout,tvmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        pnlnotinput=findViewById(R.id.pnlnotinput);
        pnlinput=findViewById(R.id.pnlinput);
        tvmode=findViewById(R.id.tvmode);
        tvidout=findViewById(R.id.tvidout);
        tvalamat=findViewById(R.id.tvalamat);
        salesList = new ArrayList<>();
        pnlnotinput.setVisibility(View.VISIBLE);
        pnlinput.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        kodearea = sesi.getKodeArea();

        mSwipeRefreshLayout=findViewById(R.id.swiperefresh);
        rvplan=findViewById(R.id.rvplan);
        rvDate=findViewById(R.id.rvDate);
        edtgl=findViewById(R.id.edtgl);
        edoutlet=findViewById(R.id.edoutlet);
        btnCalender=findViewById(R.id.btnCalender);
        btnproses=findViewById(R.id.btnproses);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        edtgl.setEnabled(false);

        mListData = new ArrayList<>();
        listDate = new ArrayList<>();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(PlanActivity.this);
        rvplan.setLayoutManager(layoutManager);

        final LinearLayoutManager ld = new LinearLayoutManager(PlanActivity.this);
        rvDate.setLayoutManager(ld);

        mSwipeRefreshLayout .setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                cek_plan();
                getDataDate();
                getData("");
            }
        });

        edtgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTgl();
            }
        });
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTgl();
            }
        });
        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edoutlet.getText().toString().equals("") || edtgl.getText().toString().equals("") || tvalamat.getText().toString().equals("")){
                    Toast.makeText(PlanActivity.this, "Outlet belum diisi", Toast.LENGTH_SHORT).show();
                }else{
//                    if (checkInternet()){
//                        Toast.makeText(PlanActivity.this, "MODE OFFLINE...Data Disimpan didalam perangkat", Toast.LENGTH_SHORT).show();
//                    }else {
                        simpan_plan();
                    //}
                }
            }
        });
        edoutlet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    get_produk_by(edoutlet.getText().toString());
                }
                return false;
            }
        });
        edoutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                get_produk_by(edoutlet.getText().toString());
            }
        });
        getDataDate();
        cek_plan();
        get_outletall();
        tvmode.setVisibility(View.GONE);

        if (checkInternet()){
            tvmode.setVisibility(View.GONE);
        }else{
            pnlnotinput.setVisibility(View.GONE);
            pnlinput.setVisibility(View.VISIBLE);

            tvmode.setVisibility(View.VISIBLE);
            tvmode.setText("MODE OFFLINE");
            tvmode.setBackgroundColor(getResources().getColor(R.color.btnDanger));
        }
    }
    public boolean checkInternet(){
        boolean connectStatus;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= ConnectionManager != null ? ConnectionManager.getActiveNetworkInfo() : null;
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }

    private void get_produk_by(final String kode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet_byname, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("data");
                    JSONObject c = result.getJSONObject(0);
                    String kode = c.getString("id_outlet");
                    String almt = c.getString("alamat");
                    String nama = c.getString("outlet");
                    salesList.add(""+kode+""+nama);
                    if (kode.trim().equals("null") || kode.trim().equals("")){
                        showMsg(PlanActivity.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }else{
                        tvalamat.setText(almt);
                        tvidout.setText(kode);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", kode);
                params.put("kodearea", kodearea);
                params.put("key", api.key);
                return params;
            }
        };
        Volley.newRequestQueue(PlanActivity.this).add(stringRequest);
    }
    private void get_outletall(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            mProgressDialog.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String namaSales="";
                    Boolean cek=jsonObject.getBoolean("success");
                    if (cek) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objectData = jsonArray.getJSONObject(i);
                            namaSales = objectData.getString("outlet");
                            salesList.add(""+namaSales);
                        }
                    }
                    else{
                        showMsg(PlanActivity.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (PlanActivity.this, android.R.layout.select_dialog_item, salesList);
                    edoutlet.setThreshold(1);
                    edoutlet.setAdapter(adapter);
                    edoutlet.setTextColor(Color.BLACK);
                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //showMsg(PlanActivity.this, "Error", "Sales tidak ditemukan: \n Error :" + e.getMessage(), helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showMsg(PlanActivity.this, "Pesan", "onErrorResponse: " + error);
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("kodearea", kodearea);
                return params;
            }
        };
        Volley.newRequestQueue(PlanActivity.this).add(stringRequest);

    }

    private  void cek_plan() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.cek_inputplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String sabtu = jsonObject.getString("sabtu");
                        String jumat = jsonObject.getString("jumat");
                        String tgl = jsonObject.getString("tgl");

                        if (sabtu.equals(tgl) || jumat.equals(tgl)){
                            pnlnotinput.setVisibility(View.GONE);
                            pnlinput.setVisibility(View.VISIBLE);
                        }else{
                            pnlnotinput.setVisibility(View.VISIBLE);
                            pnlinput.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(PlanActivity.this, ""+helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(PlanActivity.this, ""+helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tgl", edtgl.getText().toString());
                params.put("id_user", userid);
                params.put("outlet", edoutlet.getText().toString());
                params.put("key", api.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PlanActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showTgl(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(PlanActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtgl.setText(""+dateFormatter.format(newDate.getTime()));
                if(edtgl.equals(null)||edtgl.equals("")){

                }else{
                    edtgl.getText().toString();
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private  void simpan_plan() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.simpan_plan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String data = jsonObject.getString("pesan");
                    if (success) {
                        tvalamat.setText("");
                        edoutlet.setText("");

                        new SweetAlertDialog(PlanActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Informasi")
                                .setContentText(""+data)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    getDataDate();
                                    sweetAlertDialog.dismiss();
                                })
                                .show();
                    }else{
                        helper.showMsg(PlanActivity.this,"Informasi",""+data,helper.WARNING_TYPE);
                    }
                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSONERORABSEN", "onResponse: "+e);
                    helper.showMsg(PlanActivity.this, "Peringatan", ""+helper.PESAN_SERVER, helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
                mProgressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        helper.showMsg(PlanActivity.this, "Peringatan", "" + helper.PESAN_KONEKSI, helper.ERROR_TYPE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tgl", edtgl.getText().toString());
                params.put("id_user", userid);
                params.put("outlet", edoutlet.getText().toString());
                params.put("id_outlet", tvidout.getText().toString());
                params.put("key", api.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PlanActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getDataDate(){
        mProgressDialog.show();
        final StringRequest request = new StringRequest(Request.Method.POST, api.get_plan_inweek,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(PlanActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        iniDataDate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        //Toast.makeText(PlanActivity.this, "VolleyError"+error.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void iniDataDate(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                listDate.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectData = jsonArray.getJSONObject(i);
                    String tgl = objectData.getString("jadwal");
                    PlanDateMdl PlanDateMdl = new PlanDateMdl();
                    PlanDateMdl.setTgl(tgl);
                    listDate.add(PlanDateMdl);
                }
                PlanDateAdp mAdapter = new PlanDateAdp(listDate, PlanActivity.this);
                mAdapter.notifyDataSetChanged();
                rvDate.setLayoutManager(new LinearLayoutManager(PlanActivity.this, LinearLayoutManager.HORIZONTAL,false));
                rvDate.setItemAnimator(new DefaultItemAnimator());
                rvDate.setAdapter(mAdapter);

            } else {
               // Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }

    public void getData(final String tgl){
        mProgressDialog.show();
        final StringRequest request = new StringRequest(Request.Method.POST, api.get_plan_bydate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       //Toast.makeText(PlanActivity.this, ""+tgl, Toast.LENGTH_SHORT).show();
                        iniData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        //Toast.makeText(PlanActivity.this, "VolleyError"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_user", userid);
                params.put("tgl", tgl);
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
                    String alamat = objectData.getString("alamat");
                    String outlet = objectData.getString("outlet");
                    String tgl = objectData.getString("jadwal");
                    PlanMdl PlanMdl = new PlanMdl();
                    PlanMdl.setOutlet(outlet);
                    PlanMdl.setTgl(tgl);
                    PlanMdl.setAlamat(alamat);
                    mListData.add(PlanMdl);
                }
                PlanAdapter mAdapter = new PlanAdapter(mListData, PlanActivity.this);
                rvplan.setAdapter(mAdapter);
            } else {
               // Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }
}