package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.TagModel;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private Context mContext;
    private List<TagModel> mTags = new ArrayList<>();
    private TagCellListener mListener;

    public TagAdapter(Context mContext, List<TagModel> mTags, TagCellListener mListener) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagModel tag = mTags.get(position);
        holder.txtTag.setText(tag.tagTitle);
        holder.itemView.setOnClickListener(v -> mListener.onClickTagCell(tag));
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTag = itemView.findViewById(R.id.txtTag);
        }
    }

    public interface TagCellListener {
        void onClickTagCell(TagModel tag);
    }
}
