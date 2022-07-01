package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MuroModel {
    public int messageID = 0;
    public int fromuserID = 0;
    public int touserID = 0;
    public String username = "";
    public String favourite = "0";
    public String totalFav = "0";
    public String totalComments = "0";
    public String userAvatar = "";
    public String content = "";
    public String created = "";
    public String imageUrl = "";
    public String filterDate = "";
    public int verify = 0;
    public String remuroCnt = "0";
    public RewallModel rewallModel = new RewallModel();
    public RepostModel repostModel = new RepostModel();
    public List<TagModel> tags = new ArrayList<>();
    public int hasRecentPost = 0;
    public int followStatus = 0;
    public String viewCnt = "";
    public String lastCommentUserAvatar = "";
    public int cntAnswer = 0;
    public int paid = 0;

    public void initWithJSON(JSONObject object) {
        try {
            messageID = object.getInt("messageid");
            fromuserID = object.getInt("fromuserid");
            touserID = object.getInt("touserid");
            username = object.getString("username");
            favourite = object.getString("favourite");
            totalFav = object.getString("total_favourite");
            totalComments = object.getString("totalComments");
            userAvatar = object.getString("avatarblobid").isEmpty()? "" : ApiUtil.ImageUrl + object.getString("avatarblobid");
            content = object.getString("content");
            created = object.getString("created");
            imageUrl = object.getString("imageUrl");
            filterDate = object.getString("filter_date");
            verify = object.getInt("verify");
            try {
                remuroCnt = object.getString("remuroCount");
            } catch (JSONException e) {
                remuroCnt = "0";
            }
            try {
                JSONObject rewall = object.getJSONObject("rewall");
                rewallModel.initWithJSON(rewall);
            } catch (JSONException e) {
                rewallModel = null;
            }
            try {
                JSONObject repost = object.getJSONObject("repost");
                repostModel.initWithJSON(repost);
            } catch (JSONException e) {
                repostModel = null;
            }
            hasRecentPost = object.getInt("hasRecentPost");
            try {
                JSONArray tagsArr = object.getJSONArray("tags");
                tags.clear();
                for (int i = 0; i < tagsArr.length(); i++) {
                    JSONObject tagJson = tagsArr.getJSONObject(i);
                    TagModel tag = new TagModel();
                    tag.initWithJSON(tagJson);
                    if (tag.tagTitle.isEmpty()) {
                        continue;
                    }
                    tags.add(tag);
                }
            } catch (JSONException e) {
                tags = null;
            }
            try {
                followStatus = object.getInt("postFollow");
            } catch (JSONException e) {
                followStatus = 0;
            }
            try {
                viewCnt = object.getString("views");
            } catch (JSONException e) {
                viewCnt = "0";
            }
            try {
                cntAnswer = object.getInt("cntAnswer");
            } catch (JSONException e) {
                cntAnswer = 0;
            }
            try {
                lastCommentUserAvatar = object.getString("lastCommentUserAvatar").isEmpty() ? "" : ApiUtil.ImageUrl + object.getString("lastCommentUserAvatar");
            } catch (JSONException e) {
                lastCommentUserAvatar = "";
            }
            try {
                paid = object.getInt("paid");
            } catch (JSONException e) {
                paid = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
