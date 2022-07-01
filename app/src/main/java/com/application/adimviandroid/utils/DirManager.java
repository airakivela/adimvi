package com.application.adimviandroid.utils;

import android.os.Environment;

import com.application.adimviandroid.models.RoomMessageModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DirManager {

    private static final String APP_FOLDER_NAME = "Admivi";
    private static final String EXTENSION_M4A = ".m4a";

    public static File generateFile(int userID) {
        File file;
        file = new File(voiceoiceMessageDir(userID) + "/" + generateNewNameForAudio() + EXTENSION_M4A);
        return file;
    }

    public static String mainAppFolder() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + APP_FOLDER_NAME + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String generateNewNameForAudio() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddSSSS", Locale.US);
        return "AUD" + "-" + sdf.format(date);
    }

    public static String voiceoiceMessageDir(int userID) {
        File file = new File(mainAppFolder() + "/" + APP_FOLDER_NAME + " " + (userID == SharedUtil.getSharedUserID() ? "VoiceMessage/Sent/" : "VoiceMessage/Receive/"));
        if (!file.exists()) {
            file.mkdirs();
        }
        createNoMediaFile(file);
        return file.getAbsolutePath();
    }

    public static void createNoMediaFile(File folderPath) {
        File file = new File(folderPath + "/" + ".nomeedia");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String isExistAudioFile(RoomMessageModel model) {
        String[] fileSplit = model.extra.split("/");
        String fileName = fileSplit[fileSplit.length - 1];
        File file = new File(voiceoiceMessageDir(model.userID) + "/" + fileName);
        return file.exists() ? file.getAbsolutePath() : "";
    }
}
