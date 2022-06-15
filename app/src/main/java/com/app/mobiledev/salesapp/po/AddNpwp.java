package com.app.mobiledev.salesapp.po;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobiledev.salesapp.api.api;
import com.app.mobiledev.salesapp.helperPackage.helper;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobiledev.salesapp.R;
import com.mindorks.paracamera.Camera;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddNpwp extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_npwp);

        sessionmanager = new SessionManager(AddNpwp.this);
        userid = sessionmanager.getIDUser();
        kodearea = sessionmanager.getKodeArea();
        loading = new ProgressDialog(AddNpwp.this);
        loading.setMessage("Loading ...");

        tvidout=findViewById(R.id.tvidout);
        btnproses = findViewById(R.id.btnproses);
        image = findViewById(R.id.image);
        ednpwp = findViewById(R.id.ednpwp);
        btnbatal = findViewById(R.id.btnbatal);

        Intent iin = getIntent();
        Bundle sa = iin.getExtras();
        try {
            idoutlet = sa.get("idoutlet")!=null?sa.get("idoutlet").toString():null;
        }catch (Exception e) {
            idoutlet ="";
        }
        tvidout.setText(idoutlet);
        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ednpwp.getText().toString().equals("")){
                    Toast.makeText(AddNpwp.this, "Input data belum lengkap", Toast.LENGTH_SHORT).show();
                }else{
                    update_npwp();
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
                        .build((Activity) AddNpwp.this);
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
                finish();
            }
        });
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
                    //Toast.makeText(AddNpwp.this.getApplicationContext(), "foto"+encodedimage, Toast.LENGTH_SHORT).show();
                }
                else { Toast.makeText(AddNpwp.this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show(); }
            }
        }catch (Exception e){
            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
        }
    }
    private void update_npwp() {
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.update_npwp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(AddNpwp.this,""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(AddNpwp.this,""+data, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(AddNpwp.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toasty.error(AddNpwp.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddNpwp.this);
        requestQueue.add(stringRequest);
    }

}