package com.app.mobiledev.salesapp.kunjungan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.po.PO;
import com.app.mobiledev.salesapp.po.modalProduk;

import java.util.List;

public class kunjunganAdp extends RecyclerView.Adapter<kunjunganAdp.ReyclerViewHolder> {
    
    private Context mCtx;
    private List<kunjunganMdl> kunjunganMdl;
    ListKunjungan kn;

    public kunjunganAdp(List<kunjunganMdl> kunjunganMdl , Context ctx, ListKunjungan kn){
        this.mCtx=ctx;
        this.kunjunganMdl=kunjunganMdl;
        this.kn=kn;
    }

    @NonNull
    @Override
    public kunjunganAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.listkunjungan, viewGroup, false);
        return new kunjunganAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull kunjunganAdp.ReyclerViewHolder holder, int position) {
        final kunjunganMdl Object = kunjunganMdl.get(position);
        holder.tgl.setText(Object.getTgl());
        holder.outlet.setText(Object.getOutlet());
        holder.tvalasan.setText(Object.getAlasan_unplan());

        if (Object.getStatus().equals("berhasil")){
            holder.btnplan.setVisibility(View.VISIBLE);
            holder.btnunplan.setVisibility(View.GONE);
            holder.btnplan.setText("sudah kunjungan");
        }else if(Object.getStatus().equals("unplan")){
            holder.outlet.setText(Object.getOutlet()+" -> "+Object.getOutlet_up());
            holder.btnplan.setVisibility(View.GONE);
            holder.btnunplan.setVisibility(View.VISIBLE);
            holder.btnunplan.setText("unplan");
        }

        holder.btnplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListKunjungan)mCtx).getData();
                if (Object.getStatus().equals("berhasil") || Object.getStatus().equals("unplan")){
                    Toast.makeText(mCtx, "Outlet sudah berhasil dikunjungi", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(mCtx, kunjungan.class);
                    intent.putExtra("outlet",Object.getOutlet());
                    intent.putExtra("id",Object.getId());
                    mCtx.startActivity(intent);
                }
            }
        });
        holder.btnunplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListKunjungan)mCtx).getData();
                if (Object.getStatus().equals("berhasil") || Object.getStatus().equals("unplan")){
                    Toast.makeText(mCtx, "Outlet sudah berhasil dikunjungi", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(mCtx, UnplanAdd.class);
                    intent.putExtra("outlet", Object.getOutlet());
                    intent.putExtra("id", Object.getId());
                    mCtx.startActivity(intent);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return kunjunganMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView outlet,tgl,tvalasan;
        private Button btnplan,btnunplan;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            btnplan=itemView.findViewById(R.id.btnplan);
            btnunplan=itemView.findViewById(R.id.btnunplan);
            outlet=itemView.findViewById(R.id.outlet);
            tgl=itemView.findViewById(R.id.tgl);
            tvalasan=itemView.findViewById(R.id.tvalasan);

        }


    }
}