package com.application.adimviandroid.screens.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.UserModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private View content;
    private TextInputEditText edtUserName, edtUserEmail, edtUserPassword, edtUserRePassword;
    private CheckBox chkTerms;

    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUIView();
        initFcmToken();
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

    private void initUIView() {
        content = findViewById(R.id.content);
        edtUserName = findViewById(R.id.edt_name);
        edtUserEmail = findViewById(R.id.edt_email);
        edtUserPassword = findViewById(R.id.edt_password);
        edtUserRePassword = findViewById(R.id.edt_repassword);
        chkTerms = findViewById(R.id.chk_terms);
    }

    public void onClickRegisterUB(View view) {
        String userName = edtUserName.getText().toString();
        String userEmail = edtUserEmail.getText().toString();
        String userPassword = edtUserPassword.getText().toString();
        String userRePassword = edtUserRePassword.getText().toString();
        if (!chkTerms.isChecked()) {
            BannerUtil.onShowWaringAlert(content, "Consulte los Términos y la privacidad.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (userName.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, escribe tu nombre.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (userEmail.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, introduce tu correo electrónico.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (!StringUtil.isValideEmail(userEmail)) {
            BannerUtil.onShowWaringAlert(content, "Su dirección de correo electrónico tiene un formato inválido.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (userPassword.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, introduce tu correo electrónico.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (userRePassword.isEmpty()) {
            BannerUtil.onShowWaringAlert(content, "Por favor, repite tu contraseña.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        if (!userPassword.equals(userRePassword)) {
            BannerUtil.onShowWaringAlert(content, "Las contraseñas no coinciden.", AppConstant.SHOW_BANNER_TIME);
            return;
        }
        onCallRegisterAPI(userName, userEmail, userPassword);
    }

    private void onCallRegisterAPI(String userName, String userEmail, String userPassword) {
        Map<String, String> param = new HashMap<>();
        param.put("handle", userName);
        param.put("email", userEmail);
        param.put("passcheck", userPassword);
        param.put("fcm_id", fcmToken);
        param.put("createdip", "1111111");

        Gson gsonObj = new Gson();
        String jsonStrParam = gsonObj.toJson(param);
        ProgressDialog dialog = AppUtil.onShowProgressDialog(this, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionRawValue(ApiUtil.USER_REGISTER, jsonStrParam, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    int code = obj.getInt("code");
                    if (code == 200) {
                        JSONObject response = obj.getJSONObject("response").getJSONObject("user");
                        SharedUtil.setSharedUserID(response.getInt("userid"));
                        SharedUtil.setSharedUserLoggedin(true);
                        BannerUtil.onShowSuccessAlertEventWithCallback(content, "Success", AppConstant.SHOW_BANNER_TIME, () -> {
                            AppUtil.showOtherActivity(RegisterActivity.this, MainActivity.class, -1);
                            finish();
                        });
                    } else {
                        BannerUtil.onShowWaringAlert(content, "Este nombre de usuario ya está registrado.", AppConstant.SHOW_BANNER_TIME);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void onClickInforUB(View view) {
        onLoadWebView("Información", "https://www.adimvi.com/?qa=Informaci%C3%B3n");
    }

    public void onClickPrivacyUB(View view) {
        onLoadWebView("Términos y política de privacidad", "https://www.adimvi.com/?qa=T%C3%A9rminos-y-privacidad");
    }

    public void onClickContactUB(View view) {
        AppUtil.showOtherActivity(this, ContactActivity.class, -1);
    }

    public void onClickBackUB(View view) {
        onBackPressed();
    }

    public void onClickEntar(View view) {
        onBackPressed();
    }

    public void onCLickTerms(View view) {
        onLoadWebView("Términos y Condiciones", "https://www.adimvi.com/?qa=T%C3%A9rminos-y-privacidad");
    }

    private void onLoadWebView(String title, String url) {
        Intent intent = new Intent(this, AuthWebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }

}