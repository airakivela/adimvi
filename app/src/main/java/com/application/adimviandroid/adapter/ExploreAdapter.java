package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.ExploreModel;
import com.application.adimviandroid.types.HomeExplorType;
import com.application.adimviandroid.utils.AppConstant;

public class ExploreAdapter extends PagerAdapter {

    private Context mContext;
    private OnClickExploreCellListener mListener;

    public ExploreAdapter(Context mContext, OnClickExploreCellListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return AppConstant.EXPLORELIST.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ExploreModel model = AppConstant.EXPLORELIST.get(position);
        ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_explore, container, false);
        ImageView imgExplore = layout.findViewById(R.id.imgExplore);
        TextView txtExploreTitle = layout.findViewById(R.id.txtExploreTitle);
        TextView txtExploreDesc = layout.findViewById(R.id.txtExplorDesc);
        imgExplore.setImageResource(model.imgRes);
        txtExploreTitle.setText(model.title);
        txtExploreDesc.setText(model.desc);
        imgExplore.setOnClickListener(v -> mListener.onClickExploreCell(HomeExplorType.values()[position]));
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface OnClickExploreCellListener {
        void onClickExploreCell(HomeExplorType type);
    }
}
