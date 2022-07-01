package com.application.adimviandroid.adapter;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.application.adimviandroid.screens.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PublishPageAdapter extends FragmentStatePagerAdapter {

    private MainActivity mActivity;
    private List<Fragment> mFragments = new ArrayList<>();

    public PublishPageAdapter(MainActivity mainActivity, FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.mActivity = mainActivity;
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {

    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
