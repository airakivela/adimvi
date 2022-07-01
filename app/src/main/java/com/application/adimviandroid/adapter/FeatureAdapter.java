package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.FeaturModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;
import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    private Context mContext;
    private List<FeaturModel> mList = new ArrayList<>();
    private onClickFeatureCellListener mListener;

    public FeatureAdapter(Context mContext, List<FeaturModel> mList, onClickFeatureCellListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FeatureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_feature_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeaturModel model = mList.get(position);
        AppUtil.loadImageByUrl(mContext, holder.imgPost, model.postImgUrl, ImagePlaceHolderType.POSTIMAGE);
        holder.txtTitle.setText(model.postTitle);
        holder.txtReadingTime.setText(model.readTime);
        holder.txtVoteCnt.setText(model.voteCnt);
        holder.txtCommentCnt.setText("" + model.commentCnt);
        AppUtil.setWebViewThemeMode(mContext, holder.webLink);
        holder.webLink.loadUrl(model.webUVLink);
        holder.webLink.setBackgroundColor(Color.TRANSPARENT);
        if (position != mList.size() -1) {
            holder.lltTrail.setVisibility(View.GONE);
        } else {
            holder.lltTrail.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> mListener.onClickFeatureCell(model.postID));
        if (model.userAvatar.isEmpty()) {
            holder.imgUser.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrl(mContext, holder.imgUser, model.userAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        holder.imgVerify.setVisibility(model.userVerified == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SelectableRoundedImageView imgPost;
        public WebView webLink;
        public TextView txtTitle, txtCommentCnt, txtVoteCnt, txtReadingTime;
        public LinearLayout lltTrail;
        public ImageView imgUser, imgVerify;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.imgPost);
            webLink = itemView.findViewById(R.id.webLink);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCommentCnt = itemView.findViewById(R.id.txtComentCnt);
            txtVoteCnt = itemView.findViewById(R.id.txtVote);
            txtReadingTime = itemView.findViewById(R.id.txtReadTime);
            lltTrail = itemView.findViewById(R.id.lltTrail);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
        }
    }

    public interface onClickFeatureCellListener {
        void onClickFeatureCell(int index);
    }
}
