package com.application.adimviandroid.screens.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.application.adimviandroid.models.UserModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.SharedUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.application.adimviandroid.R;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Long firstClick = 1L;
    Long secondClick = 0L;

    private View content;
    private TextInputEditText edtUserName, edtUserPassword;

    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFcmToken();
        initUIView();
    }

    private void initUIView() {
        content = findViewById(R.id.content);
        edtUserName = findViewById(R.id.edt_name);
        edtUserPassword = findViewById(R.id.edt_password);
    }

    private void initFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("Failed", "getInstanceId failed", task.getException());
                return;
            }
            fcmToken = task.getResult();
        });
    }

    public void onClickLoginUB(View view) {
        String userName = edtUserName.getText().toString();
        String userPassword = edtUserPassword.getText().toString();
        if (userName.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, escribe tu nombre.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (userPassword.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, escribe tu contraseña.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        onCallLoginAPI(userName, userPassword);
    }

    private void onCallLoginAPI(String userName, String userPassword) {
        Map<String, String> param = new HashMap<>();
        param.put("handle", userName);
        param.put("password", userPassword);
        param.put("fcm_id", fcmToken);

        Gson gsonObj = new Gson();
        String jsonStrParam = gsonObj.toJson(param);
        ProgressDialog dialog = AppUtil.onShowProgressDialog(this, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionRawValue(ApiUtil.USER_LOGIN, jsonStrParam, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    int code = obj.getInt("code");
                    if (code == 200) {
                        JSONObject response = obj.getJSONObject("response");
                        JSONObject user = response.getJSONObject("user");
                        UserModel userModel = new UserModel();
                        userModel.initUserWithJSON(user);
                        AppUtil.gUser = userModel;
                        SharedUtil.setSharedUserLoggedin(true);
                        SharedUtil.setSharedUserID(userModel.userID);
                        SharedUtil.setSharedUserName(userModel.userName);
                        finish();
                        AppUtil.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
                    } else {
                        BannerUtil.onShowWaringAlert(content, "El nombre de usuario o la contraseña son incorrectos.", AppConstant.SHOW_BANNER_TIME);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    BannerUtil.onShowWaringAlert(content, "El nombre de usuario o la contraseña son incorrectos.", AppConstant.SHOW_BANNER_TIME);
                }
                AppUtil.onDismissProgressDialog(dialog);
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
                BannerUtil.onShowWaringAlert(content, AppConstant.INTERNET_ERROR, AppConstant.SHOW_BANNER_TIME);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
                BannerUtil.onShowWaringAlert(content, AppConstant.SERVER_ERROR, AppConstant.SHOW_BANNER_TIME);
            }
        });
    }

    public void onClickForgotUB(View view) {
        onLoadWebView("Recuperar contraseña", "https://www.adimvi.com/?qa=forgot");
    }

    public void onClickRegisterUB(View view) {
        AppUtil.showOtherActivity(this, RegisterActivity.class, -1);
    }

    public void onClickInforUB(View view) {
        onLoadWebView("Información", "https://www.adimvi.com/?qa=Informaci%C3%B3n");
    }

    public void onClickPrivacyUB(View view) {
        onLoadWebView("Términos y política de privacidad", "https://www.adimvi.com/?qa=T%C3%A9rminos-y-privacidad");
    }

    public void onClickContactUB(View view) {
        AppUtil.showOtherActivity(this, ContactActivity.class, -1);
    }

    private void onLoadWebView(String title, String url) {
        Intent intent = new Intent(this, AuthWebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public void onBackPressed() {
        secondClick = System.currentTimeMillis();
        if ((secondClick - firstClick) / 1000 < 2) {
            super.onBackPressed();
        } else {
            firstClick = System.currentTimeMillis();
            BannerUtil.onShowWaringAlert(content, getString(R.string.appExit), 1500);
        }
    }
}