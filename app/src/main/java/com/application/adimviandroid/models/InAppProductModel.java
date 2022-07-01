package com.application.adimviandroid.models;

public class InAppProductModel {
    public String id = "";
    public String title = "";
    public String price = "";

    public InAppProductModel(String id, String price) {
        this.id = id;
        this.price = price;
        this.title = "Depositar - " + price + " créditos";
    }
}
