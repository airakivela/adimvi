package com.application.adimviandroid.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.application.adimviandroid.screens.home.GuideItemFragment;

public class GuideAdapter extends FragmentPagerAdapter {

    public GuideAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return GuideItemFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
