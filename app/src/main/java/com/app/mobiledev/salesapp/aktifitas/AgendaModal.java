package com.app.mobiledev.salesapp.aktifitas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class AgendaModal extends DialogFragment {
    Context ctx;
    Button btnsubmit;
    private SweetAlertDialog mProgressDialog;
    private EditText edaktifitas;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String SHARED_PREF_NAME = "com.app.mobiledev.salesapp";
    private String SP_IDUSER = "iduser";
    private String email,nama,iduser;
    private TextView btnfoto,btnmaps,btnlink,btnclose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        iduser = sp.getString(SP_IDUSER, "");
        mProgressDialog = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#FF6130"));
        mProgressDialog.setTitleText("Loading");
        mProgressDialog.setCancelable(true);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.modal_logbook, container, false);
        return view;
    }
    private void refresh_data() {
        //((Aktivitas)this.getActivity()).get_aktifitas();
    }

    private void send_aktivitas() {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.add_produk, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(ctx,""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(ctx,""+data, Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                    edaktifitas.setText("");
                    dismiss();
                    refresh_data();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(ctx, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("kyano", iduser);
                params.put("status", edaktifitas.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
}
