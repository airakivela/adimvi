package com.application.adimviandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class NoteModel {
    public int noteID = 0;
    public String title = "";
    public String description = "";

    public void initWithJSON(JSONObject object) {
        try {
            noteID = object.getInt("noteId");
            title = object.getString("title");
            description = object.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
