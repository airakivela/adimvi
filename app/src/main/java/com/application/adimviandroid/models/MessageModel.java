package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageModel {
    public int userID = 0;
    public String imgUser = "";
    public String imgContent = "";
    public int verify = 0;
    public String userName = "";
    public String content = "";
    public String created = "";
    public boolean isMine = false;
    public int hasRecentPost = 0;

    public void initWithJSON(JSONObject object) {
        try {
            userID = object.getInt("from_userid");
            imgUser = ApiUtil.ImageUrl + object.getString("from_user_avatarblobid");
            verify = object.getInt("verify");
            userName = object.getString("from_user");
            content = object.getString("content");
            created = object.getString("created");
            isMine = userID == SharedUtil.getSharedUserID() ? true : false;
            imgContent = object.getString("image_path");
            hasRecentPost = object.getInt("hasRecentPost");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
