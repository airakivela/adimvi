package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RewallModel {
    public String content = "";
    public String created = "";
    public String username = "";
    public String userAvatar = "";
    public int verify = 0;
    public String imageUrl = "";
    public int id = 0;

    public void initWithJSON(JSONObject object) {
        try {
            content = object.getString("origin_wall_content");
            created = object.getString("origin_wall_created");
            username = object.getString("origin_wall_username");
            userAvatar = ApiUtil.ImageUrl + object.getString("origin_wall_useravatar");
            verify = object.getInt("origin_wall_userverify");
            imageUrl = object.getString("origin_wall_imageUrl");
            id = object.getInt("origin_wall_messageID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
