package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.speech.tts.Voice;
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

import java.util.ArrayList;
import java.util.List;

public class RoomAdminMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RoomMessageModel> messages = new ArrayList<>();
    private RoomAdminListener listener;

    public RoomAdminMessageAdapter(Context mContext, List<RoomMessageModel> messages, RoomAdminListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0 ? new NoneVoiceView(LayoutInflater.from(mContext).inflate(R.layout.item_room_admin_message, parent, false))
                : new VoiceView(LayoutInflater.from(mContext).inflate(R.layout.item_room_voice_message, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RoomMessageModel message = messages.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                NoneVoiceView holderNoneVoice = (NoneVoiceView) holder;
                holderNoneVoice.txtMsg.setText(message.content);
                if (message.extra.isEmpty()) {
                    holderNoneVoice.imgMsg.setVisibility(View.GONE);
                    holderNoneVoice.crdImg.setVisibility(View.GONE);
                } else {
                    holderNoneVoice.imgMsg.setVisibility(View.VISIBLE);
                    holderNoneVoice.crdImg.setVisibility(View.VISIBLE);
                    AppUtil.loadImageByUrl(mContext, holderNoneVoice.imgMsg, message.extra, ImagePlaceHolderType.POSTIMAGE);
                    holderNoneVoice.crdImg.setOnClickListener(v -> listener.onClickMessageExtra((((BitmapDrawable) holderNoneVoice.imgMsg.getDrawable()).getBitmap())));
                }
                break;
            case 1:
                VoiceView holderVoice = (VoiceView) holder;
                holderVoice.lltLeft.setVisibility(View.VISIBLE);
                holderVoice.lltRight.setVisibility(View.GONE);
                AppUtil.loadImageByUrl(mContext, holderVoice.imgUser, message.senderAvatar, ImagePlaceHolderType.USERIMAGE);
                holderVoice.itemView.setOnClickListener(v -> listener.onClickVoiceCell(message));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).format.equals("3") ? 1 : 0;
    }

    public interface RoomAdminListener {
        void onClickMessageExtra(Bitmap bitmap);
        void onClickVoiceCell(RoomMessageModel messageModel);
    }

    public static class NoneVoiceView extends RecyclerView.ViewHolder {

        public TextView txtMsg;
        public ImageView imgMsg;
        public CardView crdImg;

        public NoneVoiceView(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMessage);
            imgMsg = itemView.findViewById(R.id.imgMessage);
            crdImg = itemView.findViewById(R.id.crdImgMsg);
        }
    }

    public static class VoiceView extends RecyclerView.ViewHolder {

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
