package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class CategroyAdapter extends RecyclerView.Adapter<CategroyAdapter.ViewHolder> {
    private Context mContext;
    private List<CategoryModel> mCategories = new ArrayList<>();
    private OnClickCategoryCellListener mListener;

    public CategroyAdapter(Context mContext, List<CategoryModel> mCategories, OnClickCategoryCellListener mListener) {
        this.mContext = mContext;
        this.mCategories = mCategories;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel model = mCategories.get(position);
        holder.txtTitle.setText(model.title);
        AppUtil.loadImageByUrl(mContext, holder.imgCategory, model.imgUrl, ImagePlaceHolderType.POSTIMAGE);
        holder.imgCategory.setOnClickListener(v -> mListener.onClickCategoryCell(model));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgCategory;
        public TextView txtTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    public interface OnClickCategoryCellListener {
        void onClickCategoryCell(CategoryModel model);
    }
}
