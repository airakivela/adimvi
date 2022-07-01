package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.FollowModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private Context mContext;
    private List<FollowModel> mFollows = new ArrayList<>();
    private FollowAdpaterListener mListener;

    public FollowAdapter(Context mContext, List<FollowModel> mFollows, FollowAdpaterListener mListener) {
        this.mContext = mContext;
        this.mFollows = mFollows;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowModel follow = mFollows.get(position);
        AppUtil.loadImageByUrl(mContext, holder.imgUser, follow.avatar, ImagePlaceHolderType.USERIMAGE);
        holder.imgVerify.setVisibility(follow.verify == 1 ? View.VISIBLE : View.GONE);
        holder.txtUserName.setText(follow.userName);
        holder.imgUser.setOnClickListener(v -> mListener.onClickUserAvatar(follow.userID));
        holder.lltAvatar.setBackgroundResource(follow.hasRecentPost == 1 ? R.drawable.ring_shape_4 : 0);
        if (follow.followStatus == 0) {
            holder.txtFollowStatus.setText("Seguir");
            holder.txtFollowStatus.setTextColor(mContext.getColor(R.color.mainOrange));
        } else if (follow.followStatus == 1) {
            holder.txtFollowStatus.setText("Siguiendo");
            holder.txtFollowStatus.setTextColor(mContext.getColor(R.color.darkLightGray));
        }
        holder.txtFollowStatus.setOnClickListener(v -> {
            if (follow.followStatus == 0) {
                holder.txtFollowStatus.setText("Siguiendo");
                holder.txtFollowStatus.setTextColor(mContext.getColor(R.color.darkLightGray));
                follow.followStatus = 1;
            } else if (follow.followStatus == 1) {
                holder.txtFollowStatus.setText("Seguir");
                holder.txtFollowStatus.setTextColor(mContext.getColor(R.color.mainOrange));
                follow.followStatus = 0;
            }
            mListener.onClickFollow(follow.userID);
        });
        holder.txtSeguiendo.setText(follow.followers + " Seguidores");
        holder.txtSiguiendo.setText(follow.followings + " Siguiendo");
    }

    @Override
    public int getItemCount() {
        return mFollows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify;
        public TextView txtUserName, txtSeguiendo, txtSiguiendo, txtFollowStatus;
        public LinearLayout lltAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            lltAvatar = itemView.findViewById(R.id.lltAvatar);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtSiguiendo = itemView.findViewById(R.id.txtSiguiendo);
            txtSeguiendo = itemView.findViewById(R.id.txtSeguiendo);
        }
    }

    public interface FollowAdpaterListener {
        void onClickUserAvatar(int userID);
        void onClickFollow(int userID);
    }
}
