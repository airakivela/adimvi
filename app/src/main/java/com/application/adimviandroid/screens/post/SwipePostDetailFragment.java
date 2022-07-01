package com.application.adimviandroid.screens.post;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.FeaturePostAdapter;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.ui.PromptDialog;
import com.application.adimviandroid.ui.SetVotePostDialog;

import org.json.JSONObject;

import java.util.List;

public class SwipePostDetailFragment extends Fragment {

    private MainActivity mActivity;
    private List<JSONObject> jsonObjectList;
    private int tabIndex;
    private int selectedIndex;

    private AlertDialog alertDialog;
    private PromptDialog promptDialog;
    private ImageDialog imageDialog;
    private SetVotePostDialog votePostDialog;

    private ImageView imgBack;
    private TextView txtTitle;
    private RecyclerView rclPost;

    private FeaturePostAdapter adapter;

    public SwipePostDetailFragment() {
        // Required empty public constructor
    }

    public SwipePostDetailFragment(MainActivity mActivity, List<JSONObject> jsonObjectList, int tabIndex, int selectedIndex) {
        this.mActivity = mActivity;
        this.jsonObjectList = jsonObjectList;
        this.tabIndex = tabIndex;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }
        if (imageDialog != null && imageDialog.isShowing()) {
            imageDialog.dismiss();
        }
        if (votePostDialog != null && votePostDialog.isShowing()) {
            votePostDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Post");
        rclPost = view.findViewById(R.id.rclPost);
        rclPost.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclPost);
        adapter = new FeaturePostAdapter(mActivity, jsonObjectList, new FeaturePostAdapter.FeaturePostAdpaterListener() {
        });
        rclPost.setAdapter(adapter);
    }
}