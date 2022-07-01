package com.application.adimviandroid.screens.post;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.PostAdapter;
import com.application.adimviandroid.adapter.WallAdapter;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.follow.WallCommentFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.MuroCellType;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.ui.ImageDialog;
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

public class PostListByTagFragment extends Fragment {

    private MainActivity mActivity;
    private TagModel mTag;
    private int tabSelected;

    private LinearLayout lltData, lltSeguir, lltSeguindo;
    private RecyclerView rclPost, rclMuro;
    private ShimmerFrameLayout shimer;
    private TextView txtNoDataPost, txtNoDataMuro, txtTitle;
    private ImageView imgBack;
    private TabLayout segementTab;
    private RelativeLayout rlData, rlPost, rlMuro;

    private List<PostModel> mPosts = new ArrayList<>();
    private List<MuroModel> mMuros = new ArrayList<>();
    private PostAdapter mPostAdapter;
    private WallAdapter mMuroAdapter;

    private int selectedTabIndex = 0;

    private ImageDialog imageDialog;

    public PostListByTagFragment() {
        // Required empty public constructor
    }

    public PostListByTagFragment(MainActivity mainActivity, TagModel tagModel, int tabSelected) {
        this.mActivity = mainActivity;
        this.mTag = tagModel;
        this.tabSelected = tabSelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (mTag == null) {
            mTag = new TagModel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (imageDialog != null && imageDialog.isShowing()) {
            imageDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list_by_tag, container, false);
        shimer = view.findViewById(R.id.shimer);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(mTag.tagTitle);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        lltData = view.findViewById(R.id.lltData);
        lltSeguir = view.findViewById(R.id.lltSeguir);
        lltSeguir.setOnClickListener(v -> {
            lltSeguir.setVisibility(View.GONE);
            lltSeguindo.setVisibility(View.VISIBLE);
            onCallWebServiceFollowTags();
        });
        lltSeguindo = view.findViewById(R.id.lltSiguiendo);
        lltSeguindo.setOnClickListener(v -> {
            lltSeguindo.setVisibility(View.GONE);
            lltSeguir.setVisibility(View.VISIBLE);
            onCallWebServiceFollowTags();
        });
        txtNoDataPost = view.findViewById(R.id.txtNoDataPost);
        txtNoDataMuro = view.findViewById(R.id.txtNoDataMuro);
        rlData = view.findViewById(R.id.rlData);
        rlPost = view.findViewById(R.id.rlPost);
        rlMuro = view.findViewById(R.id.rlMuro);
        segementTab = view.findViewById(R.id.segementTab);
        segementTab.getTabAt(0).select();
        handleTabBar(segementTab.getSelectedTabPosition());
        segementTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabBar(segementTab.getSelectedTabPosition());
                selectedTabIndex = tab.getPosition();
                initData(selectedTabIndex);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        rclPost = view.findViewById(R.id.rclPosts);
        rclPost.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPost.setItemAnimator(new DefaultItemAnimator());
        mPostAdapter = new PostAdapter(mActivity, mPosts, PostCellType.POST_CELL_NORMAL, PostModeType.NOTFULLTYPE, new PostAdapter.PostAdapterListener() {
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
                        initData(selectedTabIndex);
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
        rclPost.setAdapter(mPostAdapter);

        rclMuro = view.findViewById(R.id.rclMuros);
        rclMuro.setLayoutManager(new LinearLayoutManager(mActivity));
        rclMuro.setItemAnimator(new DefaultItemAnimator());
        mMuroAdapter = new WallAdapter(mActivity, mMuros, MuroCellType.SIGUIENDO, new WallAdapter.WallAdapterListener() {
            @Override
            public void onClickUserAvatar(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabSelected, userID, null, null, false), tabSelected);
            }

            @Override
            public void onClickPostImage(Bitmap bitmap) {
                imageDialog = new ImageDialog(mActivity, bitmap);
                imageDialog.show();
            }

            @Override
            public void onClickOriginWallImage(Bitmap bitmap) {
                imageDialog = new ImageDialog(mActivity, bitmap);
                imageDialog.show();
            }

            @Override
            public void onClickOriginPost(int postID) {
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabSelected), tabSelected);
            }

            @Override
            public void onClickComment(int postID, int toUserID) {
                mActivity.addFragment(new WallCommentFragment(mActivity, postID, toUserID, tabSelected), tabSelected);
            }

            @Override
            public void onClickLike(int postID) {
                onCallSetFavorite(postID);
            }

            @Override
            public void onClickRemuro(MuroModel model) {
                RewallModel rewall = new RewallModel();
                rewall.id = model.messageID;
                rewall.userAvatar = model.userAvatar;
                rewall.imageUrl = model.imageUrl;
                rewall.created = model.created;
                rewall.verify = model.verify;
                rewall.username = model.username;
                rewall.content = model.content;
                mActivity.addFragment(new ProfileFragment(mActivity, tabSelected, SharedUtil.getSharedUserID(), rewall, null, false), tabSelected);
            }

            @Override
            public void onClickTag(TagModel tagModel) {
                if (tagModel.tagID == mTag.tagID) {
                    return;
                }
                mActivity.addFragment(new PostListByTagFragment(mActivity, tagModel, tabSelected), tabSelected);
            }
        });
        rclMuro.setAdapter(mMuroAdapter);

