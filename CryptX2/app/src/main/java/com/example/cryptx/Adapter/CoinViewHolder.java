package com.example.cryptx.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptx.R;

public class CoinViewHolder extends RecyclerView.ViewHolder {
    ImageView coinimage;
    TextView coinname,coinsymbol,coinprice,onehc,t4hc,d7c;
    public CoinViewHolder(@NonNull View itemView) {
        super(itemView);
        coinimage=(ImageView)itemView.findViewById(R.id.coinIcon);
        coinname=(TextView)itemView.findViewById(R.id.coinName);
        coinsymbol=(TextView)itemView.findViewById(R.id.coinSymbol);
        coinprice=(TextView)itemView.findViewById(R.id.priceUsd);
        onehc=(TextView)itemView.findViewById(R.id.oneHour);
        t4hc=(TextView)itemView.findViewById(R.id.twentyFourHour);
        d7c=(TextView)itemView.findViewById(R.id.sevenDay);
    }
}
