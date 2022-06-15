package com.app.mobiledev.salesapp.kunjungan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.plan.PlanActivity;
import com.app.mobiledev.salesapp.po.PO;
import com.app.mobiledev.salesapp.riwayat_absen.modelRiwayatAbsen;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.app.mobiledev.salesapp.helper.showMsg;

public class UnplanAdd extends AppCompatActivity {

    private List<modelRiwayatAbsen> modelRiwayatAbsens;
    private RecyclerView rcRiwayatAbsen;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    SessionManager sesi;
    String userid,id_plan,outlet,kodearea;
    EditText edalasan;
    Button btnproses,btnno,btnyes;
    LinearLayout lnkunjungan;
    AutoCompleteTextView edoutlet;
    private ArrayList<String> salesList;
    TextView tvidout,tvalamat,tvoutletold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unplan_add);

        salesList = new ArrayList<>();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");
        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        kodearea = sesi.getKodeArea();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                outlet= null;
                id_plan= null;
            } else {
                outlet= extras.getString("outlet");
                id_plan= extras.getString("id");
            }
        } else {
            outlet= (String) savedInstanceState.getSerializable("outlet");
            id_plan= (String) savedInstanceState.getSerializable("id");
        }
        //Toast.makeText(this, id_plan+outlet, Toast.LENGTH_SHORT).show();

        tvoutletold=findViewById(R.id.tvoutletold);
        tvalamat=findViewById(R.id.tvalamat);
        tvidout=findViewById(R.id.tvidout);
        lnkunjungan=findViewById(R.id.lnkunjungan);
        btnyes=findViewById(R.id.btnyes);
        btnno=findViewById(R.id.btnno);
        edalasan=findViewById(R.id.edalasan);
        edoutlet=findViewById(R.id.edoutlet);
        btnproses=findViewById(R.id.btnproses);
        tvoutletold.setText(outlet);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edoutlet.setText("");
                edoutlet.setEnabled(true);
                if (tvidout.getText().toString().equals("") || tvidout.getText().toString().equals("")) {
                    Toast.makeText(UnplanAdd.this, "Outlet belum dipilih", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(UnplanAdd.this, kunjunganUnplan.class);
                    intent.putExtra("outlet",edoutlet.getText().toString());
                    intent.putExtra("idoutlet",tvidout.getText().toString());
                    intent.putExtra("alasan",edalasan.getText().toString());
                    intent.putExtra("id",id_plan);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edoutlet.setText("NO OUTLET");
                tvidout.setText("");
                tvalamat.setText("");
                edoutlet.setEnabled(false);
            }
        });
        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(edalasan.getText().toString().equals("")) {
                    helper.showMsg(UnplanAdd.this,"Peringatan","Alasan Wajib Diisi",1);
                } else if(tvidout.getText().toString().equals("") || edoutlet.getText().toString().equals("")) {
                    helper.showMsg(UnplanAdd.this,"Peringatan","Outlet Wajib Diisi",1);
                }else{
                   Intent intent = new Intent(UnplanAdd.this, kunjunganUnplan.class);
                   intent.putExtra("outlet",edoutlet.getText().toString());
                   intent.putExtra("idoutlet",tvidout.getText().toString());
                   intent.putExtra("alasan",edalasan.getText().toString());
                   intent.putExtra("id",id_plan);
                   startActivity(intent);
                   finish();
                }

            }
        });
        edoutlet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    get_outlet_by(edoutlet.getText().toString());
                }
                return false;
            }
        });
        edoutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                get_outlet_by(edoutlet.getText().toString());
            }
        });
        get_outletall();
    }
    private void get_outlet_by(final String kode) {
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
                        showMsg(UnplanAdd.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
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
        Volley.newRequestQueue(UnplanAdd.this).add(stringRequest);
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
                        showMsg(UnplanAdd.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (UnplanAdd.this, android.R.layout.select_dialog_item, salesList);
                    edoutlet.setThreshold(1);
                    edoutlet.setAdapter(adapter);
                    edoutlet.setTextColor(Color.BLACK);
                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    showMsg(UnplanAdd.this, "Error", "Sales tidak ditemukan: \n Error :" + e.getMessage(), helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMsg(UnplanAdd.this, "Pesan", "onErrorResponse: " + error);
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
        Volley.newRequestQueue(UnplanAdd.this).add(stringRequest);

    }
    private void simpan_po() {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.simpan_unplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(UnplanAdd.this,""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(UnplanAdd.this,""+data, Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(UnplanAdd.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(UnplanAdd.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                    params.put("userid", userid);
                params.put("id_plan", id_plan);
                params.put("idoutlet", edoutlet.getText().toString());
                params.put("alasan", edalasan.getText().toString());
                params.put("outlet", edoutlet.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(UnplanAdd.this);
        requestQueue.add(stringRequest);
    }
}