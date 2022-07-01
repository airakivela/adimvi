package com.application.adimviandroid.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.publish.AddPostFragment;
import com.application.adimviandroid.screens.follow.FollowFragment;
import com.application.adimviandroid.screens.home.HomeFragment;
import com.application.adimviandroid.screens.home.NotificationFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.publish.PublishFragment;
import com.application.adimviandroid.utils.SharedUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *
 * Pager adapter that keeps state of the fragments inside the bottom page navigation tabs
 *
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> BASE_FRAGMENTS;

    private static final int HOME_POSITION = 0;
    private static final int FOLLOW_POSITION = 1;
    private static final int PUBLISH_POSITION = 2;
    //    private static final int SALES_POSITION = 3;
    private static final int NOTIFICAITON_POSITION = 3;
    private static final int PROFILE_POSITION = 4;

    private List<Fragment> mHomeFragment;
    private List<Fragment> mFollowFragment;
    private List<Fragment> mPublishFragment;
    //    private List<Fragment> mSalesFragment;
    private List<Fragment> mNotificationFragment;
    private List<Fragment> mProfileFragment;

    public MainFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, MainActivity mainActivity) {
        super(fragmentManager);
        mHomeFragment = new ArrayList<>();
        mFollowFragment = new ArrayList<>();
        mPublishFragment = new ArrayList<>();
//        mSalesFragment = new ArrayList<>();
        mNotificationFragment = new ArrayList<>();
        mProfileFragment = new ArrayList<>();
        BASE_FRAGMENTS = Arrays.asList(
                new HomeFragment(mainActivity),
                new FollowFragment(mainActivity),
                new PublishFragment(mainActivity, PUBLISH_POSITION, 0, 0),
//                new SalesFragment(mainActivity, "Ventas"),
                new NotificationFragment(mainActivity),
                new ProfileFragment(mainActivity, PROFILE_POSITION, SharedUtil.getSharedUserID(), null, null, false)
        );
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        if (position == HOME_POSITION) {
            if (mHomeFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mHomeFragment.get(mHomeFragment.size() - 1);
        } else if (position == FOLLOW_POSITION) {
            if (mFollowFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mFollowFragment.get(mFollowFragment.size() - 1);
        } else if (position == PUBLISH_POSITION) {
            if (mPublishFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mPublishFragment.get(mPublishFragment.size() - 1);
        } else if (position == NOTIFICAITON_POSITION) {
            if (mNotificationFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mNotificationFragment.get(mNotificationFragment.size() - 1);
        }
//        else if (position == SALES_POSITION) {
//            if (mSalesFragment.isEmpty()) {
//                return BASE_FRAGMENTS.get(position);
//            }
//            return mSalesFragment.get(mSalesFragment.size() - 1);
//        }
        else {
            if (mProfileFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mProfileFragment.get(mProfileFragment.size() - 1);
        }
    }

    @Override
    public int getCount() {
        return BASE_FRAGMENTS.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == HOME_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return HOME_POSITION;
        } else if (position == FOLLOW_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return FOLLOW_POSITION;
        } else if (position == PUBLISH_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return PUBLISH_POSITION;
        }
//        else if (position == SALES_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
//            return SALES_POSITION;
//        }
        else if (position == NOTIFICAITON_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return NOTIFICAITON_POSITION;
        } else  if (position == PROFILE_POSITION && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return PROFILE_POSITION;
        }

        return getItem(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
    //endregion

    //region helper methods
    public void updateFragment(Fragment fragment, int position) {
        if (!BASE_FRAGMENTS.contains(fragment)) {
            addInnerFragment(fragment, position);
        }
        notifyDataSetChanged();
    }

    public boolean removeFragment(Fragment fragment, int position) {
        if (position == HOME_POSITION) {
            if (mHomeFragment.contains(fragment)) {
                removeInnerFragment(fragment, mHomeFragment);
                return true;
            }
        } else if (position == FOLLOW_POSITION) {
            if (mFollowFragment.contains(fragment)) {
                removeInnerFragment(fragment, mFollowFragment);
                return true;
            }
        } else if (position == PUBLISH_POSITION) {
            if (mPublishFragment.contains(fragment)) {
                removeInnerFragment(fragment, mPublishFragment);
                return true;
            }
        } else if (position == NOTIFICAITON_POSITION) {
            if (mNotificationFragment.contains(fragment)) {
                removeInnerFragment(fragment, mNotificationFragment);
                return true;
            }
        }
//        else if (position == SALES_POSITION) {
//            if (mSalesFragment.contains(fragment)) {
//                removeInnerFragment(fragment, mSalesFragment);
//                return true;
//            }
//        }
        else if (position == PROFILE_POSITION) {
            if (mProfileFragment.contains(fragment)) {
                removeInnerFragment(fragment, mProfileFragment);
                return true;
            }
        }

        return false;
    }

    private void removeInnerFragment(Fragment fragment, List<Fragment> tabFragments) {
        tabFragments.remove(fragment);
        notifyDataSetChanged();
    }

    private void addInnerFragment(Fragment fragment, int position) {
        if (position == HOME_POSITION) {
            mHomeFragment.add(fragment);
        } else if (position == FOLLOW_POSITION) {
            mFollowFragment.add(fragment);
        } else if (position == PUBLISH_POSITION) {
            mPublishFragment.add(fragment);
        } else if (position == NOTIFICAITON_POSITION) {
            mNotificationFragment.add(fragment);
        }
//        else if (position == SALES_POSITION) {
//            mSalesFragment.add(fragment);
//        }
        else if (position == PROFILE_POSITION) {
            mProfileFragment.add(fragment);
        }
    }
}