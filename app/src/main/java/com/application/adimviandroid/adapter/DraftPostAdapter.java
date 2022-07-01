package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.DraftPostModel;

import java.util.ArrayList;
import java.util.List;

public class DraftPostAdapter extends RecyclerView.Adapter<DraftPostAdapter.ViewHolder> {

    private Context mContext;
    private List<DraftPostModel> mDrafts = new ArrayList<>();
    private DraftPostListener mListener;

    public DraftPostAdapter(Context mContext, List<DraftPostModel> mDrafts, DraftPostListener mListener) {
        this.mContext = mContext;
        this.mDrafts = mDrafts;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_draft_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DraftPostModel draft = mDrafts.get(position);
        holder.txtTitle.setText(draft.postTitle);
        holder.itemView.setOnClickListener(v -> mListener.onClickCell(draft.postID));
    }

    @Override
    public int getItemCount() {
        return mDrafts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    public interface DraftPostListener {
        void onClickCell(int postID);
    }
}
