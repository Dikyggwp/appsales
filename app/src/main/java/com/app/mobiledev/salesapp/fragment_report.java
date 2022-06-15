package com.app.mobiledev.salesapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class fragment_report extends Fragment {
    public fragment_report(){}
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText tgl1;
    private EditText tgl2;
    private ImageButton btnCalender1;
    private ImageButton btnCalender2;
    private List<modelRiwayatAbsen> modelRiwayatAbsens;
    private View rootView;
    private RecyclerView rcRiwayatAbsen;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    SessionManager sesi;
    String userid;


    private String email,nama,iduser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.activity_fragment_report, container, false);
        btnCalender1=rootView.findViewById(R.id.btnCalender1);
        btnCalender2=rootView.findViewById(R.id.btnCalender2);
        tgl1=rootView.findViewById(R.id.tgl1);
        tgl2=rootView.findViewById(R.id.tgl2);
        tgl1.setInputType(InputType.TYPE_NULL);
        tgl2.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        modelRiwayatAbsens = new ArrayList<>();
        rcRiwayatAbsen=rootView.findViewById(R.id.rcRiwayatAbsen);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.show();
        sesi = new SessionManager(getActivity());
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
        getRiwayatabsenNow();
        return rootView;
    };

    private void showDateDialog1(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl1.setText(""+dateFormatter.format(newDate.getTime()));
                if(tgl1.equals(null)||tgl1.equals("")){
                    helper.showMsg(getActivity(),"informasi","bulan selesai belum dipilih",helper.ERROR_TYPE);
                }else{
                    String tgl_mulai=tgl1.getText().toString();
                    String tgl_selesai=tgl2.getText().toString();
                    riwayat_po(tgl_mulai,tgl_selesai);
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showDateDialog2(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tgl1.setText(""+dateFormatter.format(newDate.getTime()));
                if(tgl1.equals(null)||tgl1.equals("")){
                    helper.showMsg(getActivity(),"informasi","bulan selesai belum dipilih",helper.ERROR_TYPE);
                }else{
                    String tgl_mulai=tgl1.getText().toString();
                    String tgl_selesai=tgl2.getText().toString();
                    riwayat_po(tgl_mulai,tgl_selesai);
                }
                Log.d("SET_TGL2", "onDateSet: "+dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private  void riwayat_po(final String tgl_mulai,final String tgl_selesai) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_po, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    modelRiwayatAbsens.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            modelRiwayatAbsen model = new modelRiwayatAbsen();
                            String marketing=data.getString("marketing");
                            String tgl=data.getString("tanggal");
                            String outlet=data.getString("outlet");
                            String nopo=data.getString("po");
                            model.setOutlet(outlet);
                            model.setMarketing(marketing);
                            model.setTgl(tgl);
                            model.setNopo(nopo);
                            modelRiwayatAbsens.add(model);
                        }
                        adapterRiwayatAbsen mAdapter;
                        mAdapter = new adapterRiwayatAbsen(modelRiwayatAbsens, getActivity());
                        mAdapter.notifyDataSetChanged();
                        rcRiwayatAbsen.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rcRiwayatAbsen.setItemAnimator(new DefaultItemAnimator());
                        rcRiwayatAbsen.setAdapter(mAdapter);
                    }else{
                        Log.d("DATA_BOOLEAN", "onResponse: "+success);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSONERORABSEN", "onResponse: "+e);
                    helper.showMsg(getActivity(), "Peringatan", ""+helper.PESAN_SERVER, helper.ERROR_TYPE);
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
                        helper.showMsg(getActivity(), "Peringatan", "" + helper.PESAN_KONEKSI, helper.ERROR_TYPE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tgl_mulai", tgl_mulai);
                params.put("tgl_selesai", tgl_selesai);
                params.put("id_user", userid);
                params.put("key", api.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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

    public void getRiwayatabsenNow()  {
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
        riwayat_po(format.format(monthStart),getDateNow());
    }



}
