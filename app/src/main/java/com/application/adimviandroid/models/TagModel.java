package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class TagModel {
    public String tagTitle = "";
    public int tagID = 0;

    public void initWithJSON(JSONObject jsonObject) {
        try {
            try {
                tagTitle = jsonObject.getString("tags");
            } catch (JSONException e) {
                tagTitle = jsonObject.getString("tag");
            }
            tagID = jsonObject.getInt("tagid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
