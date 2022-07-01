package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatModel> mChats = new ArrayList<>();
    private ChatCellListener mListener;

    public ChatAdapter(Context mContext, List<ChatModel> mChats, ChatCellListener mListener) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chat = mChats.get(position);
        AppUtil.loadImageByUrl(mContext, holder.imgUser, chat.imgAvatar, ImagePlaceHolderType.USERIMAGE);
        holder.txtUserName.setText(chat.userName);
        holder.imgVerify.setVisibility(chat.verified == 1 ? View.VISIBLE : View.GONE);
        holder.txtCreated.setText(chat.created);
        holder.txtContent.setText(chat.content);
        holder.itemView.setOnClickListener(v -> mListener.onClickChatCell(chat));
        holder.lltAvatar.setBackgroundResource(chat.hasRecentPost == 1 ? R.drawable.ring_shape_2 : 0);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify;
        public TextView txtUserName, txtCreated, txtContent;
        public LinearLayout lltAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtUserName = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtMessage);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);
        }
    }

    public interface ChatCellListener {
        void onClickChatCell(ChatModel chat);
    }
}
