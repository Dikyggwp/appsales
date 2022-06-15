package com.app.mobiledev.salesapp.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.po.PO;

import java.util.List;

public class PlanDateAdp extends RecyclerView.Adapter<PlanDateAdp.ReyclerViewHolder> {
    private Context mCtx;
    private List<PlanDateMdl> PlanDateMdl;

    public PlanDateAdp(List<PlanDateMdl> PlanDateMdl , Context ctx){
        this.mCtx=ctx;
        this.PlanDateMdl=PlanDateMdl;
    }
    @NonNull
    @Override
    public PlanDateAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_plan_date, null);
        return new PlanDateAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanDateAdp.ReyclerViewHolder holder, int position) {
        final PlanDateMdl Object = PlanDateMdl.get(position);
        holder.tgl.setText(Object.getTgl());

        holder.tgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlanActivity)mCtx).getData(Object.getTgl());
            }
        });
        holder.lntgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlanActivity)mCtx).getData(Object.getTgl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return PlanDateMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tgl;
        LinearLayout lntgl;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tgl=itemView.findViewById(R.id.tgl);
            lntgl=itemView.findViewById(R.id.lntgl);

        }


    }
}