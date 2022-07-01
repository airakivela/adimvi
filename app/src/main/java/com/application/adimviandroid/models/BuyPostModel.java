package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class BuyPostModel {
    public int buyerID = 0;
    public int postID = 0;
    public String usd = "";
    public String userName = "";
    public int notify = 0;
    public String created = "";

    public void initWithJSON(JSONObject object) {
        try {
            buyerID = object.getInt("buyerid");
            postID = object.getInt("postid");
            usd = object.getString("usd");
            userName = object.getString("username");
            notify = object.getInt("notify");
            created = object.getString("created");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
