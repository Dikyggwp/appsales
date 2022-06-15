package com.app.mobiledev.salesapp.unplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;

import java.util.List;

public class Unplanadp extends RecyclerView.Adapter<Unplanadp.ReyclerViewHolder> {
    private Context mCtx;
    private List<UnplanMdl> UnplanMdl;

    public Unplanadp(List<UnplanMdl> UnplanMdl , Context ctx){
        this.mCtx=ctx;
        this.UnplanMdl=UnplanMdl;
    }
    @NonNull
    @Override
    public Unplanadp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_plan, null);
        return new Unplanadp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Unplanadp.ReyclerViewHolder holder, int position) {
        final UnplanMdl Object = UnplanMdl.get(position);
        holder.outlet.setText(Object.getOutlet());
        holder.tvalamat.setText(Object.getAlamat());
        holder.tvtgl.setText(Object.getTgl());
    }

    @Override
    public int getItemCount() {
        return UnplanMdl.size();
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