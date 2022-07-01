package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class RecentModel {
    public int postID = 0;
    public int userID = 0;
    public String type = "";
    public String title = "";
    public String message = "";
    public String created = "";
    public String filterDate = "";

    public void initWithJSON(JSONObject object) {
        try {
            try {
                postID = object.getInt("postid");
            } catch (JSONException e) {
                postID = 0;
            }
            try {
                userID = object.getInt("userid");
            } catch (JSONException e) {
                userID = 0;
            }
            type = object.getString("type");
            title = object.getString("title");
            message = object.getString("msg");
            created = object.getString("created");
            filterDate = object.getString("filter_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
