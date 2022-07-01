package com.application.adimviandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtil {
    private static SharedPreferences pref;
    private static final String PACKAGE = "com.application.adimviandroid";

    private static final String IS_REGISTERED = "is_registered";
    private static final String IS_LOGGEDIN = "is_loggedin";
    private static final String USERID = "userID";
    private static final String USERNAME = "userName";
    private static final String USERAD = "userAD";
    private static final String HASNEWPOST = "hasNewPost";
    private static final String USERAVATAR = "userAvatar";
    private static final String PASSGUIDE = "passguide";

    public static void setInstance(Context context) {
        pref = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getInstance() {
        return pref;
    }

    public static void setSharedUserRegistered(boolean val) {
        pref.edit().putBoolean(IS_REGISTERED, val).apply();
    }

    public static boolean getSharedUserRegistered() {
        return pref.getBoolean(IS_REGISTERED, false);
    }

    public static void setSharedUserLoggedin(boolean val) {
        pref.edit().putBoolean(IS_LOGGEDIN, val).apply();
    }

    public static boolean getSharedUserLoggedin() {
        return pref.getBoolean(IS_LOGGEDIN, false);
    }

    public static void setSharedUserID(int val) {
        pref.edit().putInt(USERID, val).apply();
    }

    public static int getSharedUserID() {
        return pref.getInt(USERID, 0);
    }

    public static void setSharedUserName(String name) {
        pref.edit().putString(USERNAME, name).apply();
    }

    public static String getSharedUserName() {
        return pref.getString(USERNAME, "");
    }

    public static void setSharedUserAD(int val) {
        pref.edit().putInt(USERAD, val).apply();
    }

    public static int getSharedUserAD() {
        return pref.getInt(USERAD, 0);
    }

    public static void setSharedHasNewPost(boolean val) {
        pref.edit().putBoolean(HASNEWPOST, val).apply();
    }

    public static boolean getSharedHasNewPost() {
        return pref.getBoolean(HASNEWPOST, false);
    }

    public static void setSharedUserAvatar(String value) {
        pref.edit().putString(USERAVATAR, value).apply();
    }

    public static String getSharedUserAvatar() {
        return pref.getString(USERAVATAR, "");
    }

    public static void setPassGuide(boolean value) {
        pref.edit().putBoolean(PASSGUIDE, value).apply();
    }

    public static boolean getPassGuide() {
        return pref.getBoolean(PASSGUIDE, false);
    }

    public static void userlogOut() {
        pref.edit().remove(IS_LOGGEDIN).apply();
        pref.edit().remove(USERID).apply();
    }
}
