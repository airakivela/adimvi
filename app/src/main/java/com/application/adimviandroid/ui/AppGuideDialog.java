package com.application.adimviandroid.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.GuideAdapter;
import com.application.adimviandroid.utils.SharedUtil;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class AppGuideDialog extends DialogFragment {

    public AppGuideDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_app_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView imgClose = view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(v -> {
            SharedUtil.setPassGuide(true);
            dismiss();
        });
        DotsIndicator indicator = view.findViewById(R.id.indicator);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        GuideAdapter adapter = new GuideAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_guide);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
