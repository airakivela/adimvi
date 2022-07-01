package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.FeaturModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.TemplateView;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.google.android.gms.ads.InterstitialAd;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import per.wsj.library.AndRatingBar;

public class FeaturePostAdapter extends RecyclerView.Adapter<FeaturePostAdapter.ViewHolder> {

    private Context mContext;
    private List<JSONObject> jsonObjects;
    private FeaturePostAdpaterListener mListener;

    public FeaturePostAdapter(Context mContext, List<JSONObject> jsonObjects, FeaturePostAdpaterListener mListener) {
        this.mContext = mContext;
        this.jsonObjects = jsonObjects;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FeaturePostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_post_feature, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturePostAdapter.ViewHolder holder, int position) {
        JSONObject postData = jsonObjects.get(position);
        try {
            if (postData.getInt("pricer") == 1 && postData.getInt("userid") != SharedUtil.getSharedUserID()) {
                holder.lltBeforePurchase.setVisibility(View.VISIBLE);
                holder.lltAfterPurchase.setVisibility(View.GONE);
                holder.mainSV.setVisibility(View.GONE);
            } else {
                holder.lltBeforePurchase.setVisibility(View.GONE);
                holder.lltAfterPurchase.setVisibility(View.GONE);
                holder.mainSV.setVisibility(View.VISIBLE);
            }

            if (postData.getInt("pricer") == 1 && postData.getInt("post_buy") == 1) {
                holder.lltBeforePurchase.setVisibility(View.GONE);
                holder.lltAfterPurchase.setVisibility(View.GONE);
                holder.mainSV.setVisibility(View.VISIBLE);
            }

            holder.txtCredit.setText(postData.getString("credit"));
            holder.txtPrice.setText(postData.getString("price"));
            holder.txtPostTitleBeforePurchase.setText(postData.getString("post_title"));
            holder.txtPostTitleAfterPurchase.setText(postData.getString("post_title"));
            AppUtil.loadImageByUrl(mContext, holder.imgPostBeforePurchase, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            AppUtil.loadImageByUrl(mContext, holder.imgPostAfterPurchase, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            int verify = postData.getInt("verify");
            holder.imgVerify.setVisibility(verify == 1 ? View.VISIBLE : View.GONE);
            String userAvatar = ApiUtil.ImageUrl + postData.getString("avatarblobid");
            AppUtil.loadImageByUrl(mContext, holder.imgUser, userAvatar, ImagePlaceHolderType.USERIMAGE);
            holder.txtUserName.setText(postData.getString("username"));
            holder.txtUserPoint.setText(postData.getString("total_points"));
            holder.txtDateInfo.setText(postData.getString("post_date") + " • Tiempo de lectura: " + postData.getString("post_created"));
            if (postData.getInt("userid") == SharedUtil.getSharedUserID()) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnUnfollow.setVisibility(View.GONE);
                holder.btnFollow.setVisibility(View.GONE);
                holder.lltEditPost.setVisibility(View.VISIBLE);
                holder.lltDeletePost.setVisibility(View.VISIBLE);
            } else {
                holder.btnDelete.setVisibility(View.GONE);
                if (postData.getInt("post_followup") == 1) {
                    holder.btnUnfollow.setVisibility(View.VISIBLE);
                    holder.btnFollow.setVisibility(View.GONE);
                } else {
                    holder.btnUnfollow.setVisibility(View.GONE);
                    holder.btnFollow.setVisibility(View.VISIBLE);
                }

                holder.lltEditPost.setVisibility(View.GONE);
                holder.lltDeletePost.setVisibility(View.GONE);
            }
            holder.txtPostTitle.setText(postData.getString("post_title"));
            holder.postFavoriteStatus = postData.getInt("post_favourite");
            holder.imgBookMark.setImageResource(holder.postFavoriteStatus == 1 ? R.drawable.ic_book_mark_fill : R.drawable.ic_book_mark_empty);
            holder.txtVote.setText(postData.getString("netvotes"));
            int likeStatus = postData.getInt("like_dislike_type");
            holder.imgSelectLike.setVisibility(View.GONE);
            holder.imgDisableDislike.setVisibility(View.GONE);
            holder.imgSelectedDislike.setVisibility(View.GONE);
            holder.imgSelectDislike.setVisibility(View.GONE);
            holder.imgSelectedLike.setVisibility(View.GONE);
            holder.imgDisableLike.setVisibility(View.GONE);
            if (likeStatus == 1) {
                holder.imgSelectedLike.setVisibility(View.VISIBLE);
                holder.imgDisableDislike.setVisibility(View.VISIBLE);
            } else if (likeStatus == 0) {
                holder.imgDisableLike.setVisibility(View.VISIBLE);
                holder.imgSelectedDislike.setVisibility(View.VISIBLE);
            } else {
                holder.imgSelectLike.setVisibility(View.VISIBLE);
                holder.imgSelectDislike.setVisibility(View.VISIBLE);
            }

            holder.btnReportCancel.setVisibility(View.GONE);
            AppUtil.loadImageByUrl(mContext, holder.imgPost, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
            AppUtil.setWebViewThemeMode(mContext, holder.webPost);
            holder.webPost.loadUrl(postData.getString("webViewLink"));
            holder.txtVisitCnt.setText(postData.getString("views") + " Visitas");
            holder.txtVotos.setText("" + postData.getInt("ratingVotes"));
            holder.ratingBar.setRating(Float.parseFloat(postData.getString("userRating")));
            holder.ENABLED_AD = postData.getInt("adimvi_promotions");
            int userAD = postData.getInt("promotional_image");
            String userADImage = ApiUtil.ImageUrl + postData.getString("uadblobid");
            AppUtil.loadImageByUrl(mContext, holder.imgUserAD, userADImage, ImagePlaceHolderType.POSTIMAGE);
            holder.userADLink = postData.getString("uadimglink");
            if (holder.ENABLED_AD == 1) {
                if (userAD == 0) {
                    holder.lltAD.setVisibility(View.GONE);
                } else {
                    holder.lltAD.setVisibility(View.VISIBLE);
                    holder.nativeAD.setVisibility(View.GONE);
                    holder.imgUserAD.setVisibility(View.VISIBLE);
                }
            } else {
                holder.lltAD.setVisibility(View.VISIBLE);
                if (userAD == 0) {
                    holder.imgUserAD.setVisibility(View.GONE);
                    holder.nativeAD.setVisibility(View.VISIBLE);
                    //loadAd();
                } else {
                    holder.nativeAD.setVisibility(View.GONE);
                    holder.imgUserAD.setVisibility(View.VISIBLE);
                }
            }
            int categoryID = postData.getInt("categoryid");
            //loadRelatedPost(categoryID);
            holder.commentCnt = postData.getInt("total_comment");
            holder.txtCommentCnt.setText(holder.commentCnt + " comentarios");
            if (postData.getInt("userid") == SharedUtil.getSharedUserID()) {
                holder.lltAvatar.setBackgroundResource(0);
            } else {
                holder.lltAvatar.setBackgroundResource(postData.getInt("hasRecentPost") == 1 ? R.drawable.ring_shape_2 : 0);
            }
            try {
                JSONArray extraArr = postData.getJSONArray("post_extra_image");
                try {
                    JSONObject extraObj = extraArr.getJSONObject(0);
                    try {
                        holder.videoURL = extraObj.getString("url0");
                        if (holder.videoURL.endsWith(".jpg") || holder.videoURL.endsWith(".png") || holder.videoURL.endsWith(".jpeg")) {
                            holder.imgPlay.setVisibility(View.GONE);
                        } else {
                            holder.imgPlay.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        holder.imgPlay.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    holder.imgPlay.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                holder.imgPlay.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout lltMain, lltRemuro, lltShare, lltEditPost, lltDeletePost, lltAD, lltBeforePurchase, lltAfterPurchase, lltAvatar;
        private ScrollView mainSV;
        private ImageView imgUser, imgVerify, imgBookMark, imgDisableLike, imgDisableDislike, imgPlay,
                imgSelectLike, imgSelectDislike, imgSelectedLike, imgSelectedDislike, imgPost, imgPostBeforePurchase, imgPostAfterPurchase, imgUserAD;
        private TextView txtUserName, txtUserPoint, txtPostTitle, txtVote, txtDateInfo, txtVisitCnt, txtVotos, txtCommentCnt, txtCredit, txtPrice,
                txtPostTitleBeforePurchase, txtPostTitleAfterPurchase;
        private Button btnFollow, btnUnfollow, btnDelete, btnMorePost, btnReport, btnReportCancel, btnAddComment, btnSeeComment, btnCompare, btnCredit, btnViewPost;
        private WebView webPost;
        private TagContainerLayout tagContainer;
        private AndRatingBar ratingBar;
        private TemplateView nativeAD;
        private RecyclerView rclRelation;
        private CheckBox checkBox;
        private SocialAutoCompleteTextView edtComment;

        private List<FeaturModel> features = new ArrayList<>();
        private FeatureAdapter featureAdapter;
        private List<TagModel> mTags = new ArrayList<>();
        private ArrayAdapter<MentionUserModel> mentionAdapter;

        private int ENABLED_AD = 0;
        private InterstitialAd mInterstitalAD;

        private int postFavoriteStatus = 0;
        private int commentCnt = 0;
        private String userADLink = "";
        private String videoURL = "";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainSV = itemView.findViewById(R.id.mainSV);

            lltMain = itemView.findViewById(R.id.llt_main);
            lltRemuro = itemView.findViewById(R.id.lltRemuro);
            lltShare = itemView.findViewById(R.id.lltShare);
            lltDeletePost = itemView.findViewById(R.id.lltDeletePost);
            lltEditPost = itemView.findViewById(R.id.lltEditPost);
            lltAD = itemView.findViewById(R.id.lltAD);
            lltBeforePurchase = itemView.findViewById(R.id.lltBeforePurchase);
            lltAfterPurchase = itemView.findViewById(R.id.lltAfterPurchase);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);

            imgUser = itemView.findViewById(R.id.img_user);
            imgVerify = itemView.findViewById(R.id.img_verified);
            imgBookMark = itemView.findViewById(R.id.imgBookMark);
            imgDisableLike = itemView.findViewById(R.id.imgLikeDisable);
            imgDisableDislike = itemView.findViewById(R.id.imgDisLikeDisable);
            imgSelectLike = itemView.findViewById(R.id.imgLike);
            imgSelectDislike = itemView.findViewById(R.id.imgDisLike);
            imgSelectedLike = itemView.findViewById(R.id.imgLiked);
            imgSelectedDislike = itemView.findViewById(R.id.imgDisLiked);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgPostAfterPurchase = itemView.findViewById(R.id.imgPostAfterPurchase);
            imgPostBeforePurchase = itemView.findViewById(R.id.imgPostBeforePurchase);
            imgUserAD = itemView.findViewById(R.id.imgUserAD);
            imgPlay = itemView.findViewById(R.id.imgPlay);

            txtUserName = itemView.findViewById(R.id.txt_username);
            txtUserPoint = itemView.findViewById(R.id.txtUserPoints);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtVote = itemView.findViewById(R.id.txtNetVote);
            txtDateInfo = itemView.findViewById(R.id.txtDateInfo);
            txtVisitCnt = itemView.findViewById(R.id.txtVisitCnt);
            txtVotos = itemView.findViewById(R.id.txtVotes);
            txtCommentCnt = itemView.findViewById(R.id.txtComentCnt);
            txtCredit = itemView.findViewById(R.id.txtCredit);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPostTitleBeforePurchase = itemView.findViewById(R.id.txtPostTitleBeforePurchase);
            txtPostTitleAfterPurchase = itemView.findViewById(R.id.txtPostTitleAfterPurchase);

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            btnUnfollow = itemView.findViewById(R.id.btnUnFollow);
            btnMorePost = itemView.findViewById(R.id.btnMorePoset);
            btnReport = itemView.findViewById(R.id.btnReport);
            btnReportCancel = itemView.findViewById(R.id.btnReportCancel);
            btnSeeComment = itemView.findViewById(R.id.btnSeeComment);
            btnAddComment = itemView.findViewById(R.id.btnAddComment);
            btnViewPost = itemView.findViewById(R.id.btnViewPost);
            btnCredit = itemView.findViewById(R.id.btnCredeit);
            btnCompare = itemView.findViewById(R.id.btnCompare);

            webPost = itemView.findViewById(R.id.webPost);
            tagContainer = itemView.findViewById(R.id.tagContainer);
            ratingBar = itemView.findViewById(R.id.ratingPost);
            nativeAD = itemView.findViewById(R.id.nativeAD);
            rclRelation = itemView.findViewById(R.id.rcl_related);
            checkBox = itemView.findViewById(R.id.chkTerms);

            edtComment = itemView.findViewById(R.id.edtComment);
        }
    }

    public interface FeaturePostAdpaterListener {

    }
}
