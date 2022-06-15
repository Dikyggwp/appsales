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

public class LogbookAdapter extends RecyclerView.Adapter<LogbookAdapter.ReyclerViewHolder> {
    private Context mCtx;
    private List<LogbookModel> LogbookModels;

    public LogbookAdapter(List<LogbookModel> LogbookModel , Context ctx){
        this.mCtx=ctx;
        this.LogbookModels=LogbookModel;
    }


    @NonNull
    @Override
    public LogbookAdapter.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_aktivitas, null);
        return new LogbookAdapter.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogbookAdapter.ReyclerViewHolder holder, int position) {
        final LogbookModel Object = LogbookModels.get(position);
        holder.note=Object.getKet();
        holder.date=Object.getTgl();
        holder.tvaktifitas.setText(""+holder.note);
        holder.tgl.setText(""+holder.date);

    }
    @Override
    public int getItemCount() {
        return LogbookModels.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvaktifitas,tgl;
        private String date,nama,note;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvaktifitas=itemView.findViewById(R.id.tvaktifitas);
            tgl=itemView.findViewById(R.id.tgl);

        }


    }
}