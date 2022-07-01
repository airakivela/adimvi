package com.application.adimviandroid.types;

public enum HomeSearchType {
    POST("post"),
    USERNAME("member"),
    TAG("tag");

    public String val;

    HomeSearchType(String value) {
        val = value;
    }
}
