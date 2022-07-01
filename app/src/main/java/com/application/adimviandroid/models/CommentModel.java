package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentModel {
    public int postID = 0;
    public int parentID = 0;
    public int userID = 0;
    public int ownerUsrID = 0;
    public int categoryID = 0;
    public String commentCnt = "0";
    public String userAvatar = "";
    public String netVotes = "";
    public String totalPoints = "";
    public String userName = "";
    public String comment = "";
    public String created = "";
    public String commentType = "";
    public int verify = 0;
    public int likeType = 0;
    public boolean isSelectedComment = false;
    public int hasRecentPost = 0;
    public String lastCommentUserAvatar = "";
    public int cntAnswer = 0;
    public int votCnt = 0;

    public void initWithJSON(JSONObject obj) {
        try {
            try {
                postID = obj.getInt("postid");
            } catch (JSONException e) {
                postID = obj.getInt("messageid");
            }
            try {
                parentID = obj.getInt("parentid");
            } catch (JSONException e) {
                parentID = 0;
            }
            userID = obj.getInt("userid");
            try{
                ownerUsrID = obj.getInt("owner_userid");
            } catch (JSONException e) {
                ownerUsrID = obj.getInt("post_owner_userid");
            }
            try {
                categoryID = obj.getInt("categoryid");
            } catch (JSONException e) {
                categoryID = 0;
            }
            try {
                commentCnt = obj.getString("commentsCount");
            } catch (JSONException e) {
                commentCnt = "0";
            }
            userAvatar = ApiUtil.ImageUrl + obj.getString("avatarblobid");
            try {
                netVotes = obj.getString("netvotes");
            } catch (JSONException e) {
                netVotes = "0";
            }
            try {
                totalPoints = obj.getString("total_points");
            } catch (JSONException e) {
                totalPoints = "";
            }
            userName = obj.getString("username");
            try {
                comment = obj.getString("comment");
            } catch (JSONException e) {
                comment = obj.getString("content");
            }
            created = obj.getString("created");
            try {
                commentType = obj.getString("comment_type");
            } catch (JSONException e) {
                commentType = "";
            }
            verify = obj.getInt("verify");
            hasRecentPost = obj.getInt("hasRecentPost");
            try {
                likeType = obj.getInt("like_dislike_type");
            } catch (JSONException e) {
                likeType = 0;
            }
            try {
                cntAnswer = obj.getInt("cntAnswer");
            } catch (JSONException e) {
                cntAnswer = 0;
            }
            try {
                lastCommentUserAvatar = obj.getString("lastCommentUserAvatar").isEmpty() ? "" : ApiUtil.ImageUrl + obj.getString("lastCommentUserAvatar");
            } catch (JSONException e) {
                lastCommentUserAvatar = "";
            }
            try {
                votCnt = obj.getInt("votCnt");
            } catch (JSONException e) {
                votCnt = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
