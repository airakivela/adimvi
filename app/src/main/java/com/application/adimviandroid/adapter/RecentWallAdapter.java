package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;
import com.application.adimviandroid.utils.SwipeTouchListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class RecentWallAdapter extends RecyclerView.Adapter<RecentWallAdapter.ViewHolder> {

    private Context mContext;
    private List<MuroModel> mMuros = new ArrayList<>();
    private RecentWallListener mListner;

    public RecentWallAdapter(Context context, RecentWallListener listener) {
        this.mContext = context;
        this.mMuros = AppUtil.gRecentWallUsers;
        this.mListner = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recent_wall, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MuroModel model = mMuros.get(position);
        if (model.userAvatar.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_user_placehoder);
            Bitmap bluredBitmap = AppUtil.blurRenderScript(mContext, bitmap, 25);
            holder.imgBigUser.setImageBitmap(bluredBitmap);
            holder.imgUser.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrlWithBlur(mContext, holder.imgBigUser, model.userAvatar, ImagePlaceHolderType.USERIMAGE);
            AppUtil.loadImageByUrl(mContext, holder.imgUser, model.userAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        holder.txtUserName.setText(model.username);
        holder.imgVerify.setVisibility(model.verify == 1 ? View.VISIBLE : View.GONE);
        holder.txtCreated.setText(model.created);
        holder.txtContent.setText(model.content);
        if (model.imageUrl.isEmpty()) {
            holder.cardPostImg.setVisibility(View.GONE);
        } else {
            holder.cardPostImg.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, holder.imgExtra, model.imageUrl, ImagePlaceHolderType.POSTIMAGE);
        }
        if (model.tags == null || model.tags.size() == 0) {
            holder.tagContainerLayout.setVisibility(View.GONE);
        } else {
            holder.tagContainerLayout.setVisibility(View.VISIBLE);
            List<String> strTags = new ArrayList<>();
            for (TagModel tagModel: model.tags) {
                strTags.add(tagModel.tagTitle);
            }
            holder.tagContainerLayout.setTags(strTags);
            holder.tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {
                    mListner.onClickTag(model.tags.get(position));
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

        if (model.favourite.equals("0")) {
            holder.imgLike.setImageResource(R.drawable.ic_heart);
        } else {
            holder.imgLike.setImageResource(R.drawable.ic_heart_red);
        }

        holder.txtLikeCnt.setText(model.totalFav);

        if (model.rewallModel == null) {
            holder.lltOriginWall.setVisibility(View.GONE);
        } else {
            holder.lltOriginWall.setVisibility(View.VISIBLE);
            if (model.rewallModel.userAvatar.isEmpty()) {
                holder.imgOriginWallUser.setImageResource(R.drawable.ic_user_placehoder);
            } else {
                AppUtil.loadImageByUrl(mContext, holder.imgOriginWallUser, model.rewallModel.userAvatar, ImagePlaceHolderType.USERIMAGE);
            }
            holder.imgOriginWallUserVeify.setVisibility(model.rewallModel.verify == 1 ? View.VISIBLE : View.GONE);
            holder.txtOriginWallUserName.setText(model.rewallModel.username);
            holder.txtOriginWallCreated.setText(model.rewallModel.created);
            holder.txtOriginWallContent.setText(model.rewallModel.content);
            if (model.rewallModel.imageUrl.isEmpty()) {
                holder.cardOriginWallPostImg.setVisibility(View.GONE);
            } else {
                holder.cardOriginWallPostImg.setVisibility(View.VISIBLE);
                AppUtil.loadImageByUrl(mContext, holder.imgOriginWallExtra, model.rewallModel.imageUrl, ImagePlaceHolderType.POSTIMAGE);
            }
        }
        if (model.repostModel == null) {
            holder.rltOriginPost.setVisibility(View.GONE);
        } else {
            holder.rltOriginPost.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, holder.imgOriginPost, model.repostModel.content, ImagePlaceHolderType.POSTIMAGE);
            holder.txtOriginPostContent.setText(model.repostModel.title);
            holder.txtOriginPostCreated.setText(model.repostModel.created);
        }

        if (model.followStatus == 0) {
            holder.imgFollow.setImageResource(R.drawable.ic_follow_white);
            holder.cardFollow.setCardBackgroundColor(Color.parseColor("#343434"));
        } else {
            holder.imgFollow.setImageResource(R.drawable.ic_unfollow);
            holder.cardFollow.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.imgUser.setOnClickListener(v -> mListner.onClickUserAvatar(model.fromuserID));
        holder.cardFollow.setOnClickListener(v -> mListner.onClickFollow(position));
        ChatModel chat = new ChatModel();
        chat.userID = model.fromuserID;
        chat.userName = model.username;
        chat.imgAvatar = model.userAvatar;
        chat.verified = model.verify;
        holder.lltMessage.setOnClickListener(v -> mListner.onClickMessage(chat));
        holder.txtRemuro.setOnClickListener(v -> {
            mListner.onClickRemuro(model);
        });
        holder.imgLike.setOnClickListener(v -> mListner.onClickLike(position));
        holder.rltOriginPost.setOnClickListener(v -> mListner.onClickOriginPost(model.repostModel.id));
        holder.txtComment.setOnClickListener(v -> mListner.onClickComment(model.messageID, model.fromuserID));
        holder.txtOriginWallComment.setOnClickListener(v -> {
            mListner.onClickComment(model.rewallModel.id, SharedUtil.getSharedUserID());
        });
        holder.txtOriginWallRemuro.setOnClickListener(v -> {
            mListner.onClickOriginWallRemuro(model.rewallModel);
        });
        holder.imgExtra.setOnClickListener(v -> mListner.onClickPostImage(((BitmapDrawable) holder.imgExtra.getDrawable()).getBitmap()));
        holder.imgOriginWallExtra.setOnClickListener(v -> {
            mListner.onClickOriginWallImage(((BitmapDrawable) holder.imgOriginWallExtra.getDrawable()).getBitmap());
        });

       if (!AppUtil.gVisitedRecentWallUsers.get(position)) {
           Map<String, String> param = new HashMap<>();
           param.put("entityID", "" + model.fromuserID);
           param.put("userID", "" + SharedUtil.getSharedUserID());
           ApiUtil.onAPIConnectionResponse(ApiUtil.UPDATE_USER_PROFILE_VISIT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
               @Override
               public void onEventCallBack(JSONObject obj) {
                   AppUtil.gVisitedRecentWallUsers.set(position, true);
               }

               @Override
               public void onEventInternetError(Exception e) {
                   e.printStackTrace();
                   AppUtil.gVisitedRecentWallUsers.set(position, true);
               }

               @Override
               public void onEventServerError(Exception e) {
                   e.printStackTrace();
                   AppUtil.gVisitedRecentWallUsers.set(position, true);
               }
           });
       }

        if (model.cntAnswer == 0) {
            holder.lltLastWallComment.setVisibility(View.GONE);
        } else {
            holder.lltLastWallComment.setVisibility(View.VISIBLE);
            holder.txtAnswerCnt.setText(StringUtil.convertThousand(model.cntAnswer) + " respuestas");
            if (model.lastCommentUserAvatar.isEmpty()) {
                holder.imgLastCommentUser.setImageResource(R.drawable.ic_user_placehoder);
            } else {
                AppUtil.loadImageByUrl(mContext, holder.imgLastCommentUser, model.lastCommentUserAvatar, ImagePlaceHolderType.USERIMAGE);
            }
            holder.txtAnswerCnt.setOnClickListener(v -> mListner.onClickComment(model.messageID, SharedUtil.getSharedUserID()));
        }

       onCallUpdateRewallCnt(model.messageID, model.fromuserID);
    }

    private void onCallUpdateRewallCnt(int messageID, int userid) {
        if (userid == SharedUtil.getSharedUserID()) {
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("messageID", "" + messageID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.UPDATE_WALL_COUNT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    @Override
    public int getItemCount() {
        return mMuros.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgBigUser, imgUser, imgVerify, imgExtra , imgOriginWallUser, imgOriginWallUserVeify, imgOriginWallExtra, imgOriginPost,
                imgLike, imgFollow, imgLastCommentUser;
        public TextView txtUserName, txtCreated, txtContent, txtOriginWallUserName, txtOriginWallCreated, txtOriginWallContent, txtOriginWallComment, txtOriginWallRemuro,
                txtOriginPostCreated, txtOriginPostContent, txtLikeCnt, txtRemuro, txtComment, txtAnswerCnt;
        public TagContainerLayout tagContainerLayout;
        public CardView cardPostImg, cardOriginWallPostImg, cardFollow;
        public RelativeLayout rltOriginPost;
        public LinearLayout lltOriginWall, lltMessage, lltLastWallComment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBigUser = itemView.findViewById(R.id.imgBigUser);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            imgExtra = itemView.findViewById(R.id.imgPost);
            imgOriginWallUser = itemView.findViewById(R.id.imgOriginWallUser);
            imgOriginWallUserVeify = itemView.findViewById(R.id.imgOriginWallVerify);
            imgOriginWallExtra = itemView.findViewById(R.id.imgOriginWallPost);
            imgOriginPost = itemView.findViewById(R.id.imgOriginPostImge);
            imgLike = itemView.findViewById(R.id.imgProfileHeart);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtOriginWallUserName = itemView.findViewById(R.id.txtOriginWallUseName);
            txtOriginWallCreated = itemView.findViewById(R.id.txtOriginWallCreated);
            txtOriginWallContent = itemView.findViewById(R.id.txtOriginWallContent);
            txtOriginWallComment = itemView.findViewById(R.id.txtProfileOriginWallComentar);
            txtOriginWallRemuro = itemView.findViewById(R.id.txtProfileOriginWallRemuro);
            txtOriginPostCreated = itemView.findViewById(R.id.txtOriginPostCreated);
            txtOriginPostContent = itemView.findViewById(R.id.txtOriginPostContent);
            txtLikeCnt = itemView.findViewById(R.id.txtProfileLiketCnt);
            txtRemuro = itemView.findViewById(R.id.txtProfileRemuro);
            txtComment = itemView.findViewById(R.id.txtProfileComentar);
            tagContainerLayout = itemView.findViewById(R.id.tagContainer);
            cardPostImg = itemView.findViewById(R.id.cardPostImg);
            cardOriginWallPostImg = itemView.findViewById(R.id.cardOriginWallPostImg);
            rltOriginPost = itemView.findViewById(R.id.lltOriginPost);
            lltOriginWall = itemView.findViewById(R.id.lltOriginWall);
            lltMessage = itemView.findViewById(R.id.lltMessage);
            imgFollow = itemView.findViewById(R.id.imgFollow);
            cardFollow = itemView.findViewById(R.id.crdFollow);

            lltLastWallComment = itemView.findViewById(R.id.lltLastComment);
            imgLastCommentUser = itemView.findViewById(R.id.imgUserLastComment);
            txtAnswerCnt = itemView.findViewById(R.id.txtCntAnswer);
        }
    }

    public interface RecentWallListener {
        void onClickUserAvatar(int userID);
        void onClickPostImage(Bitmap bitmap);
        void onClickOriginWallImage(Bitmap bitmap);
        void onClickOriginPost(int postID);
        void onClickComment(int postID, int fromUserID);
        void onClickLike(int position);
        void onClickRemuro(MuroModel model);
        void onClickTag(TagModel tagModel);
        void onClickFollow(int position);
        void onClickMessage(ChatModel model);
        void onClickOriginWallRemuro(RewallModel rewall);
    }
}
