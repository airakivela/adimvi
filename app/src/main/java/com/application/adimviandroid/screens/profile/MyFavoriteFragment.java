package com.application.adimviandroid.screens.profile;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PostAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByCategoryFragment;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavoriteFragment extends Fragment {

    private MainActivity mActivity;

    private TextView txtTitle, txtNoData;
    private ImageView imgBack;
    private RecyclerView rclPost;
    private ShimmerFrameLayout shimer;

    private List<PostModel> mPosts = new ArrayList<>();
    private PostAdapter mAdapter;

    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    public MyFavoriteFragment(MainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity)getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initData() {
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        txtNoData.setVisibility(View.GONE);
        rclPost.setVisibility(View.GONE);

        mPosts.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        Gson gsonObj = new Gson();
        String jsonStrParam = gsonObj.toJson(param);
        ApiUtil.onAPIConnectionRawValue(ApiUtil.GET_FAVORITE, jsonStrParam, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray favArr = response.getJSONArray("favourite");
                    if (favArr.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclPost.setVisibility(View.VISIBLE);
                        for (int i = 0; i < favArr.length(); i++) {
                            PostModel post = new PostModel();
                            post.initWithJSON(favArr.getJSONObject(i));
                            mPosts.add(post);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        rclPost = view.findViewById(R.id.rclPosts);
        rclPost.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPost.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PostAdapter(mActivity, mPosts, PostCellType.POST_CELL_NORMAL, PostModeType.NOTFULLTYPE, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, ProfileFragment.TAB_POSITION, userID, null, null, false), ProfileFragment.TAB_POSITION);
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, ProfileFragment.TAB_POSITION), ProfileFragment.TAB_POSITION);
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
                mActivity.addFragment(new ProfileFragment(mActivity, ProfileFragment.TAB_POSITION, SharedUtil.getSharedUserID(), null, repost, false), ProfileFragment.TAB_POSITION);
            }

            @Override
            public void onClickCategroy(int id, String name) {
                CategoryModel category = new CategoryModel();
                category.categoryID = id;
                category.title = name;
                mActivity.addFragment(new PostListByCategoryFragment(mActivity, category, ProfileFragment.TAB_POSITION), ProfileFragment.TAB_POSITION);
            }
        });
        rclPost.setAdapter(mAdapter);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Mis favoritos");
        txtNoData = view.findViewById(R.id.txtNoData);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
    }
}