        initData(segementTab.getSelectedTabPosition());

        return view;
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

    private void initData(int selectedTab) {
        lltData.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        mPosts.clear();
        mMuros.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("tagid", "" + mTag.tagID);
        param.put("limit", "0");
        param.put("offset", "200");
        if (selectedTab == 1) {
            param.put("tagName", mTag.tagTitle);
        }
        ApiUtil.onAPIConnectionResponse(selectedTab == 0 ? ApiUtil.GET_POSTS_BY_TAG : ApiUtil.GET_MUROS_BY_TAG, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                lltData.setVisibility(View.VISIBLE);
                try {
                    int tagSatus = obj.getInt("tag_follow");
                    if (tagSatus == 1) {
                        lltSeguindo.setVisibility(View.VISIBLE);
                        lltSeguir.setVisibility(View.GONE);
                    } else {
                        lltSeguindo.setVisibility(View.GONE);
                        lltSeguir.setVisibility(View.VISIBLE);
                    }
                    JSONObject response = obj.getJSONObject("response");
                    if (selectedTab == 0) {
                        rclMuro.setVisibility(View.GONE);
                        txtNoDataMuro.setVisibility(View.GONE);
                        JSONArray posts = response.getJSONArray("posts");
                        if (posts.length() == 0) {
                            txtNoDataPost.setVisibility(View.VISIBLE);
                            rclPost.setVisibility(View.GONE);
                        } else {
                            rclPost.setVisibility(View.VISIBLE);
                            txtNoDataPost.setVisibility(View.GONE);
                            for (int i = 0; i < posts.length(); i++) {
                                PostModel post = new PostModel();
                                post.initWithJSON(posts.getJSONObject(i));
                                mPosts.add(post);
                            }
                            mPostAdapter.notifyDataSetChanged();
                        }
                    } else {
                        rclPost.setVisibility(View.GONE);
                        txtNoDataPost.setVisibility(View.GONE);
                        JSONArray posts = response.getJSONArray("wallPost");
                        if (posts.length() == 0) {
                            txtNoDataMuro.setVisibility(View.VISIBLE);
                            rclMuro.setVisibility(View.GONE);
                        } else {
                            rclMuro.setVisibility(View.VISIBLE);
                            txtNoDataMuro.setVisibility(View.GONE);
                            for (int i = 0; i < posts.length(); i++) {
                                MuroModel post = new MuroModel();
                                post.initWithJSON(posts.getJSONObject(i));
                                mMuros.add(post);
                            }
                            mMuroAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                lltData.setVisibility(View.VISIBLE);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoDataPost.setVisibility(selectedTab == 0 ? View.VISIBLE : View.GONE);
                txtNoDataMuro.setVisibility(selectedTab == 1 ? View.VISIBLE : View.GONE);
                rlMuro.setVisibility(View.GONE);
                rlPost.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                lltData.setVisibility(View.VISIBLE);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoDataPost.setVisibility(selectedTab == 0 ? View.VISIBLE : View.GONE);
                txtNoDataMuro.setVisibility(selectedTab == 1 ? View.VISIBLE : View.GONE);
                rlMuro.setVisibility(View.GONE);
                rlPost.setVisibility(View.GONE);
            }
        });
    }

    private void onCallWebServiceFollowTags() {
        segementTab.getTabAt(0).select();
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("tagid", "" + mTag.tagID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_FOLLOW_POSTS_BY_TAG, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    private void onCallSetFavorite(int id) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + id);
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("login_userId", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_WALL_FAVOURITE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                initData(selectedTabIndex);
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
}