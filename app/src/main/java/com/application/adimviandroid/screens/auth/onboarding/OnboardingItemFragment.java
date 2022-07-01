package com.application.adimviandroid.screens.auth.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.OnboardingModel;
import com.application.adimviandroid.utils.AppConstant;

public class OnboardingItemFragment extends Fragment {

    public OnboardingItemFragment() {

    }

    public static OnboardingItemFragment newInstance(int sectionNumber) {
        OnboardingItemFragment fragment = new OnboardingItemFragment();
        Bundle args = new Bundle();
        OnboardingModel model = AppConstant.ONBOARDINLIST.get(sectionNumber - 1);
        args.putInt("IMGRESOURCE", model.imgRes);
        args.putInt("TITLERESOURCE", model.strTitle);
        args.putInt("CONTENTRESOURCE", model.strContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_item, container, false);
        TextView txtTitle = view.findViewById(R.id.txt_onboarding_title);
        txtTitle.setText(getArguments().getInt("TITLERESOURCE"));
        TextView txtContent = view.findViewById(R.id.txt_onboarding_content);
        txtContent.setText(getArguments().getInt("CONTENTRESOURCE"));
        ImageView imgOnboarding = view.findViewById(R.id.img_onboarding);
        imgOnboarding.setImageResource(getArguments().getInt("IMGRESOURCE"));
        return view;
    }
}