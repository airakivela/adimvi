package com.application.adimviandroid.screens;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.application.adimviandroid.R;
import com.application.adimviandroid.screens.auth.LoginActivity;
import com.application.adimviandroid.screens.auth.onboarding.OnboradingActivity;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.PermissionUtil;
import com.application.adimviandroid.utils.SharedUtil;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::onNextActivity, 1500);
    }

    private void onNextActivity() {
        if (PermissionUtil.hasPermissions(SplashActivity.this)) {
            gotoNextScreen();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, PermissionUtil.PERMISSIONS_PHOTO, PERMISSION_REQUEST_CODE);
        }
    }

    private void gotoNextScreen() {
        if (SharedUtil.getSharedUserRegistered()) {
            if (SharedUtil.getSharedUserLoggedin()) {
                AppUtil.showOtherActivity(this, MainActivity.class, -1);
            } else {
                AppUtil.showOtherActivity(this, LoginActivity.class, -1);
            }
        } else {
            AppUtil.showOtherActivity(this, OnboradingActivity.class, -1);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.permissionsGranted(grantResults)) {
            gotoNextScreen();
        } else {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}