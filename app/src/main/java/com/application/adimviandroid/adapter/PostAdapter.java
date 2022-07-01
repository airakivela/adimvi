package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.models.PostModel;
import com.application.adimviandroid.types.PostCellType;
import com.application.adimviandroid.types.PostModeType;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;

import java.util.ArrayList;
import java.util.List;

import per.wsj.library.AndRatingBar;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context mContext;
    private List<PostModel> mPosts = new ArrayList<>();
    private PostAdapterListener mListener;
    private PostCellType mCellType;
    private PostModeType mModeType;

    public PostAdapter(Context context, List<PostModel> posts, PostCellType cellType, PostModeType modeType, PostAdapterListener listener) {
        this.mContext = context;
        this.mPosts = posts;
        this.mCellType = cellType;
        this.mModeType = modeType;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(mModeType == PostModeType.FULLTYPE ? R.layout.item_post_full:
                (mModeType == PostModeType.FULLTYPEMARGIN ? R.layout.item_post_full_margin:
                        (mModeType == PostModeType.FULLTAB ? R.layout.item_post_full_margin_tab : R.layout.item_post)), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel post = mPosts.get(position);
        AppUtil.loadImageByUrl(mContext, holder.imgUser, post.avatarBlobid, ImagePlaceHolderType.USERIMAGE);
        AppUtil.loadImageByUrl(mContext, holder.imgPost, post.postImg, ImagePlaceHolderType.POSTIMAGE);
        holder.imgVerify.setVisibility(post.verifiy == 1 ? View.VISIBLE : View.GONE);
        holder.txtUserName.setText(post.handle);
        holder.txtVotes.setText(post.ratingVotes);
        holder.txtVisistCnt.setText(post.views);
        holder.txtSeenTime.setText(post.postCreated);
        holder.txtFollowCnt.setText(post.like);
        holder.txtCommentCnt.setText(post.comments);
        holder.txtCategroy.setText(post.categoryName);
        holder.txtPrice.setText("");
        holder.txtPostTitle.setText(post.title);
        holder.ratingBar.setRating(post.rating);
        AppUtil.setWebViewThemeMode(mContext, holder.webPost);
        holder.webPost.loadUrl(post.shortPostLink);
        holder.webPost.setBackgroundColor(Color.TRANSPARENT);
        if (post.pricer == 1) {
            if (post.postBuy.equals("1") && SharedUtil.getSharedUserID() != post.userID) {
                holder.txtPrice.setText("Libre");
                holder.txtPrice.setTextColor(Color.parseColor("#ADED4A"));
                holder.lltPrice.setVisibility(View.VISIBLE);
            } else {
                holder.txtPrice.setText(post.price);
                holder.txtPrice.setTextColor(Color.parseColor("#FECC2B"));
                holder.lltPrice.setVisibility(View.VISIBLE);
            }
        } else {
            holder.txtPrice.setText("Libre");
            holder.lltPrice.setVisibility(View.INVISIBLE);
        }
        int postFollowStatus = post.postFollow;
        if (mCellType == PostCellType.POST_CELL_PROFILE) {
            holder.imgFollow.setVisibility(View.GONE);
            holder.imgUnfollow.setVisibility(View.GONE);
        } else {
            holder.imgFollow.setVisibility(View.VISIBLE);
            holder.imgUnfollow.setVisibility(View.VISIBLE);
            if (postFollowStatus == 1) {
                holder.imgUnfollow.setVisibility(View.VISIBLE);
                holder.imgFollow.setVisibility(View.GONE);
            } else {
                holder.imgUnfollow.setVisibility(View.GONE);
                holder.imgFollow.setVisibility(View.VISIBLE);
            }
        }
        holder.imgRemuro.setOnClickListener(v -> mListener.onClickRemuroUB(post));
        holder.imgShare.setOnClickListener(v -> mListener.onClickShare(post.shareLink));
        holder.imgUser.setOnClickListener(v -> mListener.onClickUserProfile(post.userID));
        holder.imgPost.setOnClickListener(v -> mListener.onClickPostImage(post.postID));
        holder.imgFollow.setOnClickListener(v -> mListener.onClickFollow(post));
        holder.imgUnfollow.setOnClickListener(v -> mListener.onClickFollow(post));
        holder.txtCategroy.setOnClickListener(v -> mListener.onClickCategroy(post.categoryID, post.categoryName));
        if (post.userID == SharedUtil.getSharedUserID()) {
            holder.lltAvatar.setBackgroundResource(0);
        } else {
            holder.lltAvatar.setBackgroundResource(post.hasRecentPost == 1 ? R.drawable.ring_shape_2 : 0);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUser, imgPost, imgVerify, imgShare, imgRemuro, imgFollow, imgUnfollow;
        public TextView txtUserName, txtVotes, txtVisistCnt, txtSeenTime, txtFollowCnt, txtCommentCnt, txtPrice, txtCategroy, txtPostTitle;
        public AndRatingBar ratingBar;
        public WebView webPost;
        public LinearLayout lltPrice, lltAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgRemuro = itemView.findViewById(R.id.imgRemuro);
            imgFollow = itemView.findViewById(R.id.imgFollow);
            imgUnfollow = itemView.findViewById(R.id.imgUnfollow);
            txtUserName = itemView.findViewById(R.id.txtUseName);
            txtVotes = itemView.findViewById(R.id.txtVote);
            txtVisistCnt = itemView.findViewById(R.id.txtViewCnt);
            txtSeenTime = itemView.findViewById(R.id.txtReadTime);
            txtFollowCnt = itemView.findViewById(R.id.txtFollowCnt);
            txtCommentCnt = itemView.findViewById(R.id.txtCommentCnt);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCategroy = itemView.findViewById(R.id.txtCategoryName);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            ratingBar = itemView.findViewById(R.id.ratingPost);
            webPost = itemView.findViewById(R.id.webPost);
            lltPrice = itemView.findViewById(R.id.lltPrice);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);
        }
    }

    public interface PostAdapterListener {
        void onClickUserProfile(int id);
        void onClickFollow(PostModel post);
        void onClickPostImage(int id);
        void onClickShare(String shareURL);
        void onClickRemuroUB(PostModel post);
        void onClickCategroy(int id, String name);
    }
}
