package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.profile.viewpager.MuroFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.types.MuroCellType;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {

    private final Context mContext;
    private List<MuroModel> mMures = new ArrayList<>();
    private final WallAdapterListener mLisetener;
    private final MuroCellType cellType;

    public WallAdapter(Context mContext, List<MuroModel> mMures, MuroCellType cellType, WallAdapterListener mLisetener) {
        this.mContext = mContext;
        this.mMures = mMures;
        this.mLisetener = mLisetener;
        this.cellType = cellType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_muro, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MuroModel muro = mMures.get(position);
        if (cellType == MuroCellType.SIGUIENDO) {
            holder.lltMineDeco.setVisibility(View.GONE);
            holder.lltProfileOriginWallHandle.setVisibility(View.GONE);
            holder.lltProfileHandle.setVisibility(View.GONE);
            holder.lltSiguiendoHandle.setVisibility(View.VISIBLE);
            holder.lltViewCnt.setVisibility(View.GONE);
            holder.lltLastComment.setVisibility(View.GONE);
            holder.btnDestacar.setVisibility(View.GONE);
        } else if (cellType == MuroCellType.PROFILE){
            if (SharedUtil.getSharedUserID() == muro.fromuserID && muro.paid == 0) {
                holder.btnDestacar.setVisibility(View.VISIBLE);
            } else {
                holder.btnDestacar.setVisibility(View.GONE);
            }
            holder.btnDestacar.setOnClickListener(v -> mLisetener.onClickDestacar(muro));
            if (muro.fromuserID == MuroFragment.instance.getSelectedUserID()) {
                holder.lltMineDeco.setVisibility(View.VISIBLE);
                holder.lltViewCnt.setVisibility(View.VISIBLE);
            } else {
                holder.lltMineDeco.setVisibility(View.INVISIBLE);
                holder.lltViewCnt.setVisibility(View.GONE);
            }

            holder.lltProfileOriginWallHandle.setVisibility(View.VISIBLE);
            holder.lltProfileHandle.setVisibility(View.VISIBLE);
            holder.lltSiguiendoHandle.setVisibility(View.GONE);
            if (muro.fromuserID == SharedUtil.getSharedUserID()) {
                holder.txtProfileRemuro.setVisibility(View.GONE);
                holder.txtProfileEdit.setVisibility(View.VISIBLE);
            } else {
                holder.txtProfileRemuro.setVisibility(View.VISIBLE);
                holder.txtProfileEdit.setVisibility(View.GONE);
            }

            if (SharedUtil.getSharedUserID() == MuroFragment.instance.getSelectedUserID()) {
                holder.txtProfileDelete.setVisibility(View.VISIBLE);
            } else {
                if (muro.fromuserID == SharedUtil.getSharedUserID()) {
                    holder.txtProfileDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.txtProfileDelete.setVisibility(View.GONE);
                }
            }
            holder.lltLastComment.setVisibility(View.VISIBLE);
            if (muro.cntAnswer == 0) {
                holder.lltLastComment.setVisibility(View.GONE);
            } else {
                holder.lltLastComment.setVisibility(View.VISIBLE);
                holder.txtCntAnswer.setText(StringUtil.convertThousand(muro.cntAnswer) + " respuestas");
                if (muro.lastCommentUserAvatar.isEmpty()) {
                    holder.imgLastUserAvatar.setImageResource(R.drawable.ic_user_placehoder);
                } else {
                    AppUtil.loadImageByUrl(mContext, holder.imgLastUserAvatar, muro.lastCommentUserAvatar, ImagePlaceHolderType.USERIMAGE);
                }
            }
        }

        if (muro.tags == null || muro.tags.size() == 0) {
            holder.tagContainerLayout.setVisibility(View.GONE);
        } else {
            holder.tagContainerLayout.setVisibility(View.VISIBLE);
            List<String> strTags = new ArrayList<>();
            for (TagModel tagModel: muro.tags) {
                strTags.add(tagModel.tagTitle);
            }
            holder.tagContainerLayout.setTags(strTags);
            holder.tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {
                    mLisetener.onClickTag(muro.tags.get(position));
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
        }

        AppUtil.loadImageByUrl(mContext, holder.imgUserAvatar, muro.userAvatar, ImagePlaceHolderType.USERIMAGE);
        if (muro.imageUrl.isEmpty()) {
            holder.cardPostImg.setVisibility(View.GONE);
        } else {
            holder.cardPostImg.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, holder.imgPost, muro.imageUrl, ImagePlaceHolderType.POSTIMAGE);
        }
        holder.imgUserVerify.setVisibility(muro.verify == 1 ? View.VISIBLE : View.GONE);
        holder.txtCreated.setText(muro.created);
        holder.txtUserName.setText(muro.username);
        holder.txtContent.setText(muro.content);
        if (muro.favourite.equals("0")) {
            holder.imgHeart.setImageResource(R.drawable.ic_heart);
            holder.imgProfileHeart.setImageResource(R.drawable.ic_heart);
        } else {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_red);
            holder.imgProfileHeart.setImageResource(R.drawable.ic_heart_red);
        }
        holder.txtCommentCnt.setText(muro.totalComments);
        holder.txtLikeCnt.setText(muro.totalFav);
        holder.txtProfileLikeCnt.setText(muro.totalFav);
        holder.txtRemuroCnt.setText(muro.remuroCnt);
        if (muro.rewallModel == null) {
            holder.lltOriginWall.setVisibility(View.GONE);
        } else {
            holder.lltOriginWall.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, holder.imgOriginWallUserAvatar, muro.rewallModel.userAvatar, ImagePlaceHolderType.USERIMAGE);
            holder.imgOriginWallUserVerify.setVisibility(muro.rewallModel.verify == 1 ? View.VISIBLE : View.GONE);
            holder.txtOriginWallUserName.setText(muro.rewallModel.username);
            holder.txtOriginWallCreated.setText(muro.rewallModel.created);
            holder.txtOriginWallContent.setText(muro.rewallModel.content);
            if (muro.rewallModel.imageUrl.isEmpty()) {
                holder.cardOriginWallPostImg.setVisibility(View.GONE);
            } else {
                holder.cardOriginWallPostImg.setVisibility(View.VISIBLE);
                AppUtil.loadImageByUrl(mContext, holder.imgOriginWallPost, muro.rewallModel.imageUrl, ImagePlaceHolderType.POSTIMAGE);
            }
        }
        if (muro.repostModel == null) {
            holder.rltOriginPost.setVisibility(View.GONE);
        } else {
            holder.rltOriginPost.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, holder.imgOriginPost, muro.repostModel.content, ImagePlaceHolderType.POSTIMAGE);
            holder.txtOriginPostTitle.setText(muro.repostModel.title);
            holder.txtOriginPostCreated.setText(muro.repostModel.created);
        }

        holder.imgUserAvatar.setOnClickListener(v -> mLisetener.onClickUserAvatar(muro.fromuserID));
        holder.imgPost.setOnClickListener(v -> mLisetener.onClickPostImage(((BitmapDrawable) holder.imgPost.getDrawable()).getBitmap()));
        holder.imgOriginWallPost.setOnClickListener(v -> mLisetener.onClickOriginWallImage(((BitmapDrawable) holder.imgOriginWallPost.getDrawable()).getBitmap()));
        holder.rltOriginPost.setOnClickListener(v -> mLisetener.onClickOriginPost(muro.repostModel.id));
        holder.lltComment.setOnClickListener(v -> mLisetener.onClickComment(muro.messageID, muro.fromuserID));
        holder.lltLike.setOnClickListener(v -> mLisetener.onClickLike(muro.messageID));
        holder.lltRemuro.setOnClickListener(v -> mLisetener.onClickRemuro(muro));
        holder.txtCntAnswer.setOnClickListener(v -> mLisetener.onClickComment(muro.messageID, muro.fromuserID));

        /// profile remuro handle ///
        holder.lltProifleHeart.setOnClickListener(v -> mLisetener.onClickLike(muro.messageID));
        holder.txtProfileEdit.setOnClickListener(v -> mLisetener.onClickEdit(muro));
        holder.txtProfileDelete.setOnClickListener(v -> mLisetener.onClickDelete(muro.messageID));
        holder.txtProfileComment.setOnClickListener(v -> mLisetener.onClickComment(muro.messageID, muro.fromuserID));
        holder.txtProfileRemuro.setOnClickListener(v -> mLisetener.onClickRemuro(muro));
        holder.txtProfileOriginWallRemuro.setOnClickListener(v -> mLisetener.onClickOriginWallRemuro(muro.rewallModel));
        holder.txtProfileOriginWallComment.setOnClickListener(v -> mLisetener.onClickComment(muro.rewallModel.id, SharedUtil.getSharedUserID()));
        holder.txtViewCnt.setText(muro.viewCnt + " views");
        if (muro.fromuserID == SharedUtil.getSharedUserID()) {
            holder.lltAvatar.setBackgroundResource(0);
        } else {
            holder.lltAvatar.setBackgroundResource(muro.hasRecentPost == 1 ? R.drawable.ring_shape_2 : 0);
        }
    }

    @Override
    public int getItemCount() {
        return mMures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUserAvatar, imgOriginWallUserAvatar, imgPost, imgOriginWallPost, imgOriginPost, imgUserVerify, imgOriginWallUserVerify, imgHeart,
                imgProfileHeart, imgLastUserAvatar;
        public TextView txtUserName, txtOriginWallUserName, txtCreated, txtOriginWallCreated, txtOriginPostCreated, txtContent, txtOriginWallContent,
                txtOriginPostTitle, txtCommentCnt, txtLikeCnt, txtRemuroCnt, txtProfileOriginWallComment, txtProfileOriginWallRemuro, txtProfileLikeCnt, txtProfileEdit,
                txtProfileDelete, txtProfileComment, txtProfileRemuro, txtViewCnt, txtCntAnswer;
        public LinearLayout lltComment, lltLike, lltRemuro, lltOriginWall, lltMineDeco, lltSiguiendoHandle, lltProfileOriginWallHandle,
                lltProfileHandle, lltProifleHeart, lltAvatar, lltViewCnt, lltLastComment;
        public RelativeLayout rltOriginPost;
        public CardView cardPostImg, cardOriginWallPostImg;
        public TagContainerLayout tagContainerLayout;
        public Button btnDestacar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUser);
            imgOriginWallUserAvatar = itemView.findViewById(R.id.imgOriginWallUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgOriginWallPost = itemView.findViewById(R.id.imgOriginWallPost);
            imgOriginPost = itemView.findViewById(R.id.imgOriginPostImge);
            imgUserVerify = itemView.findViewById(R.id.imgVerify);
            imgOriginWallUserVerify = itemView.findViewById(R.id.imgOriginWallVerify);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            txtUserName = itemView.findViewById(R.id.txtUseName);
            txtOriginWallUserName = itemView.findViewById(R.id.txtOriginWallUseName);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtOriginWallCreated = itemView.findViewById(R.id.txtOriginWallCreated);
            txtOriginPostCreated = itemView.findViewById(R.id.txtOriginPostCreated);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtOriginWallContent = itemView.findViewById(R.id.txtOriginWallContent);
            txtOriginPostTitle = itemView.findViewById(R.id.txtOriginPostContent);
            txtCommentCnt = itemView.findViewById(R.id.txtCommentCnt);
            txtLikeCnt = itemView.findViewById(R.id.txtLiketCnt);
            txtRemuroCnt = itemView.findViewById(R.id.txtRemuroCnt);
            lltComment = itemView.findViewById(R.id.lltCOmment);
            lltLike = itemView.findViewById(R.id.lltHeart);
            lltRemuro = itemView.findViewById(R.id.lltRemuro);
            lltOriginWall = itemView.findViewById(R.id.lltOriginWall);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);
            rltOriginPost = itemView.findViewById(R.id.lltOriginPost);
            cardPostImg = itemView.findViewById(R.id.cardPostImg);
            cardOriginWallPostImg = itemView.findViewById(R.id.cardOriginWallPostImg);

            /// profile remur part ///
            imgProfileHeart = itemView.findViewById(R.id.imgProfileHeart);
            txtProfileDelete = itemView.findViewById(R.id.txtProfileDelte);
            txtProfileComment = itemView.findViewById(R.id.txtProfileComentar);
            txtProfileRemuro = itemView.findViewById(R.id.txtProfileRemuro);
            txtProfileEdit = itemView.findViewById(R.id.txtProfileEdit);
            txtProfileLikeCnt = itemView.findViewById(R.id.txtProfileLiketCnt);
            txtProfileOriginWallComment = itemView.findViewById(R.id.txtProfileOriginWallComentar);
            txtProfileOriginWallRemuro = itemView.findViewById(R.id.txtProfileOriginWallRemuro);
            lltMineDeco = itemView.findViewById(R.id.lltMineDeco);
            lltProfileOriginWallHandle = itemView.findViewById(R.id.lltProfileOriginWallHandle);
            lltProfileHandle = itemView.findViewById(R.id.lltProfileHandle);
            lltProifleHeart = itemView.findViewById(R.id.lltProfileHeart);
            lltSiguiendoHandle = itemView.findViewById(R.id.lltSiguiendoHandle);
            tagContainerLayout = itemView.findViewById(R.id.tagContainer);
            lltViewCnt = itemView.findViewById(R.id.lltViewCnt);
            txtViewCnt = itemView.findViewById(R.id.txtViewCnt);

            lltLastComment = itemView.findViewById(R.id.lltLastComment);
            imgLastUserAvatar = itemView.findViewById(R.id.imgUserLastComment);
            txtCntAnswer = itemView.findViewById(R.id.txtCntAnswer);
            btnDestacar = itemView.findViewById(R.id.btnDestacar);
        }
    }

    public interface WallAdapterListener {
        void onClickUserAvatar(int userID);
        void onClickPostImage(Bitmap bitmap);
        void onClickOriginWallImage(Bitmap bitmap);
        void onClickOriginPost(int postID);
        void onClickComment(int postID, int fromUserID);
        void onClickLike(int postID);
        void onClickRemuro(MuroModel model);
        default void onClickEdit(MuroModel modle){};
        default void onClickDelete(int id){};
        default void onClickOriginWallRemuro(RewallModel rewall){};
        void onClickTag(TagModel tagModel);
        default void onClickDestacar(MuroModel muro){};
    }
}
