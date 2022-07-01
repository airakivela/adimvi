package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.InAppFeatureModel;
import com.application.adimviandroid.utils.AppConstant;

public class InAppFeatureAdapter extends RecyclerView.Adapter<InAppFeatureAdapter.ViewHolder> {

    private InAppFeatureListener mListener;
    private Context mContext;

    public InAppFeatureAdapter(Context mContext, InAppFeatureListener mListener) {
        this.mListener = mListener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_iap_feature, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InAppFeatureModel model = AppConstant.INAPPFEATURES.get(position);
        holder.txtNo.setText(model.timeDay);
        holder.txtPrice.setText(model.price);
        holder.txtDuration.setText(model.duration);
        holder.txtDesc.setText(model.title);
        holder.lltTrail.setVisibility(position == AppConstant.INAPPFEATURES.size() - 1 ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> mListener.onClickItemListener(position));
    }

    @Override
    public int getItemCount() {
        return AppConstant.INAPPFEATURES.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNo, txtPrice, txtDesc, txtDuration;
        public LinearLayout lltTrail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNo = itemView.findViewById(R.id.txtNo);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            lltTrail = itemView.findViewById(R.id.lltTrail);
        }
    }

    public interface InAppFeatureListener {
        void onClickItemListener(int index);
    }
}
