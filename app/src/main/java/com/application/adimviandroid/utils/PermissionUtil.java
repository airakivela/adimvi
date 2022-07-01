package com.application.adimviandroid.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtil {

    public static final String[] PERMISSIONS_PHOTO = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    };

    public static boolean permissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static boolean hasPermissions(Context context) {
        if (context != null && PERMISSIONS_PHOTO != null) {
            for (String permission : PERMISSIONS_PHOTO) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
