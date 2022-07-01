package com.application.adimviandroid.screens.post;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PostAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.FollowSegmentType;
import com.application.adimviandroid.types.HomeExplorType;
import com.application.adimviandroid.types.HomeExploreSegmentType;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostActivePostListFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private int tabIndex;
    private HomeExplorType explorType;

    private ImageView imgBack;
    private TextView txtTitle, txtNoData;
    private RecyclerView rclPosts;
    private ShimmerFrameLayout shimer;
    private TabLayout segementTab;
    private SwipeRefreshLayout swpRclPost;

    private List<PostModel> mPosts = new ArrayList<>();
    private PostAdapter postAdapter;

    public MostActivePostListFragment() {
        // Required empty public constructor
    }

    public MostActivePostListFragment(MainActivity mainActivity, int tabIndex, HomeExplorType type) {
        this.mActivity = mainActivity;
        this.tabIndex = tabIndex;
        this.explorType = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (explorType == null) {
            explorType = HomeExplorType.MOSTTRENDING;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_most_active, container, false);
        mActivity.hideShowBottomNavigationBar(false);
        initUIView(view);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.hideShowBottomNavigationBar(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtNoData = view.findViewById(R.id.txtNoData);
        txtTitle.setText(explorType.title);
        shimer = view.findViewById(R.id.shimer);
        rclPosts = view.findViewById(R.id.rclPosts);
        rclPosts.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPosts.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclPosts);
        swpRclPost = view.findViewById(R.id.swpRclPost);
        swpRclPost.setOnRefreshListener(() -> initData(view));
        postAdapter = new PostAdapter(mActivity, mPosts, PostCellType.POST_CELL_NORMAL, PostModeType.FULLTYPEMARGIN, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
            }

            @Override
            public void onClickFollow(PostModel post) {
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                Map<String, String> parma = new HashMap<>();
                parma.put("userid", "" + SharedUtil.getSharedUserID());
                parma.put("entityid", "" + post.userID);
                ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        dialog.dismiss();
                        if (post.postFollow == 1) {
                            post.postFollow = 0;
                        } else {
                            post.postFollow = 1;
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onEventInternetError(Exception e) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onEventServerError(Exception e) {
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onClickPostImage(int postID) {
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex);
            }

            @Override
            public void onClickShare(String shareURL) {
                AppUtil.showShareLink(mActivity, shareURL);
            }

            @Override
            public void onClickRemuroUB(PostModel post) {
                RepostModel repost = new RepostModel();
                repost.id = post.postID;
                repost.created = post.postDate;
                repost.title = post.title;
                repost.userAvatar = post.postImg;
                repost.verify = post.verifiy;
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, SharedUtil.getSharedUserID(), null, repost, false), tabIndex);
            }

            @Override
            public void onClickCategroy(int id, String name) {
                CategoryModel category = new CategoryModel();
                category.categoryID = id;
                category.title = name;
                mActivity.addFragment(new PostListByCategoryFragment(mActivity, category, tabIndex), tabIndex);
            }
        });
        postAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rclPosts.setAdapter(postAdapter);
        imgBack.setOnClickListener(this);

        segementTab = view.findViewById(R.id.segementTab);
        switch (AppUtil.homeExploreSegmentType.index) {
            case 0:
                segementTab.getTabAt(0).select();
                initData(view);
                break;
            case 1:
                segementTab.getTabAt(1).select();
                initData(view);
                break;
            case 2:
                segementTab.getTabAt(2).select();
                initData(view);
                break;
        }
        handleTabBar(segementTab.getSelectedTabPosition());
        segementTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabBar(segementTab.getSelectedTabPosition());
                if (tab.getPosition() == 0) {
                    AppUtil.homeExploreSegmentType = HomeExploreSegmentType.HOY;
                } else if (tab.getPosition() == 1) {
                    AppUtil.homeExploreSegmentType = HomeExploreSegmentType.SEMANA;
                } else {
                    AppUtil.homeExploreSegmentType = HomeExploreSegmentType.MES;
                }

                initData(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void handleTabBar(int selectedIndex) {
        for (int i = 0; i < segementTab.getTabCount(); i++) {
            LinearLayout tabLayout = (LinearLayout)((ViewGroup) segementTab.getChildAt(0)).getChildAt(i);
            TextView tabTextView = (TextView) tabLayout.getChildAt(1);
            if (i == selectedIndex) {
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
            } else {
                tabTextView.setTypeface(Typeface.DEFAULT);
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.NORMAL);
            }
        }
    }

    private void initData(View view) {
        rclPosts = view.findViewById(R.id.rclPosts);
        rclPosts.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        segementTab.setVisibility(View.GONE);
        shimer.startShimmer();
        mPosts.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("type", "" + AppUtil.homeExploreSegmentType.index);
        param.put("per_page", "100");
        param.put("current_page", "0");
        ApiUtil.onAPIConnectionResponse(explorType.url, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    segementTab.setVisibility(View.VISIBLE);
                    swpRclPost.setRefreshing(false);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray posts = response.getJSONArray("posts");
                    if (posts.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        rclPosts.setVisibility(View.GONE);
                    } else {
                        rclPosts.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < posts.length(); i++) {
                            PostModel post = new PostModel();
                            post.initWithJSON(posts.getJSONObject(i));
                            mPosts.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    swpRclPost.setRefreshing(false);
                    txtNoData.setVisibility(View.VISIBLE);
                    segementTab.setVisibility(View.VISIBLE);
                    rclPosts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                swpRclPost.setRefreshing(false);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                segementTab.setVisibility(View.VISIBLE);
                rclPosts.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
//                swpRclPost.setRefreshing(false);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                segementTab.setVisibility(View.VISIBLE);
                rclPosts.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                AppUtil.homeExploreSegmentType = HomeExploreSegmentType.HOY;
                mActivity.onBackPressed();
                break;
        }
    }
}