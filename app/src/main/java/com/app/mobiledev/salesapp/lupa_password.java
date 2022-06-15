package com.app.mobiledev.salesapp;

import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.mobiledev.salesapp.helperPackage.helper;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class lupa_password extends AppCompatActivity {

    private EditText nik;
    private Button btnReset;
    private ConstraintLayout reset;
    private SweetAlertDialog mProgressDialog;
    private String kyano="",no_nik="";
    private ImageButton btnBack;
    private Button btnKbl;
    private TextInputLayout tlnik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);
        btnReset=findViewById(R.id.btnReset);
        btnReset =findViewById(R.id.btnReset);
        btnKbl=findViewById(R.id.btnKbl);
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mProgressDialog.setTitleText("Loading");
        mProgressDialog.setCancelable(true);


        btnKbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                no_nik=nik.getText().toString();

                if(cek()){
                    if(no_nik.length()<16){
                        helper.snackBar(reset,"nik kurang dari 16 ndigit");
                    }else{
                        //resetPassword(no_nik,"");
                    }

                }else{
                    helper.snackBar(reset,"nik belum diinputkan");
                }

            }
        });

    }

    public boolean cek(){
        boolean cek=false;
        if(nik.getText().toString().isEmpty()) {
            cek = false;
        }else{
            cek = true;
        }
        return  cek;
    }


//    private void resetPassword(final String nik,final String mac_address){
//        AndroidNetworking.post(api.URL_reset_password)
//                .addBodyParameter("mc_address",mac_address)
//                .addBodyParameter("nik",nik)
//                .addBodyParameter("key", api.key)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Boolean success = response.getBoolean("status");
//                            String data = response.getString("ket");
//                            if (success) {
//                                new SweetAlertDialog(lupa_password.this, SweetAlertDialog.SUCCESS_TYPE)
//                                        .setTitleText("Informasi")
//                                        .setContentText(""+data)
//                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                finish();
//                                            }
//                                        })
//                                        .show();
//
//                            } else {
//
//                                helper.showMsg(lupa_password.this,"Informasi",""+data,helper.WARNING_TYPE);
//                            }
//                            mProgressDialog.dismiss();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("JSONERORABSEN", "onResponse: "+e);
//                            helper.showMsg(lupa_password.this, "Peringatan", "" + helper.PESAN_SERVER, helper.ERROR_TYPE);
//                            mProgressDialog.dismiss();
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        helper.showMsg(lupa_password.this, "Peringatan", "" + helper.PESAN_KONEKSI, helper.ERROR_TYPE);
//                        Log.d("EROOR_EXCEPTION", "onError: "+anError);
//                        mProgressDialog.dismiss();
//
//                    }
//                });
//
//    }

    @Override
    public void onBackPressed() {
        finish();
    }



}
