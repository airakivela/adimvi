package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
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
import com.application.adimviandroid.models.RoomMessageModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.List;

public class RoomOtherMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RoomMessageModel> messages;
    private RoomOtheerMessageListener listener;

    public RoomOtherMessageAdapter(Context mContext, List<RoomMessageModel> messages, RoomOtheerMessageListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0 ? new NoneVoiceView(LayoutInflater.from(mContext).inflate(R.layout.item_room_othr_message, parent, false))
                : new VoiceView(LayoutInflater.from(mContext).inflate(R.layout.item_room_voice_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RoomMessageModel message = messages.get(position);
        if (holder.getItemViewType() == 0) {
            NoneVoiceView holderNoneVoice = (NoneVoiceView) holder;
            if (message.senderAvatar.isEmpty()) {
                holderNoneVoice.imgUser.setImageResource(R.drawable.ic_user_placehoder);
            } else {
                AppUtil.loadImageByUrl(mContext, holderNoneVoice.imgUser, message.senderAvatar, ImagePlaceHolderType.USERIMAGE);
            }
            holderNoneVoice.txtUser.setText(message.userName);
            holderNoneVoice.imgVerify.setVisibility(message.senderVerify == 1 ? View.VISIBLE : View.GONE);
            holderNoneVoice.txtContent.setText(message.content);
            if (message.format.equals("0")) {
                holderNoneVoice.txtContent.setTextColor(mContext.getColor(R.color.black_white));
            } else if (message.format.equals("1")) {
                holderNoneVoice.txtContent.setTextColor(mContext.getColor(R.color.mainGreen));
            } else if (message.format.equals("2")) {
                holderNoneVoice.txtContent.setTextColor(mContext.getColor(R.color.mainYellow));
            }
            if (message.extra.isEmpty()) {
                holderNoneVoice.crdExtra.setVisibility(View.GONE);
                holderNoneVoice.imgExtra.setVisibility(View.GONE);
            } else {
                holderNoneVoice.crdExtra.setVisibility(View.VISIBLE);
                holderNoneVoice.imgExtra.setVisibility(View.VISIBLE);
                AppUtil.loadImageByUrl(mContext, holderNoneVoice.imgExtra, message.extra, ImagePlaceHolderType.POSTIMAGE);
                holderNoneVoice.imgExtra.setOnClickListener(v -> listener.onClickExtra((((BitmapDrawable) holderNoneVoice.imgExtra.getDrawable()).getBitmap())));
            }
        } else {
            VoiceView voiceView = (VoiceView) holder;
            voiceView.lltLeft.setVisibility(View.GONE);
            voiceView.lltRight.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mContext, voiceView.imgUser, message.senderAvatar, ImagePlaceHolderType.USERIMAGE);
            voiceView.itemView.setOnClickListener(v -> listener.onClickVoiceCell(message));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).format.equals("3") ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface RoomOtheerMessageListener {
        void onClickExtra(Bitmap bitmap);
        void onClickVoiceCell(RoomMessageModel model);
    }

    public class NoneVoiceView extends RecyclerView.ViewHolder {

        private ImageView imgUser, imgVerify, imgExtra;
        private TextView txtUser, txtContent;
        private CardView crdExtra;

        public NoneVoiceView(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUser = itemView.findViewById(R.id.txtUserName);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            txtContent = itemView.findViewById(R.id.txtContent);
            crdExtra = itemView.findViewById(R.id.crdExtra);
            imgExtra = itemView.findViewById(R.id.imgExtra);
        }
    }

    public class VoiceView extends RecyclerView.ViewHolder {

        public ImageView imgUser;
        public LinearLayout lltLeft, lltRight;

        public VoiceView(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            lltLeft = itemView.findViewById(R.id.lltLeft);
            lltRight = itemView.findViewById(R.id.lltRight);
        }
    }
}
