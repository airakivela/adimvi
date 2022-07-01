package com.application.adimviandroid.adapter;

import android.content.Context;
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
import com.application.adimviandroid.models.RoomModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.StringUtil;

import java.util.List;

public class RoomNormalAdapter extends RecyclerView.Adapter<RoomNormalAdapter.ViewHolder> {

    private Context mContext;
    private List<RoomModel> mRooms;
    private RoomNormalListenr mListener;

    public RoomNormalAdapter(Context context, List<RoomModel> rooms, RoomNormalListenr listenr) {
        this.mContext = context;
        this.mRooms = rooms;
        this.mListener = listenr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomModel room = mRooms.get(position);
        if (room.adminAvatar.isEmpty()) {
            holder.imgUser.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrl(mContext, holder.imgUser, room.adminAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        holder.txtUser.setText(room.adminName);
        holder.txtMemberCnt.setText(StringUtil.convertThousand(room.memberCnt));
        holder.txtRoomTitle.setText(room.title);
        holder.itemView.setOnClickListener(v -> mListener.onClickRoom(room));
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public interface RoomNormalListenr {
        void onClickRoom(RoomModel room);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser;
        public TextView txtUser, txtMemberCnt, txtRoomTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUser = itemView.findViewById(R.id.txtUserName);
            txtMemberCnt = itemView.findViewById(R.id.txtMemberCnt);
            txtRoomTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
