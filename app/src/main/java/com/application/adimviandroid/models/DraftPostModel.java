package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class DraftPostModel {
    public int postID = 0;
    public String postTitle = "";
    public String postType = "";

    public void initWithJSON(JSONObject object) {
        try {
            postID = object.getInt("postid");
            postTitle = object.getString("title");
            postType = object.getString("post_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
