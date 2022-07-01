package com.application.adimviandroid.adapter;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.viewpager.MuroFragment;
import com.application.adimviandroid.screens.profile.viewpager.PerfilFragment;
import com.application.adimviandroid.screens.profile.viewpager.PostFragment;
import com.application.adimviandroid.screens.profile.viewpager.SeguidoresFragment;
import com.application.adimviandroid.screens.profile.viewpager.SiguiendoFragment;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageAdapter extends FragmentStatePagerAdapter {

    private MainActivity mActivity;
    private List<Fragment> mFragments = new ArrayList<>();


    public ProfilePageAdapter(MainActivity mainActivity, FragmentManager fragmentManager, List<Fragment> fragments) {
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
