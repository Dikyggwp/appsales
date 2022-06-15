package com.app.mobiledev.salesapp.unplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.app.mobiledev.salesapp.riwayat_absen.adapterRiwayatAbsen;
import com.app.mobiledev.salesapp.riwayat_absen.modelRiwayatAbsen;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UnplanActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText tgl1;
    private EditText tgl2;
    private ImageButton btnCalender1;
    private ImageButton btnCalender2;
    private List<UnplanMdl> modelRiwayatAbsens;
    private RecyclerView rcRiwayatAbsen;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    SessionManager sesi;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unplan);
        btnCalender1=findViewById(R.id.btnCalender1);
        btnCalender2=findViewById(R.id.btnCalender2);
        tgl1=findViewById(R.id.tgl1);
        tgl2=findViewById(R.id.tgl2);
        tgl1.setInputType(InputType.TYPE_NULL);
        tgl2.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        modelRiwayatAbsens = new ArrayList<>();
        rcRiwayatAbsen=findViewById(R.id.rcRiwayatAbsen);
        mProgressDialog = new ProgressDialog(UnplanActivity.this);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.show();
        sesi = new SessionManager(UnplanActivity.this);
        userid = sesi.getIDUser();
        btnCalender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                showDateDialog1();
            }
        });

        btnCalender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                showDateDialog2();
            }
        });
        getDataUnplan();
    }
    private void showDateDialog1(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(UnplanActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl1.setText(""+dateFormatter.format(newDate.getTime()));
                if(tgl1.equals(null)||tgl1.equals("")){
                    helper.showMsg(UnplanActivity.this,"informasi","bulan selesai belum dipilih",helper.ERROR_TYPE);
                }else{
                    String tgl_mulai=tgl1.getText().toString();
                    String tgl_selesai=tgl2.getText().toString();
                    getData(tgl_mulai,tgl_selesai);
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showDateDialog2(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(UnplanActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl1.setText(""+dateFormatter.format(newDate.getTime()));
                if(tgl1.equals(null)||tgl1.equals("")){
                    helper.showMsg(UnplanActivity.this,"informasi","bulan selesai belum dipilih",helper.ERROR_TYPE);
                }else{
                    String tgl_mulai=tgl1.getText().toString();
                    String tgl_selesai=tgl2.getText().toString();
                    getData(tgl_mulai,tgl_selesai);
                }
                Log.d("SET_TGL2", "onDateSet: "+dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private  void getData(final String tgl_mulai,final String tgl_selesai) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_unplan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(UnplanActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    modelRiwayatAbsens.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);

                            UnplanMdl model = new UnplanMdl();
                            String alasan=data.getString("alasan_unplan");
                            String tgl=data.getString("tgl_unplan");
                            String outlet=data.getString("outlet_unplan");
                            model.setOutlet(outlet);
                            model.setAlamat(alasan);
                            model.setTgl(tgl);
                            modelRiwayatAbsens.add(model);
                        }
                        Unplanadp mAdapter;
                        mAdapter = new Unplanadp(modelRiwayatAbsens, UnplanActivity.this);
                        mAdapter.notifyDataSetChanged();
                        rcRiwayatAbsen.setLayoutManager(new LinearLayoutManager(UnplanActivity.this));
                        rcRiwayatAbsen.setItemAnimator(new DefaultItemAnimator());
                        rcRiwayatAbsen.setAdapter(mAdapter);
                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSONERORABSEN", "onResponse: "+e);
                    helper.showMsg(UnplanActivity.this, "Peringatan", ""+helper.PESAN_SERVER, helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
                mProgressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Log.d("DATA_JSONEXCEPION", "onResponse: " + error);
                        helper.showMsg(UnplanActivity.this, "Peringatan", "" + helper.PESAN_KONEKSI, helper.ERROR_TYPE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tgl1", tgl_mulai);
                params.put("tgl2", tgl_selesai);
                params.put("id_user", userid);
                params.put("key", api.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(UnplanActivity.this);
        requestQueue.add(stringRequest);
    }

    public String getMonthNow(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM");
        String formattedDate = df.format(c);
        return  formattedDate;
    }

    public String getDateNow(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        return  formattedDate;
    }

    public void getDataUnplan()  {
        int bulan= Integer.parseInt(getMonthNow());
        Calendar gc = new GregorianCalendar();
        gc.set(Calendar.MONTH, bulan-1);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = gc.getTime();
        gc.add(Calendar.MONTH, 1);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        Date monthEnd = gc.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        tgl1.setText(format.format(monthStart));
        tgl2.setText( getDateNow());
        getData(format.format(monthStart),getDateNow());
    }
}