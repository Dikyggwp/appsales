package com.app.mobiledev.salesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.aktifitas.Logbook;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.cek_gps.GpsUtils;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.kunjungan.ListKunjungan;
import com.app.mobiledev.salesapp.kunjungan.kunjungan;
import com.app.mobiledev.salesapp.outlet.Outlet;
import com.app.mobiledev.salesapp.plan.PlanActivity;
import com.app.mobiledev.salesapp.po.PO;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.app.mobiledev.salesapp.so.SO;
import com.app.mobiledev.salesapp.stok.Stok;
import com.app.mobiledev.salesapp.stok.StokAdp;
import com.app.mobiledev.salesapp.stok.StokMdl;
import com.app.mobiledev.salesapp.unplan.UnplanActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class fragment_home extends Fragment {

    public fragment_home(){ }
    private FragmentManager fragmentManager;
    private boolean isGPS = false;
    private String nama,iduser;
    private LinearLayout btnplannew,btnunplan,btnlistplan,btnkunjungan,btnlistoutlet,btnpo,btnstok,btnso;
    private ProgressDialog mProgressDialog;
    private View rootView;
    Fragment fragment = null;
    SessionManager sesi;
    TextView tvunplan,tvplan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.activity_fragment_activity, container, false);

        sesi = new SessionManager(getActivity());
        iduser = sesi.getIDUser();
        nama = sesi.getUser();

        tvunplan=rootView.findViewById(R.id.tvunplan);
        tvplan=rootView.findViewById(R.id.tvplan);
        btnstok=rootView.findViewById(R.id.btnstok);
        btnso=rootView.findViewById(R.id.btnso);
        btnpo=rootView.findViewById(R.id.btnpo);
        btnplannew=rootView.findViewById(R.id.btnplannew);
        btnunplan=rootView.findViewById(R.id.btnunplan);
        btnlistplan=rootView.findViewById(R.id.btnlistplan);
        btnkunjungan=rootView.findViewById(R.id.btnkunjungan);
        btnlistoutlet=rootView.findViewById(R.id.btnlistoutlet);

        btnstok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Stok.class));
            }
        });
        btnso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SO.class));
            }
        });
        btnunplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UnplanActivity.class));
            }
        });
        btnlistplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Logbook.class));
            }
        });
        btnkunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListKunjungan.class));
            }
        });
        btnlistoutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Outlet.class));
            }
        });
        btnpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PO.class));
            }
        });        btnplannew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PlanActivity.class));
            }
        });
        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });
        helper.requestPermissionsGps(getActivity());
        get_performa();
        return rootView;
    };
    public void get_performa() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_performa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String pl = jsonObject.getString("plan");
                        String unpl = jsonObject.getString("unplan");
                        tvplan.setText(pl);
                        tvunplan.setText(unpl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("iduser", iduser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
