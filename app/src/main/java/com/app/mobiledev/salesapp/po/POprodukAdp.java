package com.app.mobiledev.salesapp.po;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.app.mobiledev.salesapp.helper.rp;

public class POprodukAdp extends RecyclerView.Adapter<POprodukAdp.ReyclerViewHolder> {
    private Context mCtx;
    private List<POprodukMdl> POprodukMdl;
    private String userid;
    TextView txtTotalPrice;

    public POprodukAdp(List<POprodukMdl> POprodukMdl , Context ctx, String userid, TextView txtTotalPrice){
        this.mCtx=ctx;
        this.POprodukMdl=POprodukMdl;
        this.userid=userid;
        this.txtTotalPrice=txtTotalPrice;
    }


    @NonNull
    @Override
    public POprodukAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_produk, null);
        return new POprodukAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull POprodukAdp.ReyclerViewHolder holder, int position) {
        final POprodukMdl Object = POprodukMdl.get(position);
        holder.tvnama.setText(Object.getBarang());
        holder.tvharga.setText(Object.getHarga());
        try {
//            String qty = Object.getQty();
//            String hrg = Object.getHarga();
//            double tot = Double.parseDouble(hrg) * Double.parseDouble(qty);
//            txtTotalPrice.setText(""+rp(tot));
        } catch (NullPointerException e){

        }

        holder.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapus_po(Object.getPo(), Object.getId_barang(),userid);
            }
        });

    }
    private void hapus_po(final String id_po,final String id_produk,final String iduser) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.hapus_po, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("status");
                    String data = jsonObject.getString("ket");
                    if (success) {
                        Toasty.info(mCtx,""+data, Toast.LENGTH_SHORT).show();
                        ((PO)mCtx).get_produk();
                    } else {
                        Toasty.info(mCtx,""+data, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(mCtx, "" + helper.PESAN_KONEKSI, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(mCtx, "" + helper.PESAN_SERVER, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("key", api.key);
                params.put("id_po", id_po);
                params.put("iduser", iduser);
                params.put("id_produk", id_produk);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return POprodukMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvharga,tvnama;
        private Button btndel;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvharga=itemView.findViewById(R.id.tvharga);
            tvnama=itemView.findViewById(R.id.tvnama);
            btndel=itemView.findViewById(R.id.btndel);

        }


    }
}