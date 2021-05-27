package com.example.cryptx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class pageAdapter extends RecyclerView.Adapter<pageAdapter.ViewHolder>{
    Context context;
    List<datepriceobj> list;
    public pageAdapter(Context context,List<datepriceobj> list){
        this.context=context;
        this.list=list;

    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        datepriceobj dpo=list.get(position);
        holder.dv.setText(dpo.getDate());
        holder.pv.setText(dpo.getPrice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dv,pv;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dv=itemView.findViewById(R.id.datintable);
            pv=itemView.findViewById(R.id.priceintable);
        }
    }
}
