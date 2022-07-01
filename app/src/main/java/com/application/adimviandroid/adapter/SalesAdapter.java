package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.BuyPostModel;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    private Context mContext;
    private List<BuyPostModel> mList;

    public SalesAdapter(Context context, List<BuyPostModel> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sales, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BuyPostModel model = mList.get(position);
        holder.txtUsd.setText(model.usd);
        holder.txtDate.setText(model.created);
        holder.txtUser.setText(model.userName);
        holder.txtPostID.setText("" + model.postID);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtUsd, txtDate, txtUser, txtPostID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsd = itemView.findViewById(R.id.txtUsd);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtPostID = itemView.findViewById(R.id.txtPostID);
        }
    }
}
