package com.application.adimviandroid.screens.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.application.adimviandroid.R;

public class AuthWebActivity extends AppCompatActivity {

    private TextView title;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_web);

        title = findViewById(R.id.txt_title);
        webView = findViewById(R.id.webUV);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            title.setText(extra.getString("title"));
            webView.loadUrl(extra.getString("url"));
        }
    }

    public void onClickBackUB(View view) {
        onBackPressed();
    }
}