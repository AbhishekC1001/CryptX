package com.example.cryptx.Model;

public class CoinModel {
    public String id;
    public String name;
    public String symbol;

    public CoinModel(String id, String name, String symbol, String percentchange1h, String percentchange24h, String percentchange7d, String price) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.percent_change_1h = percentchange1h;
        this.percent_change_24h = percentchange24h;
        this.percent_change_7d = percentchange7d;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPercentchange1h() {
        return percent_change_1h;
    }

    public void setPercentchange1h(String percentchange1h) {
        this.percent_change_1h = percentchange1h;
    }

    public String getPercentchange24h() {
        return percent_change_24h;
    }

    public void setPercentchange24h(String percentchange24h) {
        this.percent_change_24h = percentchange24h;
    }

    public String getPercentchange7d() {
        return percent_change_7d;
    }

    public void setPercentchange7d(String percentchange7d) {
        this.percent_change_7d = percentchange7d;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String percent_change_1h;
    public String percent_change_24h;
    public String percent_change_7d;
    public String price;

}
