package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class RecentWallUserAdapter extends RecyclerView.Adapter<RecentWallUserAdapter.ViewHolder> {

    private Context mContext;
    private List<MuroModel> mList = new ArrayList<>();
    private RecentWallUserListener mListener;

    public RecentWallUserAdapter(Context context, List<MuroModel> list, RecentWallUserListener listener) {
        this.mContext = context;
        this.mList = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recent_wall_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MuroModel recentWallUserModel = mList.get(position);
        if (recentWallUserModel.paid == 1) {
            holder.rltMainBG.setBackgroundResource(R.drawable.round_border_green);
        } else {
            holder.rltMainBG.setBackgroundResource(R.drawable.round_orange_border30);
        }
        AppUtil.loadImageByUrl(mContext, holder.imgUser, recentWallUserModel.userAvatar, ImagePlaceHolderType.USERIMAGE);
        holder.itemView.setOnClickListener(v -> mListener.onClickRecentWallUser(position));
        if (position == mList.size() - 1) {
            holder.lltTrail.setVisibility(View.VISIBLE);
        } else {
            holder.lltTrail.setVisibility(View.GONE);
        }
        holder.imgVerify.setVisibility(recentWallUserModel.verify == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify;
        public LinearLayout lltTrail;
        public RelativeLayout rltMainBG;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            lltTrail = itemView.findViewById(R.id.lltTrail);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            rltMainBG = itemView.findViewById(R.id.rltMainBG);
        }
    }

    public interface RecentWallUserListener {
        void onClickRecentWallUser(int position);
    }
}
