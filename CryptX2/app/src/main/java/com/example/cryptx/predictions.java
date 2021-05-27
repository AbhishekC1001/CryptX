package com.example.cryptx;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cryptx.Model.reminder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class predictions extends AppCompatActivity {
    private float[][][] a=new float[1][100][1];
    ArrayList<Float> store = new ArrayList<Float>();
    float maxx=0;float minx= Float.MAX_VALUE;
    Interpreter interpreter;
    String filename="";
    float[] days=new float[31];
    Dialog myDialog;
    float currentvalue;
    private Handler mHandler;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_predictions);
        mHandler = new Handler(Looper.getMainLooper());
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        String symbol = bundle.getString("name");
        reminder ob=new reminder();
        myDialog=new Dialog(this);
        if(ob.map.containsKey(symbol))
            filename=symbol+".tflite";
        else{
            filename="BTC.tflite";
        }
        Log.d("filename",filename);
        String url="https://min-api.cryptocompare.com/data/v2/histoday?fsym="+symbol+"&tsym=USD&limit=99&api_key=940ead0ebcee1dc26354d6b647ffecdc7df70483fa587879958c8215fb6963d2";
        try {
            interpreter=new Interpreter(bitcoin());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView imv=(ImageView)findViewById(R.id.priv);
        Button button=(Button)findViewById(R.id.graphbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup();
            }
        });
        Picasso.get().load(new StringBuilder("https://s2.coinmarketcap.com/static/img/coins/64x64/")
                .append(id).append(".png").toString()).placeholder(R.drawable.dollar)
                .error(R.drawable.dollar).into(imv);
        TextView txv=(TextView)findViewById(R.id.prtv);
        txv.setText(symbol);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                Log.d("CoinAPi",body);
                JSONObject jobj=null;
                JSONArray jarr=null;
                try {
                    jobj = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jobj2=jobj.getJSONObject("Data");
                    jarr = jobj2.getJSONArray("Data");
                    Log.d("jsonarray", String.valueOf(jarr));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<jarr.length();i++){
                    JSONObject currentBook = null;
                    try {
                        currentBook = jarr.getJSONObject(i);
                        Log.d("Tag"+i,currentBook.getString("close"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        store.add(new Float(Float.parseFloat(currentBook.getString("close"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                days[0]=store.get(99);
                for(int j=1;j<=30;j++){
                    for(int k=0;k<100;k++){
                        a[0][k][0]=store.get(k);
                        if(a[0][k][0]<minx){
                            minx=a[0][k][0];
                        }
                        if(a[0][k][0]>maxx){
                            maxx=a[0][k][0];
                        }
                    }
                    for(int i=0;i<100;i++){
                        a[0][i][0]=(store.get(i)-minx)/(maxx-minx);
                    }
                    days[j]= (float) (doInference(a)*(maxx-minx)+minx);
                    store.remove(0);
                    store.add(new Float(days[j]));
                    minx=Float.MAX_VALUE;
                    maxx=0;
                    Log.d("Store"+j,Arrays.toString(store.toArray()));
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=1;i<=30;i++){

                            int dId = getResourceID("trd" + i, "id", getApplicationContext());
                            int pId = getResourceID("trp" + i, "id", getApplicationContext());
                            TextView tv=(TextView)findViewById(dId);
                            TextView p=(TextView)findViewById(pId);
                            tv.setText(""+java.time.LocalDate.now().plusDays(i));
                            tv.setTextColor(Color.parseColor("#003300"));
                            p.setText(""+ NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(days[i]*73.41));
                            if(days[i]<days[i-1]){
                                p.setTextColor(Color.parseColor("#ff0000"));
                            }
                            else{
                                p.setTextColor(Color.parseColor("#66ccff"));
                            }
                        } // must be inside run()
                    }
                });
            }
        });

    }
    public void showpopup(){
        myDialog.setContentView(R.layout.popupgraph);
        GraphView gp=(GraphView) myDialog.findViewById(R.id.graphv);
        Button close=(Button)myDialog.findViewById(R.id.closebtn);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        for(int i=1;i<=30;i++){
            series.appendData(new DataPoint(i, days[i]*73.41),true,50);
        }
        gp.addSeries(series);
        gp.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        gp.getViewport().setScrollable(true);  // activate horizontal scrolling
        gp.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
        gp.getViewport().setScrollableY(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }
    private double doInference(float[][][] a){
        float[][] output=new float[1][1];
        interpreter.run(a,output);
        return output[0][0];
    }
    private MappedByteBuffer bitcoin() throws IOException{
        AssetFileDescriptor assetFileDescriptor=this.getAssets().openFd(filename);
        FileInputStream fileInputStream=new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel =fileInputStream.getChannel();
        long startoffset=assetFileDescriptor.getStartOffset();
        long length=assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,length);
    }
}