package com.example.cryptx.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptx.Interface.ILoadMore;
import com.example.cryptx.Model.CoinModel;
import com.example.cryptx.R;
import com.example.cryptx.predictions;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public void setIloadMore(ILoadMore iloadMore) {
        this.iloadMore = iloadMore;
    }
    ILoadMore iloadMore;
    boolean isloading;
    Activity Activity;

    public CoinAdapter(RecyclerView RecycleView,android.app.Activity activity, List<CoinModel> items) {
        Activity = activity;
        this.items = items;
        LinearLayoutManager linearLayoutManager=(LinearLayoutManager)RecycleView.getLayoutManager();
        RecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalitemcount=linearLayoutManager.getItemCount();
                lastvisiblelement=linearLayoutManager.findLastVisibleItemPosition();
                if(!isloading && totalitemcount<=(lastvisiblelement+visiblethereshold)){
                    if(iloadMore!=null){
                        iloadMore.onLoadMore();
                    }
                    isloading=true;
                }
            }
        });
    }

    List<CoinModel> items;
    int visiblethereshold=5,lastvisiblelement,totalitemcount;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(Activity).inflate(R.layout.coin_row,parent,false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CoinModel item=items.get(position);
        CoinViewHolder holderitem=(CoinViewHolder)holder;
        holderitem.coinname.setText(item.getName());
        holderitem.coinprice.setText(""+NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(Double.parseDouble(item.getPrice())));
        holderitem.coinprice.setTextColor(Color.parseColor("#6600cc"));
        holderitem.coinsymbol.setText(item.getSymbol());
        holderitem.d7c.setText(item.getPercentchange7d());
        holderitem.onehc.setText(item.getPercentchange1h());
        holderitem.t4hc.setText(item.getPercentchange24h());
        holderitem.d7c.setText(item.getPercentchange7d());
        Picasso.get().load(new StringBuilder("https://s2.coinmarketcap.com/static/img/coins/64x64/")
                        .append(item.getId()).append(".png").toString()).placeholder(R.drawable.dollar)
                .error(R.drawable.dollar).into(holderitem.coinimage);
        StringBuilder stb=new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/")
                .append(item.getSymbol().toLowerCase()).append(".png");
        Log.d(item.getSymbol(), String.valueOf(holderitem.coinimage));
        if(holderitem.coinimage==null){
            Log.d("Null",item.getSymbol());
        }
        holderitem.onehc.setTextColor(item.getPercentchange1h().contains("-")? Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        holderitem.t4hc.setTextColor(item.getPercentchange24h().contains("-")? Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        holderitem.d7c.setTextColor(item.getPercentchange7d().contains("-")? Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        holder.itemView.findViewById(R.id.cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), predictions.class);
                intent.putExtra("id",item.getId() );
                intent.putExtra("name",item.getSymbol() );
                Activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void setloaded(){
        isloading=true;
    };
    public void updateData(List<CoinModel> coinModels){
        this.items=coinModels;
        notifyDataSetChanged();
    }
    public void filterlist(ArrayList<CoinModel> filteredlist){
        items=filteredlist;
        notifyDataSetChanged();
    }
}
