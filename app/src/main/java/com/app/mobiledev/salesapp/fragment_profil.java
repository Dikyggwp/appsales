package com.app.mobiledev.salesapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.outlet.OutletMdl;
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class fragment_profil extends Fragment {

    public fragment_profil(){}
    private View rootView;
    Button btnlogout,btnsandi;
    SessionManager sesi;
    TextView tvuser,tvemail;
    String nama,area,userid,kodearea;
    private ProgressDialog loading;
    EditText password,password2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.activity_profil, container, false);

        loading = new ProgressDialog(getActivity());
        loading.setMessage("Loading ...");

        sesi = new SessionManager(getActivity());
        sesi = new SessionManager(getActivity());
        userid = sesi.getIDUser();
        nama = sesi.getUser();
        area = sesi.getArea();
        kodearea = sesi.getKodeArea();
        password=rootView.findViewById(R.id.password);
        password2=rootView.findViewById(R.id.password2);
        btnsandi=rootView.findViewById(R.id.btnsandi);
        tvuser=rootView.findViewById(R.id.tvuser);
        tvemail=rootView.findViewById(R.id.tvemail);
        tvuser.setText(nama);
        tvemail.setText(area);
        btnlogout=rootView.findViewById(R.id.btnlogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sesi.logout();
                getActivity().finish();
            }
        });
        btnsandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gantipass();
            }
        });
        return rootView;
    };
    private void gantipass() {
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.ganti_password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(getActivity(),""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(getActivity(),""+data, Toast.LENGTH_SHORT).show();
                    }
                    password.setText("");
                    password2.setText("");
                    loading.dismiss();
                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                    Toasty.error(getActivity(), "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toasty.error(getActivity(), "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_user", userid);
                params.put("password", password.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}