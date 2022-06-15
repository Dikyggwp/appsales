package com.app.mobiledev.salesapp.outlet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobiledev.salesapp.R;

import java.util.List;

public class OutletAdp extends RecyclerView.Adapter<OutletAdp.ReyclerViewHolder> {
    private Context mCtx;
    private List<OutletMdl> OutletMdl;

    public OutletAdp(List<OutletMdl> OutletMdl , Context ctx){
        this.mCtx=ctx;
        this.OutletMdl=OutletMdl;
    }


    @NonNull
    @Override
    public OutletAdp.ReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_outlet, null);
        return new OutletAdp.ReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutletAdp.ReyclerViewHolder holder, int position) {
        final OutletMdl Object = OutletMdl.get(position);
        holder.tvoutlet.setText(Object.getNama());
        holder.tvalamat.setText(Object.getAlamat());

    }
    @Override
    public int getItemCount() {
        return OutletMdl.size();
    }

    public class ReyclerViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvoutlet,tvalamat;

        public ReyclerViewHolder(View itemView) {
            super(itemView);
            tvalamat=itemView.findViewById(R.id.tvalamat);
            tvoutlet=itemView.findViewById(R.id.tvoutlet);

        }
    }
}