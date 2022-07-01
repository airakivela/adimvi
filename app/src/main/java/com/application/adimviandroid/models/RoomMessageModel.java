package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomMessageModel {
    public int userID = 0;
    public String userName = "";
    public String senderAvatar = "";
    public int senderVerify = 0;
    public String content = "";
    public String extra = "";
    public String format = "";

    public void initWithJSON(JSONObject jsonObject) {
        try {
            userID = jsonObject.getInt("userID");
            userName = jsonObject.getString("userName");
            senderAvatar = jsonObject.getString("senderAvatar").isEmpty() ? "" : (ApiUtil.ImageUrl + jsonObject.getString("senderAvatar"));
            senderVerify = jsonObject.getInt("senderVerify");
            content = jsonObject.getString("content");
            extra = jsonObject.getString("extra");
            format = jsonObject.getString("format");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
