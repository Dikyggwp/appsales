package com.app.mobiledev.salesapp.po;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

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
import com.app.mobiledev.salesapp.sesion.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.app.mobiledev.salesapp.helper.rp;
import static com.app.mobiledev.salesapp.helper.showMsg;

public class modalProduk extends DialogFragment {

    Context ctx;
    private SessionManager sessionmanager;
    private ProgressDialog loading;
    Button btnbatal,btnadd;
    String userid;
    EditText edjml,edhrg,eddiskon;
    AutoCompleteTextView edproduk;
    private ArrayList<String> salesList;
    TextView tvidproduk,tvnamaproduk,txttotal;
    Switch sppn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionmanager = new SessionManager(ctx);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
           // dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.modal_addproduk, container, false);
        sessionmanager = new SessionManager(ctx);
        userid = sessionmanager.getIDUser();
        salesList = new ArrayList<>();
        loading = new ProgressDialog(ctx);
        loading.setMessage("Loading ...");
        sppn=view.findViewById(R.id.sppn);

        txttotal = view.findViewById(R.id.txttotal);
        tvidproduk = view.findViewById(R.id.tvidproduk);
        tvnamaproduk = view.findViewById(R.id.tvnamaproduk);
        edproduk = view.findViewById(R.id.edproduk);
        eddiskon = view.findViewById(R.id.eddiskon);
        edhrg = view.findViewById(R.id.edharga);
        edjml = view.findViewById(R.id.edjml);

        btnadd = view.findViewById(R.id.btnadd);
        btnbatal = view.findViewById(R.id.btnbatal);

        edjml.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                hitung();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        eddiskon.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                hitung();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        edproduk.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    get_produk_by(edproduk.getText().toString());
                }
                return false;
            }
        });
        edproduk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                get_produk_by(edproduk.getText().toString());
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                addproduk();
            }
        });
        btnbatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                refresh_data();
            }
        });
        get_produkall();
        edproduk.setText("");
        edproduk.setText("");
        edjml.setText("");
        eddiskon.setText("");
        edhrg.setText("");
        return view;
    }
    private void refresh_data() {
        ((PO)ctx).get_produk();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ((PO)ctx).get_produk();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void hitung() {
        try {
            if (eddiskon.getText().toString().equals("")) {
                eddiskon.setText("0");
            } else {
                Double qty = Double.parseDouble(edjml.getText().toString());
                Double hrg = Double.parseDouble(edhrg.getText().toString().replace(",", ""));
                Double dis = Double.parseDouble(eddiskon.getText().toString());
                Double jml = qty * hrg;

                Double tot = jml * dis / 100;
                Double totdis = jml - tot;
                txttotal.setText(rp(totdis));
            }
        }catch (Exception e){

        }
    }

    private void addproduk() {
        loading.show();
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

                    loading.dismiss();
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
                loading.dismiss();
                Toasty.error(ctx, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String ppn = ""+sppn.isChecked();
                params.put("key", api.key);
                params.put("barang", edproduk.getText().toString());
                params.put("id_barang", edproduk.getText().toString());
                params.put("jml", edjml.getText().toString());
                params.put("diskon", eddiskon.getText().toString());
                params.put("harga", edhrg.getText().toString());
                params.put("id_user", userid);
                params.put("ppn", ppn);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
    private void get_produkall(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_produkall, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String namaSales="";
                    Boolean cek=jsonObject.getBoolean("success");
                    if (cek) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objectData = jsonArray.getJSONObject(i);
                            namaSales = objectData.getString("produk");
                            salesList.add(""+namaSales);
                        }
                    }
                    else{
                       // showMsg(ctx, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (ctx, android.R.layout.select_dialog_item, salesList);
                    edproduk.setThreshold(1);
                    edproduk.setAdapter(adapter);
                    edproduk.setTextColor(Color.BLACK);
                    loading.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                   // showMsg(ctx, "Error", "Sales tidak ditemukan: \n Error :" + e.getMessage(), helper.ERROR_TYPE);
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showMsg(ctx, "Pesan", "onErrorResponse: " + error);
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                return params;
            }
        };
        Volley.newRequestQueue(ctx).add(stringRequest);

    }

    private void get_produk_by(final String kode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_produk_by, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("data");
                    JSONObject c = result.getJSONObject(0);
                    String kode = c.getString("kode_produk");
                    String name = c.getString("produk");
                    String harga = c.getString("harga");
                    salesList.add(""+kode+""+name);
                    if (kode.trim().equals("null") || kode.trim().equals("")){
                        //showMsg(ctx, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }else{
                        tvnamaproduk.setText(kode);
                        tvidproduk.setText(name);
                        edhrg.setText(harga);
                    }
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", kode);
                params.put("key", api.key);
                return params;
            }
        };
        Volley.newRequestQueue(ctx).add(stringRequest);
    }
}
