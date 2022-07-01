package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class FollowModel {
    public int userID = 0;
    public String userName = "";
    public int verify = 0;
    public int point = 0;
    public String avatar = "";
    public int hasRecentPost = 0;
    public String followings = "";
    public String followers = "";
    public int followStatus = 0;

    public void initWithJSON(JSONObject object) {
        try {
            userID = object.getInt("userid");
            verify = object.getInt("verify");
            point = object.getInt("total_points");
            userName = object.getString("username");
            avatar = object.getString("avatarblobid").isEmpty() ? "" : ApiUtil.ImageUrl + object.getString("avatarblobid");
            hasRecentPost = object.getInt("hasRecentPost");
            followings = object.getString("totalFollowing");
            followers = object.getString("totalFollowers");
            followStatus = object.getInt("post_followup");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
