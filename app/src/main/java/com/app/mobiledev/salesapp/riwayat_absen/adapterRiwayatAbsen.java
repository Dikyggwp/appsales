package com.app.mobiledev.salesapp.riwayat_absen;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.helperPackage.helper;

import java.util.List;

public class adapterRiwayatAbsen extends RecyclerView.Adapter<adapterRiwayatAbsen.ReyclerViewHolder> {
    private Context mCtx;
    private List<modelRiwayatAbsen> modelRiwayatAbsens;

    public adapterRiwayatAbsen(List<modelRiwayatAbsen> modelRiwayatAbsen , Context ctx){
        this.mCtx=ctx;
        this.modelRiwayatAbsens=modelRiwayatAbsen;
    }


    @NonNull
    @Override
    public ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_riwayat, null);
        return new ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReyclerViewHolder holder, int position) {
        final modelRiwayatAbsen Object = modelRiwayatAbsens.get(position);
        holder.tgl.setText(helper.format_tgl(Object.getTgl(),""));
        holder.tvoutlet.setText(Object.getOutlet());
        holder.tvmarket.setText(Object.getMarketing());
        holder.tvnopo.setText(Object.getNopo());

    }
    @Override
    public int getItemCount() {
        return modelRiwayatAbsens.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvoutlet,tgl,tvmarket,tvnopo;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvnopo=itemView.findViewById(R.id.tvnopo);
            tvoutlet=itemView.findViewById(R.id.tvoutlet);
            tvmarket=itemView.findViewById(R.id.tvmarket);
            tgl=itemView.findViewById(R.id.tgl);

        }


    }
}
