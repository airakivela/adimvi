package com.application.adimviandroid.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.application.adimviandroid.screens.auth.onboarding.OnboardingItemFragment;
import com.application.adimviandroid.screens.auth.onboarding.OnboardingLastFragment;

public class OnboardingAdapter extends FragmentPagerAdapter {

    public OnboardingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 3) {
            return OnboardingLastFragment.newInstance();
        } else {
            return OnboardingItemFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
