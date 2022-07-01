package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RepostModel {
    public String userName = "";
    public String userAvatar = "";
    public int verify = 0;
    public String title = "";
    public String content = "";
    public String created = "";
    public int id = 0;

    public void initWithJSON(JSONObject object) {
        try {
            userName = object.getString("origin_post_user");
            try {
                userAvatar = ApiUtil.ImageUrl + object.getString("origin_post_user_avatar");
            } catch (JSONException e) {
                e.printStackTrace();
                userAvatar = "";
            }
            verify = object.getInt("origin_post_user_verify");
            title = object.getString("origin_post_title");
            content = object.getString("origin_post_content");
            created = object.getString("origin_post_created");
            id = object.getInt("origin_post_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
