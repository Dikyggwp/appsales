package com.app.mobiledev.salesapp.stok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;

import java.util.List;

public class StokAdp extends RecyclerView.Adapter<StokAdp.ReyclerViewHolder> {
    private Context mCtx;
    private List<StokMdl> StokMdl;

    public StokAdp(List<StokMdl> StokMdl , Context ctx){
        this.mCtx=ctx;
        this.StokMdl=StokMdl;
    }

    @NonNull
    @Override
    public StokAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_stok, null);
        return new StokAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StokAdp.ReyclerViewHolder holder, int position) {
        final StokMdl Object = StokMdl.get(position);
        holder.tvproduk.setText(Object.getNama());
        holder.tvgudang.setText(Object.getGdg());
        holder.tvharga.setText(Object.getHrg());
        holder.tvstok.setText(Object.getStok());

    }
    @Override
    public int getItemCount() {
        return StokMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvproduk,tvgudang,tvstok,tvharga;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvproduk=itemView.findViewById(R.id.tvproduk);
            tvgudang=itemView.findViewById(R.id.tvgudang);
            tvstok=itemView.findViewById(R.id.tvstok);
            tvharga=itemView.findViewById(R.id.tvharga);

        }
    }
}