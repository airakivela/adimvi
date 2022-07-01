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

import java.util.List;

public class RoomSiguiendoAdapter extends RecyclerView.Adapter<RoomSiguiendoAdapter.ViewHolder> {

    private Context mContext;
    private List<RoomModel> mRooms;
    private RoomSiguiendoListenr mListener;

    public RoomSiguiendoAdapter(Context context, List<RoomModel> rooms, RoomSiguiendoListenr listenr) {
        this.mContext = context;
        this.mRooms = rooms;
        this.mListener = listenr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_siguiendo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            holder.crdAddRoom.setVisibility(View.VISIBLE);
            holder.lltTrail.setVisibility(View.GONE);
            holder.txtUserName.setVisibility(View.INVISIBLE);
            holder.txtUserName.setText("");
        } else {
            RoomModel room = mRooms.get(position - 1);
            holder.crdAddRoom.setVisibility(View.GONE);
            holder.lltUser.setVisibility(View.VISIBLE);
            holder.txtUserName.setVisibility(View.VISIBLE);
            holder.txtUserName.setText(room.adminName);
            AppUtil.loadImageByUrl(mContext, holder.imgUser, ApiUtil.ImageUrl + room.adminAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        if (position == mRooms.size()) {
            holder.lltTrail.setVisibility(View.VISIBLE);
        } else {
            holder.lltTrail.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (position == 0) {
                mListener.onClickAddRoom();
            } else {
                RoomModel room = mRooms.get(position - 1);
                mListener.onClickSiguiendoRoom(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRooms.size() + 1;
    }

    public interface RoomSiguiendoListenr {
        void onClickAddRoom();
        void onClickSiguiendoRoom(RoomModel room);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView crdAddRoom;
        public LinearLayout lltUser, lltTrail;
        public ImageView imgUser;
        public TextView txtUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            crdAddRoom = itemView.findViewById(R.id.crdAddRoom);
            lltUser = itemView.findViewById(R.id.lltUser);
            lltTrail = itemView.findViewById(R.id.lltTrail);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
        }
    }
}
