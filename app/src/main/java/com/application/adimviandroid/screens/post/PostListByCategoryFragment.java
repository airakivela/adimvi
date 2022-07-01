package com.application.adimviandroid.screens.post;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PostAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostListByCategoryFragment extends Fragment {

    private CategoryModel mCategory;
    private MainActivity mActivity;
    private int tabSelected;

    private TextView txtTitle, txtNoData;
    private ImageView imgBack;
    private RecyclerView rclPost;
    private ShimmerFrameLayout shimer;
    private SwipeRefreshLayout swpRclPost;

    private List<PostModel> mPosts = new ArrayList<>();
    private PostAdapter mAdapter;

    public PostListByCategoryFragment() {
        // Required empty public constructor
    }

    public PostListByCategoryFragment(MainActivity mainActivity, CategoryModel categoryModel, int tabSelected) {
        this.mActivity = mainActivity;
        this.mCategory = categoryModel;
        this.tabSelected = tabSelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (mCategory == null) {
            mCategory = new CategoryModel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.hideShowBottomNavigationBar(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list_by_category, container, false);
        mActivity.hideShowBottomNavigationBar(false);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(mCategory.title);
        txtNoData = view.findViewById(R.id.txtNoData);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        rclPost = view.findViewById(R.id.rclPosts);
        rclPost.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPost.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclPost);
        swpRclPost = view.findViewById(R.id.swpRclPost);
        swpRclPost.setOnRefreshListener(() -> {
            initData(view);
        });
        mAdapter = new PostAdapter(mActivity, mPosts, PostCellType.POST_CELL_NORMAL, PostModeType.FULLTYPE, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabSelected, userID, null, null, false), tabSelected);
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
                        initData(view);
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabSelected), tabSelected);
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
                mActivity.addFragment(new ProfileFragment(mActivity, tabSelected, SharedUtil.getSharedUserID(), null, repost, false), tabSelected);
            }

            @Override
            public void onClickCategroy(int id, String name) {

            }
        });
        mAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rclPost.setAdapter(mAdapter);
        shimer = view.findViewById(R.id.shimer);
        initData(view);
        return view;
    }

    private void initData(View view) {
        rclPost = view.findViewById(R.id.rclPosts);
        rclPost.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        mPosts.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("categoryid", "" + mCategory.categoryID);
        param.put("offset", "200");
        param.put("limit", "0");
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_POSTS_BY_CATEGORY, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray posts = response.getJSONArray("posts");
                    if (posts.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        rclPost.setVisibility(View.GONE);
                    } else {
                        rclPost.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < posts.length(); i++) {
                            PostModel post = new PostModel();
                            post.initWithJSON(posts.getJSONObject(i));
                            mPosts.add(post);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    swpRclPost.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    txtNoData.setVisibility(View.VISIBLE);
                    rclPost.setVisibility(View.GONE);
                    swpRclPost.setRefreshing(false);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                swpRclPost.setRefreshing(false);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclPost.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                swpRclPost.setRefreshing(false);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclPost.setVisibility(View.GONE);
            }
        });
    }
}