package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class FeaturModel {
    public int postID = 0;
    public String postImgUrl = "";
    public String postTitle = "";
    public String webUVLink = "";
    public String readTime = "";
    public String commentCnt = "0";
    public String voteCnt = "";
    public String userAvatar = "";
    public int userVerified = 0;

    public void initWithJSON(JSONObject jsonObject) {
        try {
            postID = jsonObject.getInt("postid");
            postImgUrl = jsonObject.getString("post_image");
            postTitle =  jsonObject.getString("post_title");
            userAvatar = jsonObject.getString("avatarblobid").isEmpty() ? "" : ApiUtil.ImageUrl + jsonObject.getString("avatarblobid");
            try{
                webUVLink = jsonObject.getString("webViewLink");
            } catch (JSONException e) {
                webUVLink = jsonObject.getString("shortPostLink");
            }
            try {
                readTime = jsonObject.getString("post_created");
            } catch (JSONException e) {
                e.printStackTrace();
                readTime = jsonObject.getString("post_time");
            }
            voteCnt = jsonObject.getString("netvotes");
            commentCnt = jsonObject.getString("total_message");
            try {
                userVerified = jsonObject.getInt("verify");
            } catch (JSONException e) {
                userVerified = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
