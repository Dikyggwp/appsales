package com.app.mobiledev.salesapp.po;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.mobiledev.salesapp.kunjungan.kunjungan;
import com.app.mobiledev.salesapp.main_fragment;
import com.app.mobiledev.salesapp.outlet.ModalAddOutlet;
import com.app.mobiledev.salesapp.outlet.Outlet;
import com.app.mobiledev.salesapp.plan.PlanActivity;
import com.app.mobiledev.salesapp.plan.PlanAdapter;
import com.app.mobiledev.salesapp.plan.PlanMdl;
import com.app.mobiledev.salesapp.sesion.SessionManager;
import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static com.app.mobiledev.salesapp.helper.showMsg;

public class PO extends AppCompatActivity {

    modalProduk modalproduk;
    Button btnadd,btnsimpan;
    RecyclerView rcproduk;
    private ProgressDialog mProgressDialog;
    private List<POprodukMdl> mListData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SessionManager sesi;
    String userid,marketing,kodearea;
    EditText edket,eddistri,ednpwp;
    ImageView image;
    private Camera camera;
    private String encodedimage="";
    private Bitmap imageFoto;
    AutoCompleteTextView edoutlet,edkodepjk;
    private ArrayList<String> salesList;
    TextView tvalamat,tvidout,txtTotalPrice,tvmode,tvaddnpwp;
    ModalNpwp modalnpwp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po);

        modalproduk = new modalProduk();
        salesList = new ArrayList<>();

        modalnpwp = new ModalNpwp();

        tvaddnpwp=findViewById(R.id.tvaddnpwp);
        edkodepjk=findViewById(R.id.edkodepjk);
        ednpwp=findViewById(R.id.ednpwp);
        txtTotalPrice=findViewById(R.id.txtTotalPrice);
        tvidout=findViewById(R.id.tvidout);
        tvalamat=findViewById(R.id.tvalamat);
        image=findViewById(R.id.image);
        btnadd=findViewById(R.id.btnadd);
        eddistri=findViewById(R.id.eddistri);
        edket=findViewById(R.id.edket);
        edoutlet=findViewById(R.id.edoutlet);
        btnsimpan=findViewById(R.id.btnsimpan);
        rcproduk=findViewById(R.id.rcproduk);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading ...");

        sesi = new SessionManager(this);
        userid = sesi.getIDUser();
        marketing = sesi.getUser();
        kodearea = sesi.getKodeArea();

        mListData = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PO.this);
        rcproduk.setLayoutManager(layoutManager);

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

        mSwipeRefreshLayout=findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout .setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                get_produk();
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
                        .build(PO.this);
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//               photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, 1213);
            }
        });
        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListData.isEmpty()){
                    helper.showMsg(PO.this,"Peringatan","Tambahkan minimal 1 barang");
                } else if(eddistri.getText().toString().equals("")) {
                    helper.showMsg(PO.this,"Peringatan","Distributor Wajib Diisi",1);
                } else if(edoutlet.getText().toString().equals("")) {
                    helper.showMsg(PO.this,"Peringatan","Outlet Wajib Diisi",1);
                }else{
                    simpan_po();
                }

            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_produk();
            }
        });
        tvaddnpwp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addnpwp();
            }
        });
        get_produk();
        get_outletall();
        tvmode=findViewById(R.id.tvmode);
        tvmode.setVisibility(View.GONE);
        if (checkInternet()){
            tvmode.setVisibility(View.GONE);
        }else{
            tvmode.setVisibility(View.VISIBLE);
            tvmode.setText("MODE OFFLINE");
            tvmode.setBackgroundColor(getResources().getColor(R.color.btnDanger));
        }
    }
    public boolean checkInternet(){
        boolean connectStatus;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= ConnectionManager != null ? ConnectionManager.getActiveNetworkInfo() : null;
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }
    private void get_produk_by(final String kode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet_byname, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("data");
                    JSONObject c = result.getJSONObject(0);
                    String kode = c.getString("id_outlet");
                    String almt = c.getString("alamat");
                    String nama = c.getString("outlet");
                    String foto_npwp = c.getString("foto_npwp");
                    String npwp = c.getString("npwp");
                    String kode_pajak = c.getString("kode_pajak");
                    salesList.add(""+kode+""+nama);
                    if (kode.trim().equals("null") || kode.trim().equals("")){
                        showMsg(PO.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }else{
                        tvalamat.setText(almt);
                        tvidout.setText(kode);
                        edkodepjk.setText(kode_pajak);
                        ednpwp.setText(npwp);
                        if (npwp.equals("") || npwp.equals("null")){
                            //npwp kosong
                            ednpwp.setText("");
                            new SweetAlertDialog(PO.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Konfirmasi")
                                    .setContentText("Npwp belum ada..input NPWP baru ?")
                                    .setConfirmText("Ya")
                                    .setCancelText("Tidak")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            addnpwp();
                                        }
                                    }).show();
                        }
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", kode);
                params.put("key", api.key);
                params.put("kodearea", kodearea);
                return params;
            }
        };
        Volley.newRequestQueue(PO.this).add(stringRequest);
    }

    private void addnpwp() {
        if (tvidout.getText().toString().equals("")){
            Toast.makeText(this, "Outlet Belum diisi", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(PO.this, AddNpwp.class);
            intent.putExtra("idoutlet", tvidout.getText().toString());
            startActivity(intent);
        }
    }

    private void get_outletall(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.get_outlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.show();
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
                        //showMsg(PO.this, "Error", "Data tidak ditemukan", helper.ERROR_TYPE);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (PO.this, android.R.layout.select_dialog_item, salesList);
                    edoutlet.setThreshold(1);
                    edoutlet.setAdapter(adapter);
                    edoutlet.setTextColor(Color.BLACK);
                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                   // showMsg(PO.this, "Error", "Sales tidak ditemukan: \n Error :" + e.getMessage(), helper.ERROR_TYPE);
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showMsg(PO.this, "Pesan", "onErrorResponse: " + error);
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("kodearea", kodearea);
                return params;
            }
        };
        Volley.newRequestQueue(PO.this).add(stringRequest);

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                }
                else { Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show(); }
            }
        }catch (Exception e){
            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
        }

