package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryModel {
    public int categoryID = 0;
    public String title = "";
    public String imgUrl = "";

    public void initWithJSON(JSONObject jsonObject) {
        try {
            categoryID = jsonObject.getInt("categoryid");
            title = jsonObject.getString("title");
            imgUrl = jsonObject.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
