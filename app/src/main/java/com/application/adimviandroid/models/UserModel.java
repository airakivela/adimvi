package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    public int userID = 0;
    public String userName = "";
    public String userEmail = "";
    public int userWallPost = 0;
    public String userPoint = "";
    public int userPointStatus = 0;
    public int userPromotionImageStatus = 0;
    public String created = "";

    public void initUserWithJSON(JSONObject obj) {
        try {
            userID = obj.getInt("userid");
            userName = obj.getString("handle");
            userEmail = obj.getString("email");
            userWallPost = obj.getInt("wallposts");
            userPoint = obj.getString("points");
            userPointStatus = obj.getInt("point_status");
            userPromotionImageStatus = obj.getInt("promotional_image_status");
            created = obj.getString("created");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
