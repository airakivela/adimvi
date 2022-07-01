package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatModel {
    public int userID = 0;
    public String imgAvatar = "";
    public String content = "";
    public String created = "";
    public int verified = 0;
    public String userName = "";
    public int hasRecentPost = 0;

    public void initWithJSON(JSONObject object) {
        try {
            userID = object.getInt("otherUser");
            imgAvatar = object.getString("user_image").isEmpty() ? "" : ApiUtil.ImageUrl + object.getString("user_image");
            created = object.getString("created");
            verified = object.getInt("verify");
            content = object.getString("content");
            userName = object.getString("user_name");
            hasRecentPost = object.getInt("hasRecentPost");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
