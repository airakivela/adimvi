package com.application.adimviandroid.screens.auth.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.OnboardingAdapter;
import com.application.adimviandroid.screens.auth.LoginActivity;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class OnboradingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private DotsIndicator pageIndicator;
    private TextView skip, next;
    private View content;

    private int currentPage = 0;
    private OnboardingAdapter adapter;


    Long firstClick = 1L;
    Long secondClick = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onborading);

        initUIView();
    }

    private void initUIView() {
        content = findViewById(R.id.content);
        viewPager = findViewById(R.id.viewPager);
        pageIndicator = findViewById(R.id.page_indicator);
        skip = findViewById(R.id.txt_skip);
        next = findViewById(R.id.txt_continue);
        adapter = new OnboardingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        pageIndicator.setViewPager(viewPager);

        skip.setOnClickListener(v -> {
            gotoLoginScreen();
        });
        next.setOnClickListener(v -> {
            if (currentPage == 3) {
                gotoLoginScreen();
            } else {
                viewPager.setCurrentItem(currentPage + 1);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                handleUI(currentPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        handleUI(currentPage);
    }

    private void gotoLoginScreen() {
        SharedUtil.setSharedUserRegistered(true);
        AppUtil.showOtherActivity(this, LoginActivity.class, -1);
        finish();
    }

    private void handleUI(int currentPage) {
        if (currentPage == 0) {
            skip.setVisibility(View.INVISIBLE);
        } else {
            skip.setVisibility(View.VISIBLE);
        }
    }

    @Override
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