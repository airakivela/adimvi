package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.MessageModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MessageModel> mMessages = new ArrayList<>();
    private MessageCellListener mListenter;

    public MessageAdapter(Context context, List<MessageModel> mMessages, MessageCellListener listener) {
        this.mContext = context;
        this.mMessages = mMessages;
        this.mListenter = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0 ? new MineView(LayoutInflater.from(mContext).inflate(R.layout.item_message_mine, parent, false)) :
                new OtherView(LayoutInflater.from(mContext).inflate(R.layout.item_message_other, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = mMessages.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                MineView holderMine = (MineView) holder;
                holderMine.imgVerify.setVisibility(message.verify == 1 ? View.VISIBLE : View.GONE);
                holderMine.txtUserName.setText(message.userName);
                holderMine.txtContent.setText(message.content);
                holderMine.txtCreated.setText(message.created);
                AppUtil.loadImageByUrl(mContext, holderMine.imgUser, message.imgUser, ImagePlaceHolderType.USERIMAGE);
                if (message.imgContent.isEmpty()) {
                    holderMine.imgPost.setVisibility(View.GONE);
                    holderMine.cardImg.setVisibility(View.GONE);
                } else {
                    holderMine.imgPost.setVisibility(View.VISIBLE);
                    holderMine.cardImg.setVisibility(View.VISIBLE);
                    AppUtil.loadImageByUrl(mContext, holderMine.imgPost, message.imgContent, ImagePlaceHolderType.POSTIMAGE);
                }
                holderMine.itemView.setOnClickListener(v -> mListenter.onClickMessageCell(message.imgContent.isEmpty() ? null : (((BitmapDrawable) holderMine.imgPost.getDrawable()).getBitmap())));
                break;
            case 1:
                OtherView holderOther = (OtherView) holder;
                holderOther.imgVerify.setVisibility(message.verify == 1 ? View.VISIBLE : View.GONE);
                holderOther.txtUserName.setText(message.userName);
                holderOther.txtContent.setText(message.content);
                holderOther.txtCreated.setText(message.created);
                AppUtil.loadImageByUrl(mContext, holderOther.imgUser, message.imgUser, ImagePlaceHolderType.USERIMAGE);
                if (message.imgContent.isEmpty()) {
                    holderOther.imgPost.setVisibility(View.GONE);
                    holderOther.cardImg.setVisibility(View.GONE);
                } else {
                    holderOther.imgPost.setVisibility(View.VISIBLE);
                    holderOther.cardImg.setVisibility(View.VISIBLE);
                    AppUtil.loadImageByUrl(mContext, holderOther.imgPost, message.imgContent, ImagePlaceHolderType.POSTIMAGE);
                }
                holderOther.itemView.setOnClickListener(v -> mListenter.onClickMessageCell(message.imgContent.isEmpty() ? null : (((BitmapDrawable) holderOther.imgPost.getDrawable()).getBitmap())));
                holderOther.lltAvatar.setBackgroundResource(message.hasRecentPost == 1 ? R.drawable.ring_shape_2 : 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).isMine ? 0 : 1;
    }

    public class OtherView extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify, imgPost;
        public TextView txtUserName, txtContent, txtCreated;
        public CardView cardImg;
        public LinearLayout lltAvatar;

        public OtherView(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            imgPost = itemView.findViewById(R.id.imgContent);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            cardImg = itemView.findViewById(R.id.imgCard);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);
        }
    }

    public class MineView extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify, imgPost;
        public TextView txtUserName, txtContent, txtCreated;
        public CardView cardImg;
        public MineView(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            imgPost = itemView.findViewById(R.id.imgContent);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            cardImg = itemView.findViewById(R.id.imgCard);
        }
    }

    public interface MessageCellListener {
        void onClickMessageCell(Bitmap bitmap);
    }
}
