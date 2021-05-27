package com.example.cryptx.Model;

import java.util.HashMap;

public class reminder {
    public HashMap<String, Integer> map = new HashMap<>();

    public reminder() {
        map.put("BTC",1);
        map.put("ETH",2);
        map.put("BCH",1);
        map.put("BNB",2);
        map.put("DOGE",1);
        map.put("LTC",2);
        map.put("LINK",1);
        map.put("MKR",2);
        map.put("USDT",1);
        map.put("XRP",2);
    }
}