//        try {
//            if (resultCode == RESULT_OK) {
//                if (requestCode == 1213) {
//                    Uri selectedImageUri = data.getData();
//                    if (null != selectedImageUri) {
//                        image.setImageURI(selectedImageUri);
//
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                        imageFoto=bitmap;
//                        imageFoto=Bitmap.createScaledBitmap(imageFoto, 500, 500, false);
//                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                        imageFoto.compress(Bitmap.CompressFormat.PNG, 50, bytes);
//                        encodedimage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
//                    }
//                }
//            }
//        }catch (Exception e){
//            Log.d("TAKE_CAMERA", "onActivityResult: "+e);
//        }

    }
    private void simpan_po() {
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.simpan_po, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(PO.this,""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(PO.this,""+data, Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(PO.this, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toasty.error(PO.this, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_marketing", userid);
                params.put("marketing", marketing);
                params.put("outlet", edoutlet.getText().toString());
                params.put("idoutlet", tvidout.getText().toString());
                params.put("npwp", ednpwp.getText().toString());
                params.put("keterangan", edket.getText().toString());
                params.put("distributor", eddistri.getText().toString());
                params.put( "img", "data:image/png;base64,"+encodedimage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PO.this);
        requestQueue.add(stringRequest);
    }

    public void get_produk(){
        mProgressDialog.show();
        final StringRequest request = new StringRequest(Request.Method.POST, api.get_produkpo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(PO.this, ""+response, Toast.LENGTH_SHORT).show();
                        iniData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        //Toast.makeText(PO.this, "VolleyError"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_user", userid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private void iniData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mListData.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectData = jsonArray.getJSONObject(i);
                    String barang = objectData.getString("barang");
                    String harga = objectData.getString("harga_po");
                    String id_barang = objectData.getString("id_barang");
                    String po = objectData.getString("po");
                    String jml = objectData.getString("jml");
                    POprodukMdl mdl = new POprodukMdl();
                    mdl.setId_barang(id_barang);
                    mdl.setPo(po);
                    mdl.setQty(jml);
                    mdl.setHarga(harga);
                    mdl.setBarang(barang);
                    mListData.add(mdl);
                }
                POprodukAdp mAdapter = new POprodukAdp(mListData, PO.this, userid, txtTotalPrice);
                rcproduk.setAdapter(mAdapter);
            } else {
                //Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }

    private void add_produk() {
        modalproduk.ctx = PO.this;
        Bundle bundl = new Bundle();
        modalproduk.setArguments(bundl);
        modalproduk.show(getSupportFragmentManager(), "modal produk");
    }
}