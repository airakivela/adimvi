package com.application.adimviandroid.screens.publish;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PublishPageAdapter;
import com.application.adimviandroid.screens.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublishFragment extends Fragment {

    public static final int TAB_POSITION = 2;

    private MainActivity mActivity;
    private int tabIndex;
    private int postID;
    private int storyID;

    private TabLayout tabBar;
    private ViewPager viewPager;
    private PublishPageAdapter mPageAdapter;
    private List<Fragment> mFragments;

    public PublishFragment() {

    }

    public PublishFragment(MainActivity mActivity, int tabIndex, int postID, int storyID) {
        this.mActivity = mActivity;
        this.tabIndex = tabIndex;
        this.postID = postID;
        this.storyID = storyID;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        tabBar = view.findViewById(R.id.tabBar);
        mFragments = new ArrayList<>(
                Arrays.asList(
                        new AddPostFragment(mActivity, postID, tabIndex),
                        new AddStoryFragment(mActivity, storyID, tabIndex)
                )
        );
        mPageAdapter = new PublishPageAdapter(mActivity, getChildFragmentManager(), mFragments);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(mPageAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabBar.setupWithViewPager(viewPager);
        tabBar.getTabAt(0).setText("Post");
        tabBar.getTabAt(1).setText("Storytime");
        if (storyID != 0) {
            viewPager.setCurrentItem(1);
        }
    }
}