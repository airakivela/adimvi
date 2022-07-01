package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class MentionUserModel {
    public int id = 0;
    public String name = "";
    public String userAvatar = "";

    public void initWithJSON(JSONObject object) {
        try {
            id = object.getInt("id");
            name = object.getString("name");
            userAvatar = object.getString("avatarblobid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
