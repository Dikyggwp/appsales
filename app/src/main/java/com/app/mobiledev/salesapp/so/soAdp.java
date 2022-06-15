package com.app.mobiledev.salesapp.so;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;

import java.util.List;


public class soAdp extends RecyclerView.Adapter<soAdp.ReyclerViewHolder> {
    private Context mCtx;
    private List<soMdl> soMdl;

    public soAdp(List<soMdl> soMdl , Context ctx){
        this.mCtx=ctx;
        this.soMdl=soMdl;
    }

    @NonNull
    @Override
    public soAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_plan, null);
        return new soAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull soAdp.ReyclerViewHolder holder, int position) {
        final soMdl Object = soMdl.get(position);
        holder.nama.setText(Object.getKet());
        holder.alamat.setText(Object.getKode());

    }
    @Override
    public int getItemCount() {
        return soMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView nama,alamat;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            nama=itemView.findViewById(R.id.outlet);
            alamat=itemView.findViewById(R.id.tgl);

        }
    }
}