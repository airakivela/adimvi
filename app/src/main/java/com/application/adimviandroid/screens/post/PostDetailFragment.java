package com.application.adimviandroid.screens.post;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.FeatureAdapter;
import com.application.adimviandroid.adapter.MentionAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.models.FeaturModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.publish.AddPostFragment;
import com.application.adimviandroid.screens.comment.CommentCommentFragment;
import com.application.adimviandroid.screens.comment.CommentEditFragment;
import com.application.adimviandroid.screens.comment.CommentListFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.profile.WalletFragment;
import com.application.adimviandroid.screens.profile.chat.MessageFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.ui.ActionSheetDialog;
import com.application.adimviandroid.ui.BottomComment;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.ui.NativeTemplateStyle;
import com.application.adimviandroid.ui.PromptDialog;
import com.application.adimviandroid.ui.SetVotePostDialog;
import com.application.adimviandroid.ui.TemplateView;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.SqlUtil;
import com.application.adimviandroid.utils.StringUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.onecode369.wysiwyg.WYSIWYG;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import per.wsj.library.AndRatingBar;

public class PostDetailFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private int postID, categoryID, tabIndex;

    private JSONObject postData = new JSONObject();

    private RelativeLayout lltMain;
    private LinearLayout lltEditPost, lltDeletePost, lltAD, lltBeforePurchase, lltAfterPurchase, lltAvatar, lltBottom, btnDelete;
    private ScrollView mainSV;
    private ImageView imgBack, imgUser, imgVerify, imgBookMark, imgThumb, imgShare, imgRemuro, imgDisableLike, imgDisableDislike, imgPlay,
            imgSelectLike, imgSelectDislike, imgSelectedLike, imgSelectedDislike, imgPost, imgPostBeforePurchase, imgPostAfterPurchase, imgUserAD, imgOption, imgChat;
    private TextView txtUserName, txtPostTitle, txtVote, txtDateInfo, txtVisitCnt, txtVotos, txtCommentCnt, txtCredit, txtPrice,
            txtPostTitleBeforePurchase, txtPostTitleAfterPurchase, btnFollow, btnUnfollow;
    private Button btnMorePost, btnReport, btnReportCancel, btnAddComment, btnSeeComment, btnCompare, btnCredit, btnViewPost;
    private CardView lltLike;
    private WebView webPost;
    private TagContainerLayout tagContainer;
    private AndRatingBar ratingBar;
    private TemplateView nativeAD;
    private RecyclerView rclRelation;
    private CheckBox checkBox;
    private SocialAutoCompleteTextView edtComment;
    private ShimmerFrameLayout shimer;

    private List<FeaturModel> features = new ArrayList<>();
    private FeatureAdapter featureAdapter;
    private List<TagModel> mTags = new ArrayList<>();
    private ArrayAdapter<MentionUserModel> mentionAdapter;

    private int ENABLED_AD = 0;
    private InterstitialAd mInterstitalAD;

    private WYSIWYG richEditor;

    private int postFavoriteStatus = 0;
    private int commentCnt = 0;
    private String userADLink = "";
    private String videoURL = "";

    private AlertDialog alertDialog;
    private PromptDialog promptDialog;
    private ImageDialog imageDialog;
    private SetVotePostDialog votePostDialog;
    private ActionSheetDialog sheetDialog;

    private CoordinatorLayout crdBottomSheet;
    private BottomComment bottomComment;
    private BottomSheetBehavior mBottomSheetBehavior;

    public PostDetailFragment() {

    }

    public PostDetailFragment(MainActivity mainActivity, int id, int tabIndex) {
        this.mActivity = mainActivity;
        this.postID = id;
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
    public void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }
        if (imageDialog != null && imageDialog.isShowing()) {
            imageDialog.dismiss();
        }
        if (votePostDialog != null && votePostDialog.isShowing()) {
            votePostDialog.dismiss();
        }
        if (sheetDialog != null && sheetDialog.isShowing()) {
            sheetDialog.dismiss();
        }
        mActivity.hideShowBottomNavigationBar(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        initUIView(view);
        onCallViewedPost();
        mActivity.hideShowBottomNavigationBar(false);
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        mainSV = view.findViewById(R.id.mainSV);

        lltMain = view.findViewById(R.id.llt_main);
        lltLike = view.findViewById(R.id.lltLike);
        lltDeletePost = view.findViewById(R.id.lltDeletePost);
        lltEditPost = view.findViewById(R.id.lltEditPost);
        lltAD = view.findViewById(R.id.lltAD);
        lltBeforePurchase = view.findViewById(R.id.lltBeforePurchase);
        lltAfterPurchase = view.findViewById(R.id.lltAfterPurchase);
        lltAvatar = view.findViewById(R.id.lltAvatar);
        lltBottom = view.findViewById(R.id.lltBottom);

        imgBack = view.findViewById(R.id.imgBack);
        imgUser = view.findViewById(R.id.img_user);
        imgVerify = view.findViewById(R.id.img_verified);
        imgBookMark = view.findViewById(R.id.imgBookMark);
        imgDisableLike = view.findViewById(R.id.imgLikeDisable);
        imgDisableDislike = view.findViewById(R.id.imgDisLikeDisable);
        imgSelectLike = view.findViewById(R.id.imgLike);
        imgSelectDislike = view.findViewById(R.id.imgDisLike);
        imgSelectedLike = view.findViewById(R.id.imgLiked);
        imgSelectedDislike = view.findViewById(R.id.imgDisLiked);
        imgPost = view.findViewById(R.id.imgPost);
        imgPostAfterPurchase = view.findViewById(R.id.imgPostAfterPurchase);
        imgPostBeforePurchase = view.findViewById(R.id.imgPostBeforePurchase);
        imgUserAD = view.findViewById(R.id.imgUserAD);
        imgPlay = view.findViewById(R.id.imgPlay);
        imgThumb = view.findViewById(R.id.imgThumbsUp);
        imgShare = view.findViewById(R.id.imgShare);
        imgRemuro = view.findViewById(R.id.imgMuro);
        imgChat = view.findViewById(R.id.imgChat);

        txtUserName = view.findViewById(R.id.txt_username);
        txtPostTitle = view.findViewById(R.id.txtPostTitle);
        txtVote = view.findViewById(R.id.txtNetVote);
        txtDateInfo = view.findViewById(R.id.txtDateInfo);
        txtVisitCnt = view.findViewById(R.id.txtVisitCnt);
        txtVotos = view.findViewById(R.id.txtVotes);
        txtCommentCnt = view.findViewById(R.id.txtComentCnt);
        txtCredit = view.findViewById(R.id.txtCredit);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtPostTitleBeforePurchase = view.findViewById(R.id.txtPostTitleBeforePurchase);
        txtPostTitleAfterPurchase = view.findViewById(R.id.txtPostTitleAfterPurchase);

        btnDelete = view.findViewById(R.id.btnDelete);
        btnFollow = view.findViewById(R.id.btnFollow);
        btnUnfollow = view.findViewById(R.id.btnUnFollow);
        btnMorePost = view.findViewById(R.id.btnMorePoset);
        btnReport = view.findViewById(R.id.btnReport);
        btnReportCancel = view.findViewById(R.id.btnReportCancel);
        btnReportCancel.setVisibility(View.GONE);
        btnSeeComment = view.findViewById(R.id.btnSeeComment);
        btnAddComment = view.findViewById(R.id.btnAddComment);
        btnViewPost = view.findViewById(R.id.btnViewPost);
        btnCredit = view.findViewById(R.id.btnCredeit);
        btnCompare = view.findViewById(R.id.btnCompare);

        webPost = view.findViewById(R.id.webPost);
        webPost.setInitialScale(1);
        tagContainer = view.findViewById(R.id.tagContainer);
        ratingBar = view.findViewById(R.id.ratingPost);
        nativeAD = view.findViewById(R.id.nativeAD);
        rclRelation = view.findViewById(R.id.rcl_related);
        rclRelation.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        featureAdapter = new FeatureAdapter(mActivity, features, id -> {
            mActivity.addFragment(new PostDetailFragment(mActivity, id, tabIndex), tabIndex);
        });
        rclRelation.setAdapter(featureAdapter);
        checkBox = view.findViewById(R.id.chkTerms);

        edtComment = view.findViewById(R.id.edtComment);
        mentionAdapter = new MentionAdapter(mActivity, AppUtil.mMentionUsers);
        edtComment.setMentionAdapter(mentionAdapter);

        imgOption = view.findViewById(R.id.imgOption);

        imgBack.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        imgBookMark.setOnClickListener(this);
        imgSelectLike.setOnClickListener(this);
        imgSelectedLike.setOnClickListener(this);
        imgSelectDislike.setOnClickListener(this);
        imgSelectedDislike.setOnClickListener(this);
        imgDisableDislike.setOnClickListener(this);
        imgDisableLike.setOnClickListener(this);
        imgUserAD.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgPost.setOnClickListener(this);
        imgOption.setOnClickListener(this);
        imgChat.setOnClickListener(this);

        btnFollow.setOnClickListener(this);
        btnUnfollow.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnMorePost.setOnClickListener(this);
        btnCompare.setOnClickListener(this);
        btnCredit.setOnClickListener(this);
        btnViewPost.setOnClickListener(this);
        btnSeeComment.setOnClickListener(this);
        btnAddComment.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        btnReportCancel.setOnClickListener(this);

        lltDeletePost.setOnClickListener(this);
        lltEditPost.setOnClickListener(this);
        imgRemuro.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        imgThumb.setOnClickListener(this);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                onCallSetRatingPost(rating);
            }
        });
        richEditor = view.findViewById(R.id.richEditor);
        richEditor.setClickable(false);
        richEditor.setInputEnabled(false);

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
    }

    private void initData() {

        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + postID);
        param.put("userid", "" + SharedUtil.getSharedUserID());

        mainSV.setVisibility(View.GONE);
        lltAfterPurchase.setVisibility(View.GONE);
        lltBeforePurchase.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();

        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_POST_DETAIL, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONObject("response");
                    postData = response.getJSONObject("postinfo");
                    handleUV(postData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.INTERNET_ERROR, AppConstant.SHOW_BANNER_TIME);
            }

            @Override
            public void onEventServerError(Exception e) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.SERVER_ERROR, AppConstant.SHOW_BANNER_TIME);
            }
        });

        Map<String, String> param1 = new HashMap<>();
        param1.put("postid", "" + postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_TAGS_BY_POST, param1, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    mTags.clear();
                    List<String> mTagsTitle = new ArrayList<>();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray tags = response.getJSONArray("tags");
                    for (int i = 0; i < tags.length(); i++) {
                        TagModel tag = new TagModel();
                        tag.initWithJSON(tags.getJSONObject(i));
                        mTagsTitle.add(tag.tagTitle);
                        mTags.add(tag);
                    }
                    tagContainer.setTags(mTagsTitle);
                    tagContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
                        @Override
                        public void onTagClick(int position, String text) {
                            mActivity.addFragment(new PostListByTagFragment(mActivity, mTags.get(position), tabIndex), tabIndex);
                        }

                        @Override
                        public void onTagLongClick(int position, String text) {

                        }

                        @Override
                        public void onSelectedTagDrag(int position, String text) {

                        }

                        @Override
                        public void onTagCrossClick(int position) {

                        }
                    });
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

    private void handleUV(JSONObject postData) {
        shimer.stopShimmer();
        shimer.setVisibility(View.GONE);
        try {
            if (postData.getInt("pricer") == 1 && postData.getInt("userid") != SharedUtil.getSharedUserID()) {
                lltBeforePurchase.setVisibility(View.VISIBLE);
                lltAfterPurchase.setVisibility(View.GONE);
                mainSV.setVisibility(View.GONE);
                lltBottom.setVisibility(View.GONE);
            } else {
                lltBeforePurchase.setVisibility(View.GONE);
                lltAfterPurchase.setVisibility(View.GONE);
                mainSV.setVisibility(View.VISIBLE);
                lltBottom.setVisibility(View.VISIBLE);
            }

            if (postData.getInt("pricer") == 1 && postData.getInt("post_buy") == 1) {
                lltBeforePurchase.setVisibility(View.GONE);
                lltAfterPurchase.setVisibility(View.GONE);
                mainSV.setVisibility(View.VISIBLE);
            }

            txtCredit.setText(postData.getString("credit"));
            txtPrice.setText(postData.getString("price"));
            txtPostTitleBeforePurchase.setText(postData.getString("post_title"));
            txtPostTitleAfterPurchase.setText(postData.getString("post_title"));
            AppUtil.loadImageByUrl(mActivity, imgPostBeforePurchase, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            AppUtil.loadImageByUrl(mActivity, imgPostAfterPurchase, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            int verify = postData.getInt("verify");
            imgVerify.setVisibility(verify == 1 ? View.VISIBLE : View.GONE);
            String userAvatar = ApiUtil.ImageUrl + postData.getString("avatarblobid");
            AppUtil.loadImageByUrl(mActivity, imgUser, userAvatar, ImagePlaceHolderType.USERIMAGE);
            txtUserName.setText(postData.getString("username"));
            txtDateInfo.setText(postData.getString("post_date") + " • Tiempo de lectura: " + postData.getString("post_created"));
            if (postData.getInt("userid") == SharedUtil.getSharedUserID()) {
                btnDelete.setVisibility(View.VISIBLE);
                btnUnfollow.setVisibility(View.GONE);
                btnFollow.setVisibility(View.GONE);
                lltEditPost.setVisibility(View.GONE);
                lltDeletePost.setVisibility(View.GONE);
                btnReport.setVisibility(View.GONE);
                btnReportCancel.setVisibility(View.GONE);
            } else {
                btnDelete.setVisibility(View.GONE);
                btnReport.setVisibility(View.VISIBLE);
                btnReportCancel.setVisibility(View.VISIBLE);
                if (postData.getInt("post_followup") == 1) {
                    btnUnfollow.setVisibility(View.VISIBLE);
                    btnFollow.setVisibility(View.GONE);
                } else {
                    btnUnfollow.setVisibility(View.GONE);
                    btnFollow.setVisibility(View.VISIBLE);
                }

                lltEditPost.setVisibility(View.GONE);
                lltDeletePost.setVisibility(View.GONE);
            }
            txtPostTitle.setText(postData.getString("post_title"));
            postFavoriteStatus = postData.getInt("post_favourite");
            imgBookMark.setImageResource(postFavoriteStatus == 1 ? R.drawable.ic_book_mark_fill : R.drawable.ic_book_mark_empty);
            txtVote.setText(postData.getString("netvotes"));
            int likeStatus = postData.getInt("like_dislike_type");
            imgSelectLike.setVisibility(View.GONE);
            imgDisableDislike.setVisibility(View.GONE);
            imgSelectedDislike.setVisibility(View.GONE);
            imgSelectDislike.setVisibility(View.GONE);
            imgSelectedLike.setVisibility(View.GONE);
            imgDisableLike.setVisibility(View.GONE);
            if (likeStatus == 1) {
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.mainGreen), PorterDuff.Mode.SRC_IN);
                imgSelectedLike.setVisibility(View.VISIBLE);
                imgDisableDislike.setVisibility(View.VISIBLE);
            } else if (likeStatus == 0) {
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.mainRed), PorterDuff.Mode.SRC_IN);
                imgDisableLike.setVisibility(View.VISIBLE);
                imgSelectedDislike.setVisibility(View.VISIBLE);
            } else {
                imgThumb.setColorFilter(ContextCompat.getColor(mActivity, R.color.darkGray), PorterDuff.Mode.SRC_IN);
                imgSelectLike.setVisibility(View.VISIBLE);
                imgSelectDislike.setVisibility(View.VISIBLE);
            }
            AppUtil.loadImageByUrl(mActivity, imgPost, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            AppUtil.setWebViewThemeMode(mActivity, webPost);
            webPost.loadUrl(postData.getString("webViewLink"));
            txtVisitCnt.setText(postData.getString("views") + " Visitas");
            txtVotos.setText("" + postData.getInt("ratingVotes"));
            ratingBar.setRating(Float.parseFloat(postData.getString("userRating")));
            ENABLED_AD = postData.getInt("adimvi_promotions");
            int userAD = postData.getInt("promotional_image");
            String userADImage = ApiUtil.ImageUrl + postData.getString("uadblobid");
            AppUtil.loadImageByUrl(mActivity, imgUserAD, userADImage, ImagePlaceHolderType.POSTIMAGE);
            userADLink = postData.getString("uadimglink");
            if (ENABLED_AD == 1) {
                if (userAD == 0) {
                    lltAD.setVisibility(View.GONE);
                } else {
                    lltAD.setVisibility(View.VISIBLE);
                    nativeAD.setVisibility(View.GONE);
                    imgUserAD.setVisibility(View.VISIBLE);
                }
            } else {
                lltAD.setVisibility(View.VISIBLE);
                if (userAD == 0) {
                    imgUserAD.setVisibility(View.GONE);
                    nativeAD.setVisibility(View.VISIBLE);
                    loadAd();
                } else {
                    nativeAD.setVisibility(View.GONE);
                    imgUserAD.setVisibility(View.VISIBLE);
                }
            }
            categoryID = postData.getInt("categoryid");
            loadRelatedPost(categoryID);
            commentCnt = postData.getInt("total_comment");
            txtCommentCnt.setText(commentCnt + " comentarios");
            richEditor.setHtml(postData.getString("post_description"));
            Log.w("====>", postData.getString("post_description"));
            if (postData.getInt("userid") == SharedUtil.getSharedUserID()) {
                lltAvatar.setBackgroundResource(R.drawable.round_full_white_black1);
            } else {
                lltAvatar.setBackgroundResource(postData.getInt("hasRecentPost") == 1 ? R.drawable.ring_shape_4 : R.drawable.round_full_white_black1);
            }
            try {
                JSONArray extraArr = postData.getJSONArray("post_extra_image");
                try {
                    JSONObject extraObj = extraArr.getJSONObject(0);
                    try {
                        videoURL = extraObj.getString("url0");
                        if (videoURL.endsWith(".jpg") || videoURL.endsWith(".png") || videoURL.endsWith(".jpeg")) {
                            imgPlay.setVisibility(View.GONE);
                        } else {
                            imgPlay.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        imgPlay.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    imgPlay.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                imgPlay.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        AdLoader adLoader = new AdLoader.Builder(mActivity, AppConstant.NATIVE_AD_ID)
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                    nativeAD.setStyles(styles);
                    nativeAD.setNativeAd(unifiedNativeAd);
                }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

        mInterstitalAD = new InterstitialAd(mActivity);
        mInterstitalAD.setAdUnitId(AppConstant.INTERSTITIAL_AD_ID);
        mInterstitalAD.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                onCallPostDetailActivity();
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitalAD.loadAd(adRequest);
            }

            @Override
            public void onAdLoaded() {

            }
        });

        mInterstitalAD.loadAd(adRequest);
    }

    private void loadRelatedPost(int categroyID) {
        Map<String, String> param = new HashMap<>();
        try {
            param.put("userid", "" + SharedUtil.getSharedUserID());
            param.put("postid", "" + postData.getInt("postid"));
            param.put("categoryid", "" + categroyID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_RELATED_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    features.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray posts = response.getJSONArray("posts");
                    for (int i = 0; i < posts.length(); i++) {
                        FeaturModel feature = new FeaturModel();
                        feature.initWithJSON(posts.getJSONObject(i));
                        features.add(feature);
                    }
                    featureAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMorePoset:
//                if (ENABLED_AD == 0) {
//                    if (mInterstitalAD != null) {
//                        mInterstitalAD.show();
//                    }
//                } else {
//                    onCallPostDetailActivity();
//                }
                onCallPostDetailActivity();
                break;
            case R.id.imgUserAD:
                AppUtil.openURL(mActivity, userADLink);
                break;
            case R.id.imgPost:
                imageDialog = new ImageDialog(mActivity, ((BitmapDrawable) imgPost.getDrawable()).getBitmap());
                imageDialog.show();
                break;
            case R.id.imgBack:
                mActivity.onBackPressed();
                break;
            case R.id.btnCompare:
                onCallBuyPostAPI();
                break;
            case R.id.btnViewPost:
                lltBeforePurchase.setVisibility(View.GONE);
                lltAfterPurchase.setVisibility(View.GONE);
                mainSV.setVisibility(View.VISIBLE);
                break;
            case R.id.btnCredeit:
                mActivity.addFragment(new WalletFragment(mActivity, false, tabIndex), tabIndex);
                break;
            case R.id.img_user:
                try {
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, postData.getInt("userid"), null, null, false), tabIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgBookMark:
                onCallSetFavouritePost();
                break;
            case R.id.btnFollow:
                onCallSetFollowPostUser(0);
                break;
            case R.id.btnUnFollow:
                onCallSetFollowPostUser(1);
                break;
            case R.id.btnDelete:
                mActivity.addFragment(new FeaturePostFragment(mActivity, postData), tabIndex);
                break;
            case R.id.imgLiked:
                onCallSetLikePost(0);
                lltLike.setVisibility(View.GONE);
                break;
            case R.id.imgThumbsUp:
                if (lltLike.getVisibility() == View.VISIBLE) {
                    lltLike.setVisibility(View.GONE);
                } else {
                    lltLike.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.imgLike:
                onCallSetLikePost(1);
                lltLike.setVisibility(View.GONE);
                break;
            case R.id.imgOption:
                onClickOptions();
                break;
            case R.id.imgDisLike:
                onCallSetLikePost(2);
                lltLike.setVisibility(View.GONE);
                break;
            case R.id.imgDisLiked:
                onCallSetLikePost(3);
                lltLike.setVisibility(View.GONE);
                break;
            case R.id.btnAddComment:
                onCallAddComment();
                break;
            case R.id.imgPlay:
                onPlayVideo();
                break;
            case R.id.btnSeeComment:
                mActivity.addFragment(new CommentListFragment(mActivity, postID, tabIndex, 0), tabIndex);
                break;
            case R.id.btnReport:
                onCallSetReportPost(0);
                break;
            case R.id.btnReportCancel:
                onCallSetReportPost(1);
                break;
            case R.id.imgMuro:
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
            case R.id.lltDeletePost:
                promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", new PromptDialog.PromptDialogListener() {
                    @Override
                    public void onClickOKUBListener() {
                        onCallDeletePost();
                    }
                });
                promptDialog.show();
                break;
            case R.id.lltEditPost:
//                mActivity.addFragment(new AddPostFragment(mActivity, postID, tabIndex), tabIndex);
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

    private void onClickOptions() {
        try {
            if (postData.getInt("userid") != SharedUtil.getSharedUserID()) {
                sheetDialog = new ActionSheetDialog(mActivity, R.style.SheetDialog, "Bloquear usuario", "Enviar mensaje", new ActionSheetDialog.ActionSheetListener() {
                    @Override
                    public void onClickFirstOption() {

                    }

                    @Override
                    public void onClickSecondOption() {
                        ChatModel model = new ChatModel();
                        try {
                            model.userID = postData.getInt("userid");
                            model.userName = postData.getString("username");
                            model.imgAvatar = postData.getString("avatarblobid").isEmpty() ? "" : ApiUtil.ImageUrl + postData.getString("avatarblobid");
                            model.verified = postData.getInt("verify");
                            mActivity.addFragment(new MessageFragment(mActivity, model, tabIndex), tabIndex);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                sheetDialog.show();
            } else {
                sheetDialog = new ActionSheetDialog(mActivity, R.style.SheetDialog, "Editar", "Eliminar", new ActionSheetDialog.ActionSheetListener() {
                    @Override
                    public void onClickFirstOption() {
//                        mActivity.addFragment(new AddPostFragment(mActivity, postID, tabIndex), tabIndex);
                    }

                    @Override
                    public void onClickSecondOption() {
                        promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", new PromptDialog.PromptDialogListener() {
                            @Override
                            public void onClickOKUBListener() {
                                onCallDeletePost();
                            }
                        });
                        promptDialog.show();
                    }
                });
                sheetDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onPlayVideo() {
        AppUtil.openURL(mActivity, videoURL);
    }

    private void onCallDeletePost() {
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + postID);

        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELETE_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                mActivity.onBackPressed();
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

    private void onCallSetRatingPost(float rating) {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + this.postID);
        param.put("rating", "" + rating);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_RATING, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                initData();
                votePostDialog = new SetVotePostDialog(mActivity);
                votePostDialog.show();
            }

            @Override
            public void onEventInternetError(Exception e) {
            }

            @Override
            public void onEventServerError(Exception e) {
            }
        });

    }

    private void onCallAddComment() {
        if (edtComment.getText().toString().isEmpty()) {
            alertDialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, escribe un comentario.");
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + this.postID);
        param.put("mentions", TextUtils.join(",", edtComment.getMentions()));
        try {
            param.put("categoryid", postData.getString("categoryid"));
        } catch (JSONException e) {
            param.put("categoryid", "0");
        }
        param.put("comment", edtComment.getText().toString().replace("@", ""));
        param.put("type", "A");
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                alertDialog = AppUtil.showNormalDialog(mActivity, "Nuevo comentario", "¡Tu comentario ha sido añadido!");
                edtComment.setText("");
                commentCnt += 1;
                txtCommentCnt.setText(commentCnt + " comentarios");
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

    private void onCallSetReportPost(int status) {
        if (status == 0) {
            btnReport.setVisibility(View.GONE);
            btnReportCancel.setVisibility(View.VISIBLE);
        } else {
            btnReport.setVisibility(View.VISIBLE);
            btnReportCancel.setVisibility(View.GONE);
        }
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + this.postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_REPORT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {

            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
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
                    txtVote.setText(vote);
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
                    btnFollow.setVisibility(View.GONE);
                    btnUnfollow.setVisibility(View.VISIBLE);
                } else {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnUnfollow.setVisibility(View.GONE);
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

    private void onCallSetFavouritePost() {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + this.postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_FOLLOW, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                if (postFavoriteStatus == 1) {
                    postFavoriteStatus = 0;
                } else {
                    postFavoriteStatus = 1;
                }
                imgBookMark.setImageResource(postFavoriteStatus == 1 ? R.drawable.ic_book_mark_fill : R.drawable.ic_book_mark_empty);
                alertDialog = AppUtil.showNormalDialog(mActivity, "Guardado", "Este post se ha guardado en tus favoritos.");
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

    private void onCallBuyPostAPI() {
        try {
            if (postData.getInt("post_purchase") == 0) {
                alertDialog = AppUtil.showNormalDialog(mActivity, "Mensaje", postData.getString("credit_msg"));
            } else {
                Map<String, String> param = new HashMap<>();
                param.put("buyer", "" + SharedUtil.getSharedUserID());
                param.put("postid", "" + this.postID);
                param.put("seller", postData.getString("userid"));
                param.put("price", postData.getString("price"));
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                ApiUtil.onAPIConnectionResponse(ApiUtil.BUY_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        AppUtil.onDismissProgressDialog(dialog);
                        lltBeforePurchase.setVisibility(View.GONE);
                        lltAfterPurchase.setVisibility(View.VISIBLE);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onCallViewedPost() {
        Date date = Calendar.getInstance().getTime();
        String strDate = StringUtil.getDate("yyyy-MM-dd", date);
        SqlUtil sqlUtil = new SqlUtil(mActivity);
        if (sqlUtil.getData(SharedUtil.getSharedUserID(), postID, strDate)) {
            initData();
            return;
        } else {
            mainSV.setVisibility(View.GONE);
            lltAfterPurchase.setVisibility(View.GONE);
            lltBeforePurchase.setVisibility(View.GONE);
            shimer.setVisibility(View.VISIBLE);
            shimer.startShimmer();
            sqlUtil.insertData(SharedUtil.getSharedUserID(), postID, strDate);
            Map<String, String> param = new HashMap<>();
            param.put("postid", "" + postID);
            ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_VIEWED, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                @Override
                public void onEventCallBack(JSONObject obj) {
                    initData();
                }

                @Override
                public void onEventInternetError(Exception e) {

                }

                @Override
                public void onEventServerError(Exception e) {

                }
            });
        }
    }

    private void onCallPostDetailActivity() {
        mActivity.addFragment(new PostContentFragment(mActivity, postID, categoryID, tabIndex), tabIndex);
    }

}