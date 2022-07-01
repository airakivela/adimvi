package com.application.adimviandroid.screens.post;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.comment.CommentCommentFragment;
import com.application.adimviandroid.screens.comment.CommentEditFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.BottomComment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostContentFragment extends Fragment implements View.OnClickListener {

    public static String KEY_POST_ID = "KEY_POST_ID";
    private MainActivity mActivity;
    private int postID, categoryID;
    private int tabIndex;

    private WebView webView;
    private ImageView imgDisableLike, imgDisableDislike, imgSelectLike, imgSelectDislike, imgSelectedLike, imgSelectedDislike, imgRemuro, imgShare, imgUser, imgBack
            , imgThumb, imgChat, imgUserFollowSatatus, imgWhiteBG;
    private TextView txtNetVote;
    private View content;
    private LinearLayout lltMain, lltBottom, lltWeb;
    private ShimmerFrameLayout shimer;
    private CardView crdLike;

    private CoordinatorLayout crdBottomSheet;
    private BottomComment bottomComment;
    private BottomSheetBehavior mBottomSheetBehavior;

    private JSONObject postData = new JSONObject();
    private int userFollowStatus = 0;
    private boolean isShowLikeUV = false;
    private int postFavoriteStatus = 0;

    public PostContentFragment() {

    }

    public PostContentFragment(MainActivity mainActivity, int postID, int categoryID, int tabIndex) {
        this.mActivity = mainActivity;
        this.postID = postID;
        this.categoryID = categoryID;
        this.tabIndex = tabIndex;
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
        View view = inflater.inflate(R.layout.fragment_post_content, container, false);
        initUIView(view);
        mActivity.hideShowBottomNavigationBar(true);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.hideShowBottomNavigationBar(true);
    }

    private void initUIView(View view) {
        imgDisableLike = view.findViewById(R.id.imgLikeDisable);
        imgDisableDislike = view.findViewById(R.id.imgDisLikeDisable);
        imgSelectLike = view.findViewById(R.id.imgLike);
        imgSelectDislike = view.findViewById(R.id.imgDisLike);
        imgSelectedLike = view.findViewById(R.id.imgLiked);
        imgSelectedDislike = view.findViewById(R.id.imgDisLiked);
        imgRemuro = view.findViewById(R.id.imgRemuro);
        imgShare = view.findViewById(R.id.imgShare);
        imgUser = view.findViewById(R.id.imgUser);
        txtNetVote = view.findViewById(R.id.txtNetVote);
        content = view.findViewById(R.id.content);
        webView = view.findViewById(R.id.webPost);
        imgBack = view.findViewById(R.id.imgBack);
        lltMain = view.findViewById(R.id.lltMain);
        shimer = view.findViewById(R.id.shimer);
        crdLike = view.findViewById(R.id.lltLike);
        imgThumb = view.findViewById(R.id.imgThumbsUp);
        imgChat = view.findViewById(R.id.imgChat);
        imgUserFollowSatatus = view.findViewById(R.id.imgUserFollow);
        lltBottom = view.findViewById(R.id.lltBottom);
        imgWhiteBG = view.findViewById(R.id.imgWhiteBG);
        lltWeb = view.findViewById(R.id.lltWeb);

        crdBottomSheet = view.findViewById(R.id.crdBottomSheet);
        bottomComment = view.findViewById(R.id.bottomComment);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomComment);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomComment.initBottomCommentView(postID, categoryID, new BottomComment.BottomCommentListener() {
            @Override
            public void onClickUserImage(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
            }

            @Override
            public void onClickComment(CommentModel comment) {
                mActivity.addFragment(new CommentCommentFragment(mActivity, comment, tabIndex), tabIndex);
            }

            @Override
            public void onClickEdit(CommentModel comment) {
                mActivity.addFragment(new CommentEditFragment(mActivity, comment), tabIndex);
            }

            @Override
            public void onClickMention(int userid) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userid, null, null, false), tabIndex);
            }
        });
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    crdBottomSheet.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    crdBottomSheet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        imgBack.setOnClickListener(this);
        imgThumb.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgSelectLike.setOnClickListener(this);
        imgSelectedLike.setOnClickListener(this);
        imgSelectDislike.setOnClickListener(this);
        imgSelectedDislike.setOnClickListener(this);
        imgDisableDislike.setOnClickListener(this);
        imgDisableLike.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        imgRemuro.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        imgChat.setOnClickListener(this);

        handleLikeUV(isShowLikeUV);
        initData();

    }

    private void handleLikeUV(boolean isShowLikeUV) {
        crdLike.setVisibility(isShowLikeUV ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        lltMain.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + postID);
        param.put("userid", "" + SharedUtil.getSharedUserID());

        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_POST_DETAIL, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONObject("response");
                    postData = response.getJSONObject("postinfo");
                    handleUV(postData);
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    lltMain.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                BannerUtil.onShowWaringAlert(content, AppConstant.INTERNET_ERROR, AppConstant.SHOW_BANNER_TIME);
            }

            @Override
            public void onEventServerError(Exception e) {
                BannerUtil.onShowWaringAlert(content, AppConstant.SERVER_ERROR, AppConstant.SHOW_BANNER_TIME);
            }
        });
    }

    private void handleUV(JSONObject postData) {
        try {
            int likeStatus = postData.getInt("like_dislike_type");
            imgSelectLike.setVisibility(View.GONE);
            imgDisableDislike.setVisibility(View.GONE);
            imgSelectedDislike.setVisibility(View.GONE);
            imgSelectDislike.setVisibility(View.GONE);
            imgSelectedLike.setVisibility(View.GONE);
            imgDisableLike.setVisibility(View.GONE);
            if (likeStatus == 1) {
                imgSelectedLike.setVisibility(View.VISIBLE);
                imgDisableDislike.setVisibility(View.VISIBLE);
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.mainGreen), PorterDuff.Mode.SRC_IN);
            } else if (likeStatus == 0) {
                imgDisableLike.setVisibility(View.VISIBLE);
                imgSelectedDislike.setVisibility(View.VISIBLE);
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.mainRed), PorterDuff.Mode.SRC_IN);
            } else {
                imgSelectLike.setVisibility(View.VISIBLE);
                imgSelectDislike.setVisibility(View.VISIBLE);
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.darkGray), PorterDuff.Mode.SRC_IN);
            }
            AppUtil.setWebViewThemeMode(mActivity, webView);
            webView.loadUrl(postData.getString("postLink"));
            userFollowStatus = postData.getInt("post_followup");
            if (postData.getInt("userid") == SharedUtil.getSharedUserID()) {
                imgUserFollowSatatus.setVisibility(View.GONE);
                imgWhiteBG.setVisibility(View.GONE);
            } else {
                imgUserFollowSatatus.setVisibility(View.VISIBLE);
                imgWhiteBG.setVisibility(View.VISIBLE);
                if (userFollowStatus == 0) {
                    imgUserFollowSatatus.setImageResource(R.drawable.ic_add_circle_fill);
                } else {
                    imgUserFollowSatatus.setImageResource(R.drawable.ic_minus_circle_fill);
                }
            }
            String userAvatar = ApiUtil.ImageUrl + postData.getString("avatarblobid");
            AppUtil.loadImageByUrl(mActivity, imgUser, userAvatar, ImagePlaceHolderType.USERIMAGE);
            postFavoriteStatus = postData.getInt("post_favourite");
            txtNetVote.setText(postData.getString("netvotes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                mActivity.onBackPressed();
                break;
            case R.id.imgUser:
                onCallSetFollowPostUser(userFollowStatus);
                break;
            case R.id.imgLiked:
                onCallSetLikePost(0);
                break;
            case R.id.imgLike:
                onCallSetLikePost(1);
                break;
            case R.id.imgDisLike:
                onCallSetLikePost(2);
                break;
            case R.id.imgDisLiked:
                onCallSetLikePost(3);
                break;
            case R.id.imgRemuro:
                RepostModel repostModel = new RepostModel();
                try {
                    repostModel.id = this.postID;
                    repostModel.verify = postData.getInt("verify");
                    repostModel.userAvatar = postData.getString("post_image");
                    repostModel.created = postData.getString("post_date");
                    repostModel.title = postData.getString("post_title");
                    repostModel.userName = postData.getString("username");
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, SharedUtil.getSharedUserID(), null, repostModel, false), tabIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgShare:
                try {
                    String shareLink = postData.getString("share_link");
                    AppUtil.showShareLink(mActivity, shareLink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgThumbsUp:
                isShowLikeUV = !isShowLikeUV;
                handleLikeUV(isShowLikeUV);
                break;
            case R.id.imgChat:
                onShowCommentOfPost();
                break;
        }
    }

    private void onShowCommentOfPost() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            crdBottomSheet.setVisibility(View.GONE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            crdBottomSheet.setVisibility(View.VISIBLE);
        }
    }

    private void onCallSetLikePost(int status) {
        String likeType = "0";
        if (status == 0) {
            likeType = "1";
            imgSelectedLike.setVisibility(View.GONE);
            imgSelectLike.setVisibility(View.VISIBLE);
            imgSelectDislike.setVisibility(View.VISIBLE);
            imgDisableDislike.setVisibility(View.GONE);
        } else if (status == 1) {
            likeType = "1";
            imgSelectLike.setVisibility(View.GONE);
            imgSelectedLike.setVisibility(View.VISIBLE);
            imgSelectDislike.setVisibility(View.GONE);
            imgDisableDislike.setVisibility(View.VISIBLE);
        } else if (status == 2) {
            likeType = "0";
            imgSelectLike.setVisibility(View.GONE);
            imgSelectDislike.setVisibility(View.GONE);
            imgSelectedDislike.setVisibility(View.VISIBLE);
            imgDisableLike.setVisibility(View.VISIBLE);
        } else {
            likeType = "0";
            imgSelectedDislike.setVisibility(View.GONE);
            imgSelectLike.setVisibility(View.VISIBLE);
            imgSelectDislike.setVisibility(View.VISIBLE);
            imgDisableLike.setVisibility(View.GONE);
        }

        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + this.postID);
        param.put("like_dislike_type", likeType);

        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_LIKE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    AppUtil.onDismissProgressDialog(dialog);
                    JSONObject response = obj.getJSONObject("response");
                    JSONObject postVote = response.getJSONObject("postVotes");
                    String vote = postVote.getString("netvotes");
                    txtNetVote.setText(vote);
                    initData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }
        });
    }

    private void onCallSetFollowPostUser(int status) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> parma = new HashMap<>();
        parma.put("userid", "" + SharedUtil.getSharedUserID());
        try {
            parma.put("entityid", "" + postData.getInt("userid"));
        } catch (JSONException e) {
            e.printStackTrace();
            parma.put("entityid", "0");
        }
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                AppUtil.onDismissProgressDialog(dialog);
                if (status == 0) {
                    userFollowStatus = 1;
                    imgUserFollowSatatus.setImageResource(R.drawable.ic_add_circle_fill);
                } else {
                    userFollowStatus = 0;
                    imgUserFollowSatatus.setImageResource(R.drawable.ic_minus_circle_fill);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }
        });
    }
}