package com.application.adimviandroid.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;

public class CreditActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> onBackPressed());
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Política de créditos");
    }
}