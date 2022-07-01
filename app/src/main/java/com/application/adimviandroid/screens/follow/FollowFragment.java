package com.application.adimviandroid.screens.follow;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.application.adimviandroid.adapter.TagAdapter;
import com.application.adimviandroid.adapter.WallAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByCategoryFragment;
import com.application.adimviandroid.screens.post.PostListByTagFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.FollowSegmentType;
import com.application.adimviandroid.types.MuroCellType;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FollowFragment extends Fragment {

    public static final int TAB_POSITION = 1;

    private MainActivity mActivity;

    private TextView txtTitle, txtNoDataPost, txtNoDataMuro, txtNoDataTag, txtLoadMore;
    private RecyclerView rclPublication, rclEtiquetas, rclRemuro, rclTag;
    private LinearLayout lltEtiquestas;
    private ShimmerFrameLayout shimer;
    private TabLayout segementTab;
    private SwipeRefreshLayout swpRclPost;

    private List<TagModel> favouriteTags = new ArrayList<>();
    private List<PostModel> mPublications = new ArrayList<>();
    private List<PostModel> mFollowingTagPosts = new ArrayList<>();
    private List<MuroModel> mMuros = new ArrayList<>();

    private PostAdapter mPublicationAdapter;
    private PostAdapter mFollowingAdapter;
    private WallAdapter mWallAdapter;
    private TagAdapter mTagAdapter;

    private LinearLayoutManager mPublicationLayoutManger;

    private ImageDialog imageDialog;

    private boolean isLoadingMore = true;
    private boolean isPageable = true;
    private int startIndex = 0;
    private int stepCounter = 50;

    public FollowFragment() {

    }

    public FollowFragment(MainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        startIndex = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Siguiendo");
        txtNoDataPost = view.findViewById(R.id.txtNoDataPost);
        txtNoDataMuro = view.findViewById(R.id.txtNoDataMuro);
        txtNoDataTag = view.findViewById(R.id.txtNoDataTag);
        txtLoadMore = view.findViewById(R.id.txtLoadMore);
        txtLoadMore.setVisibility(View.GONE);
        shimer = view.findViewById(R.id.shimer);
        rclTag = view.findViewById(R.id.rclTags);
        rclTag.setItemAnimator(new DefaultItemAnimator());
        rclTag.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mTagAdapter = new TagAdapter(mActivity, favouriteTags, tag -> mActivity.addFragment(new PostListByTagFragment(mActivity, tag, TAB_POSITION), TAB_POSITION));
        rclTag.setAdapter(mTagAdapter);
        rclPublication = view.findViewById(R.id.rclPublication);
        mPublicationLayoutManger = new LinearLayoutManager(mActivity);
        rclPublication.setLayoutManager(new LinearLayoutManager(mActivity));
        rclPublication.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclPublication);
        swpRclPost = view.findViewById(R.id.swpRclPost);
        swpRclPost.setOnRefreshListener(() -> {
            mPublications.clear();
            startIndex = 0;
            segementTab.setVisibility(View.INVISIBLE);
            initData(view, startIndex, stepCounter);
        });
        rclRemuro = view.findViewById(R.id.rclMuro);
        rclRemuro.setLayoutManager(new LinearLayoutManager(mActivity));
        rclRemuro.setItemAnimator(new DefaultItemAnimator());
        rclEtiquetas = view.findViewById(R.id.rclEtiquetas);
        rclEtiquetas.setLayoutManager(new LinearLayoutManager(mActivity));
        rclEtiquetas.setItemAnimator(new DefaultItemAnimator());
        lltEtiquestas = view.findViewById(R.id.lltEtiquetas);
        segementTab = view.findViewById(R.id.segementTab);
        switch (AppUtil.followSegmentType.index) {
            case 0:

                segementTab.getTabAt(0).select();
                initData(view, startIndex, stepCounter);
                break;
            case 1:
                segementTab.getTabAt(1).select();
                initData(view, startIndex, stepCounter);
                break;
            case 2:
                segementTab.getTabAt(2).select();
                initData(view, startIndex, stepCounter);
                break;
        }
        handleTabBar(segementTab.getSelectedTabPosition());
        segementTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabBar(tab.getPosition());
                startIndex = 0;
                if (tab.getPosition() == 0) {
                    mPublications.clear();
                    AppUtil.followSegmentType = FollowSegmentType.FOLLOWING_POST_TEST;
                } else if (tab.getPosition() == 1) {
                    AppUtil.followSegmentType = FollowSegmentType.FOLLOWING_WALL;
                } else {
                    AppUtil.followSegmentType = FollowSegmentType.FOLLOWING_POST_TAG;
                }

                initData(view, startIndex, stepCounter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPublicationAdapter = new PostAdapter(mActivity, mPublications, PostCellType.POST_CELL_NORMAL, PostModeType.FULLTAB, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, userID, null, null, false), TAB_POSITION);
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
                        Iterator<PostModel> iter = mPublications.iterator();
                        while (iter.hasNext()) {
                            PostModel postModel = iter.next();
                            if (postModel.userID == post.userID) {
                                iter.remove();
                            }
                        }
                        startIndex = mPublications.size();
                        mPublicationAdapter.notifyDataSetChanged();
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, TAB_POSITION), TAB_POSITION);
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
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, SharedUtil.getSharedUserID(), null, repost, false), TAB_POSITION);
            }

            @Override
            public void onClickCategroy(int id, String name) {
                CategoryModel category = new CategoryModel();
                category.categoryID = id;
                category.title = name;
                mActivity.addFragment(new PostListByCategoryFragment(mActivity, category, TAB_POSITION), TAB_POSITION);
            }
        });
        rclPublication.setAdapter(mPublicationAdapter);

        mFollowingAdapter = new PostAdapter(mActivity, mFollowingTagPosts, PostCellType.POST_CELL_NORMAL, PostModeType.NOTFULLTYPE, new PostAdapter.PostAdapterListener() {
            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, userID, null, null, false), TAB_POSITION);
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
                        initData(view, startIndex, stepCounter);
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, TAB_POSITION), TAB_POSITION);
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
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, SharedUtil.getSharedUserID(), null, repost, false), TAB_POSITION);
            }

            @Override
            public void onClickCategroy(int id, String name) {
                CategoryModel category = new CategoryModel();
                category.categoryID = id;
                category.title = name;
                mActivity.addFragment(new PostListByCategoryFragment(mActivity, category, TAB_POSITION), TAB_POSITION);
            }
        });
        rclEtiquetas.setAdapter(mFollowingAdapter);

        mWallAdapter = new WallAdapter(mActivity, mMuros, MuroCellType.SIGUIENDO, new WallAdapter.WallAdapterListener() {
            @Override
            public void onClickUserAvatar(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, userID, null, null, false), TAB_POSITION);
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, TAB_POSITION), TAB_POSITION);
            }

            @Override
            public void onClickComment(int postID, int toUserID) {
                mActivity.addFragment(new WallCommentFragment(mActivity, postID, toUserID, TAB_POSITION), TAB_POSITION);
            }

            @Override
            public void onClickLike(int postID) {
                onCallSetFavorite(postID, view);
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
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, SharedUtil.getSharedUserID(), rewall, null, false), TAB_POSITION);
            }

            @Override
            public void onClickTag(TagModel tagModel) {
                mActivity.addFragment(new PostListByTagFragment(mActivity, tagModel, TAB_POSITION), TAB_POSITION);
            }
        });
        rclRemuro.setAdapter(mWallAdapter);

        rclPublication.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && isPageable) {
                    int totalCount = recyclerView.getLayoutManager().getItemCount();
                    int visisbleItemCount = recyclerView.getLayoutManager().getChildCount();
                    int pastVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (isLoadingMore) {
                        if ((visisbleItemCount + pastVisibleItems) == totalCount) {
                            isLoadingMore = false;
                            startIndex += stepCounter;
                            txtLoadMore.setVisibility(View.VISIBLE);
                            initData(view, startIndex, stepCounter);
                        }
                    }
                }
            }
        });

        initFollowTag();
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

    private void initData(View view, int startIndex, int stepCounter) {
        rclPublication = view.findViewById(R.id.rclPublication);
        rclPublication.setVisibility(View.GONE);
        rclRemuro = view.findViewById(R.id.rclMuro);
        rclRemuro.setVisibility(View.GONE);
        rclEtiquetas = view.findViewById(R.id.rclEtiquetas);
        lltEtiquestas.setVisibility(View.GONE);
        txtNoDataPost.setVisibility(View.GONE);
        txtNoDataMuro.setVisibility(View.GONE);
        txtNoDataTag.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();

        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        if (AppUtil.followSegmentType != FollowSegmentType.FOLLOWING_WALL) {
            if (AppUtil.followSegmentType == FollowSegmentType.FOLLOWING_POST_TEST) {
                param.put("limit", "" + startIndex);
                param.put("offset", "" + stepCounter);
            } else if (AppUtil.followSegmentType == FollowSegmentType.FOLLOWING_POST_TAG) {
                param.put("limit", "0");
                param.put("offset", "50");
            }
        }

        ApiUtil.onAPIConnectionResponse(AppUtil.followSegmentType.url, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                swpRclPost.setRefreshing(false);
                segementTab.setVisibility(View.VISIBLE);
                try {
                    txtNoDataPost.setVisibility(View.GONE);
                    txtNoDataTag.setVisibility(View.GONE);
                    txtNoDataMuro.setVisibility(View.GONE);
                    txtLoadMore.setVisibility(View.GONE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray arrData = new JSONArray();
                    switch (AppUtil.followSegmentType) {
                        case FOLLOWING_POST_TEST:
                            arrData = response.getJSONArray("postFollowing");
                            if (arrData.length() < stepCounter) {
                                isPageable = false;
                            }
                            if (isLoadingMore) {
                                mPublications.clear();
                            }
                            txtNoDataMuro.setVisibility(View.GONE);
                            txtNoDataPost.setVisibility(View.GONE);
                            if (arrData.length() == 0) {
                                txtNoDataPost.setVisibility(View.VISIBLE);
                            } else {
                                rclPublication.setVisibility(View.VISIBLE);
                                for (int i = 0; i < arrData.length(); i++) {
                                    PostModel post = new PostModel();
                                    post.initWithJSON(arrData.getJSONObject(i));
                                    mPublications.add(post);
                                }
                                mPublicationAdapter.notifyDataSetChanged();
                                isLoadingMore = true;
                            }
                            break;
                        case FOLLOWING_WALL:
                            txtNoDataPost.setVisibility(View.GONE);
                            txtNoDataTag.setVisibility(View.GONE);
                            arrData = response.getJSONArray("wallFollowPost");
                            if (arrData.length() == 0) {
                                txtNoDataMuro.setVisibility(View.VISIBLE);
                            } else {
                                rclRemuro.setVisibility(View.VISIBLE);
                                mMuros.clear();
                                for (int i = 0; i < arrData.length(); i++) {
                                    MuroModel muro = new MuroModel();
                                    muro.initWithJSON(arrData.getJSONObject(i));
                                    mMuros.add(muro);
                                }
                                mWallAdapter.notifyDataSetChanged();
                            }
                            break;
                        case FOLLOWING_POST_TAG:
                            txtNoDataMuro.setVisibility(View.GONE);
                            txtNoDataPost.setVisibility(View.GONE);
                            arrData = response.getJSONArray("posts");
                            if (arrData.length() == 0) {
                                txtNoDataTag.setVisibility(View.VISIBLE);
                            } else {
                                lltEtiquestas.setVisibility(View.VISIBLE);
                                mFollowingTagPosts.clear();
                                for (int i = 0; i < arrData.length(); i++) {
                                    PostModel post = new PostModel();
                                    post.initWithJSON(arrData.getJSONObject(i));
                                    mFollowingTagPosts.add(post);
                                }
                                mFollowingAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    segementTab.setVisibility(View.VISIBLE);
                    txtNoDataPost.setVisibility(View.GONE);
                    txtNoDataTag.setVisibility(View.GONE);
                    txtNoDataMuro.setVisibility(View.GONE);
                    txtLoadMore.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    switch (AppUtil.followSegmentType) {
                        case FOLLOWING_POST_TEST:
                            txtNoDataPost.setVisibility(View.VISIBLE);
                            break;
                        case FOLLOWING_WALL:
                            txtNoDataMuro.setVisibility(View.VISIBLE);
                            break;
                        case FOLLOWING_POST_TAG:
                            txtNoDataTag.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                swpRclPost.setRefreshing(false);
                segementTab.setVisibility(View.VISIBLE);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoDataPost.setVisibility(View.GONE);
                txtNoDataTag.setVisibility(View.GONE);
                txtNoDataMuro.setVisibility(View.GONE);
                txtLoadMore.setVisibility(View.GONE);
                switch (AppUtil.followSegmentType) {
                    case FOLLOWING_POST_TEST:
                        txtNoDataPost.setVisibility(View.VISIBLE);
                        break;
                    case FOLLOWING_WALL:
                        txtNoDataMuro.setVisibility(View.VISIBLE);
                        break;
                    case FOLLOWING_POST_TAG:
                        txtNoDataTag.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onEventServerError(Exception e) {
                swpRclPost.setRefreshing(false);
                segementTab.setVisibility(View.VISIBLE);
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtLoadMore.setVisibility(View.GONE);
                switch (AppUtil.followSegmentType) {
                    case FOLLOWING_POST_TEST:
                        txtNoDataPost.setVisibility(View.VISIBLE);
                        break;
                    case FOLLOWING_WALL:
                        txtNoDataMuro.setVisibility(View.VISIBLE);
                        break;
                    case FOLLOWING_POST_TAG:
                        txtNoDataTag.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void initFollowTag() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("limit", "0");
        param.put("offset", "100");

        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_FOLLOWING_TAG_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    favouriteTags.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray tagArr = response.getJSONArray("followTags");
                    for (int i = 0; i < tagArr.length(); i++) {
                        TagModel model = new TagModel();
                        model.initWithJSON(tagArr.getJSONObject(i));
                        favouriteTags.add(model);
                    }
                    mTagAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }

    private void onCallSetFavorite(int id, View view) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + id);
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("login_userId", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_WALL_FAVOURITE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                initData(view, startIndex, stepCounter);
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