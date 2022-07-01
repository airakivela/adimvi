package com.application.adimviandroid.screens.profile.viewpager;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PostAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByCategoryFragment;
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

public class PostFragment extends Fragment {

    private MainActivity mActivity;
    private int selectedUserID;
    private int tabIndex;
    private PostFragmentListener mListener;

    private RecyclerView rclPublication;
    private TextView txtNoData;
    private ShimmerFrameLayout shimer;

    private List<PostModel> mPosts = new ArrayList<>();
    private PostAdapter mAdpater;

    public PostFragment() {
    }

    public PostFragment(MainActivity mActivity, int selectedUserID, int tabIndex, PostFragmentListener listener) {
        this.mActivity = mActivity;
        this.selectedUserID = selectedUserID;
        this.tabIndex = tabIndex;
        this.mListener = listener;
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
        View view =  inflater.inflate(R.layout.fragment_post, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        rclPublication = view.findViewById(R.id.rclPublication);
        rclPublication.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPublication.setItemAnimator(new DefaultItemAnimator());
        mAdpater = new PostAdapter(mActivity, mPosts, PostCellType.POST_CELL_PROFILE, PostModeType.NOTFULLTYPE, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                if (tabIndex == ProfileFragment.TAB_POSITION && userID == SharedUtil.getSharedUserID()) {
                    return;
                }
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
                        initData();
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
                if (tabIndex == ProfileFragment.TAB_POSITION && selectedUserID == SharedUtil.getSharedUserID()) {
                    mListener.onClickRemuroButton(repost);
                } else {
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, SharedUtil.getSharedUserID(), null, repost, false), tabIndex);
                }
            }

            @Override
            public void onClickCategroy(int id, String name) {
                CategoryModel category = new CategoryModel();
                category.categoryID = id;
                category.title = name;
                mActivity.addFragment(new PostListByCategoryFragment(mActivity, category, tabIndex), tabIndex);
            }
        });
        rclPublication.setAdapter(mAdpater);
        txtNoData = view.findViewById(R.id.txtNoData);
    }

    private void initData() {
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        rclPublication.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        mPosts.clear();

        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + selectedUserID);
        param.put("limit", "0");
        param.put("offset", "50");
        param.put("loggedin_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_USER_PUBLICATION_NEW, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray posts = response.getJSONArray("posts");
                    if (posts.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        rclPublication.setVisibility(View.GONE);
                    } else {
                        rclPublication.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < posts.length(); i++) {
                            PostModel post = new PostModel();
                            post.initWithJSON(posts.getJSONObject(i));
                            mPosts.add(post);
                        }
                        mAdpater.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    txtNoData.setVisibility(View.VISIBLE);
                    rclPublication.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclPublication.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclPublication.setVisibility(View.GONE);
            }
        });
    }

    public interface PostFragmentListener {
        void onClickRemuroButton(RepostModel repost);
    }
}