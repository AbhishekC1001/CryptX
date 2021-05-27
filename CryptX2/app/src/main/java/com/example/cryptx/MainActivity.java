package com.example.cryptx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cryptx.Adapter.CoinAdapter;
import com.example.cryptx.Interface.ILoadMore;
import com.example.cryptx.Model.CoinModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    List<CoinModel> items=new ArrayList<>();
    CoinAdapter adapter;
    RecyclerView recyclerView;
    OkHttpClient client;
    Request request;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        search=(EditText)findViewById(R.id.search_bar);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.rootlayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadf10coin(1);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                loadf10coin(1);
                setupAdapter();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.coinlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void filter(String text) {
        ArrayList<CoinModel> filteredlist=new ArrayList<>();
        for(CoinModel cm:items){
            if(cm.symbol.toLowerCase().contains(text)){
                filteredlist.add(cm);
            }

        }
        adapter.filterlist(filteredlist);
    }

    private void loadf10coin(int i){
            client=new OkHttpClient();
            request=new Request.Builder().url(String.format("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=ce49fcbf-2df2-4ebb-9c40-f42dc2194f0a&convert=inr&start=%d",i)).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ArrayList<CoinModel>newitems=new ArrayList<>();
                    String body=response.body().string();
                    JSONArray booksArray=null;
                    Log.d("Tag",body);
                    JSONObject bookObject = null;

                    try {
                        bookObject = new JSONObject(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        booksArray = bookObject.getJSONArray("data");
                        Log.d("jsonarray",booksArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < booksArray.length(); i++){

                        JSONObject currentBook = null;
                        try {
                            currentBook = booksArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String id = null;
                        try {
                            id = currentBook.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String name = null;
                        try {
                            name = currentBook.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String symbol = null;
                        try {
                            symbol = currentBook.getString("symbol");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String quote = null;
                        JSONObject trial=null;
                        JSONObject trial2=null;
                        String price=null;
                        String h1c=null;
                        String h24c=null;
                        String d7c=null;
                        try {
                            quote = currentBook.getString("quote");
                             trial= new JSONObject(quote);
                            trial2=trial.getJSONObject("INR");
                            price=trial2.getString("price");
                            h1c=trial2.getString("percent_change_1h");
                            h24c=trial2.getString("percent_change_24h");
                            d7c=trial2.getString("percent_change_7d");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("tag",id+"\t"+name+"\t"+symbol+"\t"+h1c+"\t"+h24c+"\t"+d7c+"\t"+price+"\n");
                        price = String.format("%.4f", Double.parseDouble(price));
                        h1c = String.format("%.4f", Double.parseDouble(h1c));
                        h24c = String.format("%.4f", Double.parseDouble(h24c));
                        d7c = String.format("%.4f", Double.parseDouble(d7c));
                        newitems.add(new CoinModel(id,name,symbol,h1c,h24c,d7c,price));
                    }
                    Gson gson=new Gson();
                    CoinModel cm=gson.fromJson(body,CoinModel.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            items.addAll(newitems);
                            adapter.setloaded();
                            adapter.updateData(items);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
    }
    private void setupAdapter(){
        adapter=new CoinAdapter(recyclerView,this,items);
        recyclerView.setAdapter(adapter);
        adapter.setIloadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if(items.size()<=1000){
                    loadnext10coin(items.size());
                }
                else{
                    Toast.makeText(MainActivity.this,"Max reached",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadnext10coin(int i){
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=ce49fcbf-2df2-4ebb-9c40-f42dc2194f0a&convert=inr&start=%d",i+10)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().toString();
                Gson gson=new Gson();
                List<CoinModel> newitems=gson.fromJson(body,new TypeToken<List<CoinModel>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(items);
                    }
                });
            }
        });
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}