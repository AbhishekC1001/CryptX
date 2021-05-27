package com.example.cryptx;

import java.time.LocalDate;

public class datepriceobj {
    String date;

    public datepriceobj(String date, String price) {
        this.date = date;
        this.price = price;
    }


    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    String price;
}
