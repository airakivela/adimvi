package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.RecentModel;

import java.util.ArrayList;
import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    private Context mContext;
    private List<RecentModel> mRecents = new ArrayList<>();
    private RecentCellListener mListener;

    public RecentAdapter(Context mContext, List<RecentModel> recents, RecentCellListener mListener) {
        this.mContext = mContext;
        this.mRecents = recents;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentModel recent = mRecents.get(position);
        holder.txtMsg.setText(recent.message);
        holder.txtCreated.setText(recent.created);
        holder.txtTitle.setText(recent.title);
        holder.itemView.setOnClickListener(v -> mListener.onClickRecentCell(recent));
    }

    @Override
    public int getItemCount() {
        return mRecents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtMsg, txtCreated, txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    public interface RecentCellListener {
        void onClickRecentCell(RecentModel recent);
    }
}
