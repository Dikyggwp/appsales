package com.app.mobiledev.salesapp.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.outlet.OutletAdp;
import com.app.mobiledev.salesapp.outlet.OutletMdl;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ReyclerViewHolder> {
    private Context mCtx;
    private List<PlanMdl> PlanMdl;

    public PlanAdapter(List<PlanMdl> PlanMdl , Context ctx){
        this.mCtx=ctx;
        this.PlanMdl=PlanMdl;
    }
    @NonNull
    @Override
        public PlanAdapter.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_plan, null);
        return new PlanAdapter.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.ReyclerViewHolder holder, int position) {
        final PlanMdl Object = PlanMdl.get(position);
        holder.outlet.setText(Object.getOutlet());
        holder.tvalamat.setText(Object.getAlamat());
        holder.tvtgl.setText(Object.getTgl());
    }

    @Override
    public int getItemCount() {
        return PlanMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView outlet,tvalamat,tvtgl;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            outlet=itemView.findViewById(R.id.outlet);
            tvalamat=itemView.findViewById(R.id.tvalamat);
            tvtgl=itemView.findViewById(R.id.tvtgl);

        }


    }
}