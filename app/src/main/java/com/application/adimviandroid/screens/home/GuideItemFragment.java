package com.application.adimviandroid.screens.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.GuideModel;
import com.application.adimviandroid.utils.AppConstant;
import com.google.gson.Gson;

public class GuideItemFragment extends Fragment {

    public GuideItemFragment() {
        // Required empty public constructor
    }

    public static GuideItemFragment newInstance(int position) {
        GuideItemFragment fragment = new GuideItemFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        GuideModel model = AppConstant.GUDIES.get(position);
        String strArg = gson.toJson(model);
        args.putString("Args", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_item, container, false);
        Gson gson = new Gson();
        GuideModel model = gson.fromJson(getArguments().getString("Args"), GuideModel.class);
        ImageView imgRes = view.findViewById(R.id.imgRes);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtContent = view.findViewById(R.id.txtContent);
        TextView txtSubContent = view.findViewById(R.id.txtSubContent);
        imgRes.setImageResource(model.imgRes);
        txtTitle.setText(model.title);
        txtContent.setText(model.content);
        txtSubContent.setText(model.subContent);
        txtSubContent.setVisibility(model.subContent.isEmpty() ? View.GONE : View.VISIBLE);
        LinearLayout lltLast = view.findViewById(R.id.lltLast);
        lltLast.setVisibility(model.subContent.isEmpty() ? View.GONE : View.VISIBLE);
        return view;
    }
}