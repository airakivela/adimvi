package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.types.CommentCellType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentModel> mComments = new ArrayList<>();
    private CommentCellType type;
    private CommentCellListener mListener;

    public CommentAdapter(Context mContext, List<CommentModel> mComments, CommentCellType type, CommentCellListener mListener) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.type = type;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentModel comment = mComments.get(position);
        if (type == CommentCellType.ORIGINCOMMENT) {
            holder.lltHandle.setVisibility(View.VISIBLE);
            holder.lltHandleComment.setVisibility(View.GONE);
            holder.lltLastComment.setVisibility(View.VISIBLE);
            if (comment.likeType == 1) {
                holder.imgSelectedLike.setVisibility(View.VISIBLE);
                holder.imgDisableDislike.setVisibility(View.VISIBLE);
                holder.imgSelectLike.setVisibility(View.GONE);
                holder.imgDisableLike.setVisibility(View.GONE);
                holder.imgSelectDislike.setVisibility(View.GONE);
                holder.imgSelectedDislike.setVisibility(View.GONE);
            } else if (comment.likeType == 0) {
                holder.imgDisableLike.setVisibility(View.VISIBLE);
                holder.imgSelectedDislike.setVisibility(View.VISIBLE);
                holder.imgDisableDislike.setVisibility(View.GONE);
                holder.imgSelectLike.setVisibility(View.GONE);
                holder.imgSelectDislike.setVisibility(View.GONE);
                holder.imgSelectedLike.setVisibility(View.GONE);
            } else {
                holder.imgSelectLike.setVisibility(View.VISIBLE);
                holder.imgSelectDislike.setVisibility(View.VISIBLE);
                holder.imgDisableLike.setVisibility(View.GONE);
                holder.imgSelectedDislike.setVisibility(View.GONE);
                holder.imgDisableDislike.setVisibility(View.GONE);
                holder.imgSelectedLike.setVisibility(View.GONE);
            }
            if (comment.cntAnswer == 0) {
                holder.lltLastComment.setVisibility(View.GONE);
            } else {
                holder.lltLastComment.setVisibility(View.VISIBLE);
                if (comment.lastCommentUserAvatar.isEmpty()) {
                    holder.imgLastCommentUser.setImageResource(R.drawable.ic_user_placehoder);
                } else {
                    AppUtil.loadImageByUrl(mContext, holder.imgLastCommentUser, comment.lastCommentUserAvatar, ImagePlaceHolderType.USERIMAGE);
                }
                holder.txtAnswerCnt.setText(StringUtil.convertThousand(comment.cntAnswer) + " respuestas");
                holder.txtAnswerCnt.setOnClickListener(v -> mListener.onClickComment(comment));
            }
        } else if (type == CommentCellType.COMMENTCOMMENT){
            holder.lltHandleComment.setVisibility(View.VISIBLE);
            holder.lltHandle.setVisibility(View.GONE);
            holder.lltLastComment.setVisibility(View.GONE);
        }
        if (comment.isSelectedComment) {
            holder.txtComment.setTypeface(null, Typeface.BOLD);
        } else {
            holder.txtComment.setTypeface(null, Typeface.NORMAL);
        }
        if (comment.userID == SharedUtil.getSharedUserID()) {
            holder.txtEdit.setVisibility(View.VISIBLE);
            holder.txtDelete.setVisibility(View.VISIBLE);
            holder.txtCommentDelete.setVisibility(View.VISIBLE);
            holder.txtCommentEdit.setVisibility(View.VISIBLE);
        } else {
            if (comment.ownerUsrID == SharedUtil.getSharedUserID()) {
                holder.txtDelete.setVisibility(View.VISIBLE);
                holder.txtEdit.setVisibility(View.GONE);
                holder.txtCommentDelete.setVisibility(View.VISIBLE);
                holder.txtCommentEdit.setVisibility(View.GONE);
            } else {
                holder.txtEdit.setVisibility(View.GONE);
                holder.txtDelete.setVisibility(View.GONE);
                holder.txtCommentDelete.setVisibility(View.GONE);
                holder.txtCommentEdit.setVisibility(View.GONE);
            }
        }
        AppUtil.loadImageByUrl(mContext, holder.imgUser, comment.userAvatar, ImagePlaceHolderType.USERIMAGE);
        holder.txtUserPoint.setText(comment.totalPoints);
        holder.txtNetVote.setText(comment.netVotes);
        holder.txtCreated.setText(comment.created);
        List<MentionUserModel> mentions = AppUtil.getMentionUsers(comment.comment);
        if (!mentions.isEmpty()) {
            holder.txtComment.setText(AppUtil.getSpannableString(mContext, String.valueOf(Html.fromHtml(comment.comment)), mentions, mentionUserModel -> {
                mListener.onClickMention(mentionUserModel.id);
            }));
            holder.txtComment.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.txtComment.setText(Html.fromHtml(comment.comment));
        }
        holder.txtUserName.setText(comment.userName);
        holder.imgVerify.setVisibility(comment.verify == 1 ? View.VISIBLE : View.GONE);

        holder.imgUser.setOnClickListener(v -> mListener.onClickUserImage(comment.userID));
        holder.imgSelectLike.setOnClickListener(v -> mListener.onClickImageSelectLike(comment.postID));
        holder.imgSelectedLike.setOnClickListener(v -> mListener.onClickImageSelectedLike(comment.postID));
        holder.imgSelectDislike.setOnClickListener(v -> mListener.onClickImageSelectDisLike(comment.postID));
        holder.imgSelectedDislike.setOnClickListener(v -> mListener.onClickImageSelectedDisLike(comment.postID));
        holder.txtCommentComment.setOnClickListener(v -> mListener.onClickComment(comment));
        holder.txtEdit.setOnClickListener(v -> mListener.onClickEdit(comment));
        holder.txtDelete.setOnClickListener(v -> mListener.onClickDelete(comment.postID, position));
        holder.txtCommentEdit.setOnClickListener(v -> mListener.onClickEdit(comment));
        holder.txtCommentDelete.setOnClickListener(v -> mListener.onClickDelete(comment.postID, position));
        if (comment.ownerUsrID == SharedUtil.getSharedUserID()) {
            holder.lltAvatar.setBackgroundResource(0);
        } else {
            holder.lltAvatar.setBackgroundResource(comment.hasRecentPost == 1 ? R.drawable.ring_shape_2 : 0);
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUser, imgDisableLike, imgDisableDislike,
                imgSelectLike, imgSelectDislike, imgSelectedLike, imgSelectedDislike, imgVerify, imgLastCommentUser;
        public TextView txtUserName, txtUserPoint, txtCreated, txtComment, txtEdit, txtDelete, txtCommentComment, txtNetVote, txtCommentEdit, txtCommentDelete, txtAnswerCnt;
        public LinearLayout lltHandle, lltHandleComment, lltAvatar, lltLastComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgDisableLike = itemView.findViewById(R.id.imgLikeDisable);
            imgDisableDislike = itemView.findViewById(R.id.imgDisLikeDisable);
            imgSelectLike = itemView.findViewById(R.id.imgLike);
            imgSelectDislike = itemView.findViewById(R.id.imgDisLike);
            imgSelectedLike = itemView.findViewById(R.id.imgLiked);
            imgSelectedDislike = itemView.findViewById(R.id.imgDisLiked);
            imgVerify = itemView.findViewById(R.id.img_verified);
            txtUserName = itemView.findViewById(R.id.txt_username);
            txtUserPoint = itemView.findViewById(R.id.txtUserPoints);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtEdit = itemView.findViewById(R.id.txtEdit);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            txtCommentComment = itemView.findViewById(R.id.txtCommentComment);
            txtNetVote = itemView.findViewById(R.id.txtNetVote);
            lltHandle = itemView.findViewById(R.id.lltHandle);

            lltHandleComment = itemView.findViewById(R.id.lltHandleComment);
            txtCommentEdit = itemView.findViewById(R.id.txtCommentEdit);
            txtCommentDelete = itemView.findViewById(R.id.txtCommentDelete);

            lltAvatar = itemView.findViewById(R.id.lltAvatar);

            lltLastComment = itemView.findViewById(R.id.lltLastComment);
            imgLastCommentUser = itemView.findViewById(R.id.imgUserLastComment);
            txtAnswerCnt = itemView.findViewById(R.id.txtCntAnswer);
        }
    }

    public interface CommentCellListener {
        void onClickUserImage(int userID);
        default void onClickComment(CommentModel comment){};
        default void onClickImageSelectLike(int commentID){};
        default void onClickImageSelectedLike(int commentID) {};
        default void onClickImageSelectDisLike(int commentID){};
        default void onClickImageSelectedDisLike(int commentID){};
        default void onClickEdit(CommentModel comment){};
        default void onClickDelete(int commentID, int position){};
        default void onClickMention(int userid){};
    }
}
