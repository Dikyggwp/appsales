package com.app.mobiledev.salesapp.outlet;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.app.mobiledev.salesapp.po.PO;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;
import static com.app.mobiledev.salesapp.helper.showMsg;

public class ModalAddOutlet extends DialogFragment {

    Context ctx;
    private SessionManager sessionmanager;
    private ProgressDialog loading;
    Button btnproses,btnbatal;
    String userid,kodearea;
    EditText edalamat,ednpwp;
    ImageView image;
    Camera camera;
    private Bitmap imageFoto;
    private String encodedimage="";
    AutoCompleteTextView edoutlet;
    private ArrayList<String> salesList;
    TextView tvalamat,tvidout;

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
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.modal_addoutlet, container, false);
        sessionmanager = new SessionManager(ctx);

        userid = sessionmanager.getIDUser();
        kodearea = sessionmanager.getKodeArea();
        loading = new ProgressDialog(ctx);
        loading.setMessage("Loading ...");
        salesList = new ArrayList<>();
        tvidout=view.findViewById(R.id.tvidout);
        tvalamat=view.findViewById(R.id.tvalamat);
        btnproses = view.findViewById(R.id.btnproses);
        image = view.findViewById(R.id.image);
        edoutlet = view.findViewById(R.id.edoutlet);
        edalamat = view.findViewById(R.id.edalamat);
        ednpwp = view.findViewById(R.id.ednpwp);
        btnbatal = view.findViewById(R.id.btnbatal);

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
        
        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edoutlet.getText().toString().equals("") || edalamat.getText().toString().equals("")){
                    Toast.makeText(ctx, "Input data belum lengkap", Toast.LENGTH_SHORT).show();
                }else{
                    dismiss();
                    add_outlet();
                }

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera = new Camera.Builder()
                        .resetToCorrectOrientation(true)
                        .setTakePhotoRequestCode(0)
                        .setDirectory("pics")
                        .setName("ali_" + System.currentTimeMillis())
                        .setImageFormat(Camera.IMAGE_JPEG)
                        .setCompression(75)
                        .setImageHeight(1000)
                        .build((Activity) ctx);
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnbatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                refresh_data();
            }
        });
        return view;
    }
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }
    private void get_produk_by(final String kode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet_byname, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("data");
                    JSONObject c = result.getJSONObject(0);
                    String kode = c.getString("id_outlet");
                    String almt = c.getString("alamat");
                    String nama = c.getString("outlet");
                    salesList.add(""+kode+""+nama);
                    if (kode.trim().equals("null") || kode.trim().equals("")){
                        showMsg(ctx, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }else{
                        tvalamat.setText(almt);
                        tvidout.setText(kode);
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
    private void get_outletall(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet, new Response.Listener<String>() {
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
                            namaSales = objectData.getString("outlet");
                            salesList.add(""+namaSales);
                        }
                    }
                    else{
                        showMsg(ctx, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (ctx, android.R.layout.select_dialog_item, salesList);
                    edoutlet.setThreshold(1);
                    edoutlet.setAdapter(adapter);
                    edoutlet.setTextColor(Color.BLACK);
                    loading.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    showMsg(ctx, "Error", "Sales tidak ditemukan: \n Error :" + e.getMessage(), helper.ERROR_TYPE);
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMsg(ctx, "Pesan", "onErrorResponse: " + error);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                Bitmap bitmap = camera.getCameraBitmap();
                if (bitmap != null ) {
                    image.setImageBitmap(bitmap);
                    imageFoto=bitmap;
                    imageFoto=Bitmap.createScaledBitmap(imageFoto, 500, 500, false);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageFoto.compress(Bitmap.CompressFormat.PNG, 50, bytes);
                    encodedimage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                } else { Toast.makeText(ctx, "Picture not taken!", Toast.LENGTH_SHORT).show(); }
            }
        }catch (Exception e){
            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
        }
    }

    private void refresh_data() {
        ((Outlet)ctx).get_outlet();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ((Outlet)ctx).get_outlet();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void add_outlet() {
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.simpan_outlet, new Response.Listener<String>() {
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
                params.put("key", api.key);
                params.put("outlet", edoutlet.getText().toString());
                params.put("alamat", edalamat.getText().toString());
                params.put("npwp", ednpwp.getText().toString());
                params.put("id_user", userid);
                params.put("kode_area", kodearea);
                params.put( "img", "data:image/png;base64,"+encodedimage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
}