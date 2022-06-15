package com.app.mobiledev.salesapp.po;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import com.app.mobiledev.salesapp.outlet.Outlet;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ModalNpwp extends DialogFragment {

    Context ctx;
    private SessionManager sessionmanager;
    private ProgressDialog loading;
    Button btnproses,btnbatal;
    String userid,kodearea;
    EditText ednpwp;
    ImageView image;
    Camera camera;
    private Bitmap imageFoto;
    private String encodedimage="",idoutlet;
    TextView tvidout;

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
        final View view = inflater.inflate(R.layout.modal_addnpwp, container, false);
        sessionmanager = new SessionManager(ctx);

        userid = sessionmanager.getIDUser();
        kodearea = sessionmanager.getKodeArea();
        loading = new ProgressDialog(ctx);
        loading.setMessage("Loading ...");

        tvidout=view.findViewById(R.id.tvidout);
        btnproses = view.findViewById(R.id.btnproses);
        image = view.findViewById(R.id.image);
        ednpwp = view.findViewById(R.id.ednpwp);
        btnbatal = view.findViewById(R.id.btnbatal);

        Bundle m = getArguments();
        idoutlet = m.getString("outlet");
        if (idoutlet!=null && idoutlet!=""){
            tvidout.setText(idoutlet);
        }else{
            tvidout.setText("");
        }

        tvidout.setText(idoutlet);
        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ednpwp.getText().toString().equals("")){
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
                        .setTakePhotoRequestCode(1)
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
                    Toast.makeText(ctx.getApplicationContext(), "foto"+encodedimage, Toast.LENGTH_SHORT).show();
                }
                else { Toast.makeText(ctx.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show(); }
            }
        }catch (Exception e){
            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
        }
    }

    private void refresh_data() {
        //((PO)ctx).get_outlet();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //((PO)ctx).get_outlet();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void add_outlet() {
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.update_npwp, new Response.Listener<String>() {
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
                    loading.dismiss();
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
                params.put("outlet", tvidout.getText().toString());
                params.put("npwp", ednpwp.getText().toString());
                params.put( "img", "data:image/png;base64,"+encodedimage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
}