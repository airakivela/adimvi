package com.application.adimviandroid.models;

public class InAppFeatureModel {
    public String id;
    public String duration;
    public String price;
    public String title;
    public String timeDay;

    public InAppFeatureModel(String id, String duration, String price, String title, String timeDay) {
        this.id = id;
        this.duration = duration;
        this.price = price;
        this.title = title;
        this.timeDay = timeDay;
    }
}
