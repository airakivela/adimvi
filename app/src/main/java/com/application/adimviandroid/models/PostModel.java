package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PostModel {
    public int verifiy = 0;
    public String categoryName = "";
    public int userID = 0;
    public int postID = 0;
    public String handle = "";
    public String avatarBlobid = "";
    public String comments = "";
    public String title = "";
    public String upvotes = "0";
    public String views = "";
    public String like = "";
    public String postImg = "";
    public int categoryID = 0;
    public String shareLink = "";
    public String credit = "";
    public String price = "";
    public int pricer = 0;
    public String postBuy = "0";
    public int postFollow = 0;
    public String postContent = "";
    public float rating = 0;
    public String ratingVotes = "";
    public String postCreated = "";
    public String shortPostLink = "";
    public String postDate = "";
    public int hasRecentPost = 0;

    public void initWithJSON(JSONObject object) {
        try {
            verifiy = object.getInt("verify");
            try {
                categoryName = object.getString("category_name");
            } catch (JSONException e) {
                categoryName = object.getString("categoyname");
            }
            userID = object.getInt("userid");
            postID = object.getInt("postid");
            try {
                handle = object.getString("handle");
            } catch (JSONException e) {
                handle = object.getString("username");
            }
            avatarBlobid = ApiUtil.ImageUrl + object.getString("avatarblobid");
            try {
                comments = object.getString("comments");
            } catch (JSONException e) {
                comments = object.getString("total_message");
            }
            try {
                title = object.getString("title");
            } catch (JSONException e) {
                title = object.getString("post_title");
            }
            try {
                upvotes = object.getString("upvotes");
            } catch (JSONException e) {
                upvotes = "0";
            }
            views = object.getString("views");
            try{
                like = object.getString("like");
            } catch (JSONException e) {
                try {
                    like = object.getString("netVotes");
                } catch (JSONException e1) {
                    like = object.getString("netvotes");
                }
            }
            postImg = object.getString("post_image");
            try {
                categoryID = object.getInt("categoryid");
            } catch (JSONException e) {
                categoryID = 0;
            }
            shareLink = object.getString("share_link");
            credit = object.getString("credit");
            price = object.getString("price");
            pricer = object.getInt("pricer");
            postBuy = object.getString("post_buy");
            postFollow = object.getInt("post_followup");
            try {
                postContent = object.getString("post_content");
            } catch (JSONException e) {
                postContent = object.getString("post_description");
            }
            rating = Float.parseFloat(object.getString("avgRating"));
            ratingVotes = object.getString("ratingVotes");
            postCreated = object.getString("post_created");
            shortPostLink = object.getString("shortPostLink");
            postDate = object.getString("post_date");
            hasRecentPost = object.getInt("hasRecentPost");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
