package com.application.adimviandroid.models;

import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomModel {
    public int roomID = 0;
    public int adminID = 0;
    public String adminAvatar = "";
    public String adminName = "";
    public int background = 0;
    public int adminVerify = 0;
    public int memberCnt = 0;
    public String title = "";
    public int isSiguiendo = 0;

    public void initWithJSON(JSONObject jsonObject) {
        try {
            roomID = jsonObject.getInt("roomID");
            adminID = jsonObject.getInt("adminID");
            adminAvatar = jsonObject.getString("adminAvatar").isEmpty() ? "" : (ApiUtil.ImageUrl + jsonObject.getString("adminAvatar"));
            adminName = jsonObject.getString("adminName");
            adminVerify = jsonObject.getInt("adminVerify");
            background = AppConstant.GROUPBG.get(jsonObject.getInt("background")).bgResID;
            memberCnt = jsonObject.getInt("members");
            title = jsonObject.getString("title");
            isSiguiendo = jsonObject.getInt("siguiendo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
