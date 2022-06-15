package com.app.mobiledev.salesapp.aktifitas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;

import java.util.List;

public class aktifitasAdapter extends RecyclerView.Adapter<aktifitasAdapter.ReyclerViewHolder> {
    private Context mCtx;
    private List<aktifitasModel> aktifitasModel;

    public aktifitasAdapter(List<aktifitasModel> aktifitasModel , Context ctx){
        this.mCtx=ctx;
        this.aktifitasModel=aktifitasModel;
    }


    @NonNull
    @Override
    public aktifitasAdapter.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_aktivitas, null);
        return new aktifitasAdapter.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull aktifitasAdapter.ReyclerViewHolder holder, int position) {
        final aktifitasModel Object = aktifitasModel.get(position);
        holder.tvaktifitas.setText(Object.getAktifitas());
        holder.tgl.setText(Object.getTgl());

    }
    @Override
    public int getItemCount() {
        return aktifitasModel.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvaktifitas,tgl;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvaktifitas=itemView.findViewById(R.id.tvaktifitas);
            tgl=itemView.findViewById(R.id.tgl);

        }


    }
}
