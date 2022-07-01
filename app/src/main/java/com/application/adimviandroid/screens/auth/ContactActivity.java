package com.application.adimviandroid.screens.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.application.adimviandroid.R;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private View content;
    private EditText edtComment, edtUserName, edtUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initUIView();
    }

    private void initUIView() {
        content = findViewById(R.id.contnet);
        edtComment = findViewById(R.id.txt_comment);
        edtUserName = findViewById(R.id.txt_username);
        edtUserEmail = findViewById(R.id.txt_email);
    }

    public void onClickBackUB(View view) {
        onBackPressed();
    }

    public void onClickSubmitUB(View view) {
        if (SharedUtil.getSharedUserLoggedin()) {
            String strComment = edtComment.getText().toString();
            String strUserName = edtUserName.getText().toString();
            String strUserEmail = edtUserEmail.getText().toString();
            if (strComment.isEmpty()) {
                BannerUtil.onShowWaringAlert(content, "Por favor, escribe tu comentario.", AppConstant.SHOW_BANNER_TIME);
                return;
            }
            if (strUserName.isEmpty()) {
                BannerUtil.onShowWaringAlert(content, "Por favor, introduce tu nombre.", AppConstant.SHOW_BANNER_TIME);
                return;
            }
            if (strUserEmail.isEmpty()) {
                BannerUtil.onShowWaringAlert(content, "Por favor, introduce tu correo electrónico.", AppConstant.SHOW_BANNER_TIME);
                return;
            }
            if (!StringUtil.isValideEmail(strUserEmail)) {
                BannerUtil.onShowWaringAlert(content, "Su dirección de correo electrónico tiene un formato inválido.", AppConstant.SHOW_BANNER_TIME);
                return;
            }
            onCallContactAPI(strComment, strUserEmail, strUserName);
        } else {
            BannerUtil.onShowWaringAlert(content, "Debes iniciar sesión de inmediato.", AppConstant.SHOW_BANNER_TIME);
        }
    }

    private void onCallContactAPI(String strComment, String strUserEmail, String strUserName) {
        Map<String, String> param = new HashMap<>();
        param.put("comment", strComment);
        param.put("email", strUserEmail);
        param.put("username", strUserName);
        param.put("userid", String.valueOf(SharedUtil.getSharedUserID()));

        ProgressDialog dialog = AppUtil.onShowProgressDialog(this, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.CONTACT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    String message = obj.getString("message");
                    BannerUtil.onShowWaringAlert(content, message, AppConstant.SHOW_BANNER_TIME);
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
}