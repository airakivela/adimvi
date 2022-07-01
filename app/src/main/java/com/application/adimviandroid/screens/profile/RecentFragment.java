package com.application.adimviandroid.screens.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.RecentAdapter;
import com.application.adimviandroid.models.RecentModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {

    private MainActivity mActivity;
    private int userID;
    private int tabIndex;

    private ImageView imgBack;
    private TextView txtTitle, txtNoData;
    private RecyclerView rclRecent;
    private ShimmerFrameLayout shimer;

    private List<RecentModel> recents = new ArrayList<>();
    private RecentAdapter mAdapter;

    public RecentFragment() {
        // Required empty public constructor
    }

    public RecentFragment(MainActivity mainActivity, int userID, int tabIndex) {
        this.mActivity = mainActivity;
        this.userID = userID;
        this.tabIndex = tabIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        txtNoData = view.findViewById(R.id.txtNoData);
        shimer = view.findViewById(R.id.shimer);
        rclRecent = view.findViewById(R.id.rclRecent);
        rclRecent.setLayoutManager(new LinearLayoutManager(mActivity));
        rclRecent.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RecentAdapter(mActivity, recents, recent -> {
            if (recent.type.equals("Wall") || recent.type.equals("Favorite") || recent.type.equals("Re_Wall")) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, recent.userID, null, null, false), tabIndex);
            } else {
                mActivity.addFragment(new PostDetailFragment(mActivity, recent.postID, tabIndex), tabIndex);
            }
        });
        rclRecent.setAdapter(mAdapter);

        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Actividad reciente");

        initData();
    }

    private void initData() {
        txtNoData.setVisibility(View.GONE);
        rclRecent.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();

        recents.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + userID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_RECENT_ACTIVITY, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray arrData = response.getJSONArray("activity");
                    if (arrData.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclRecent.setVisibility(View.VISIBLE);
                        for (int i = 0; i < arrData.length(); i++) {
                            RecentModel recent = new RecentModel();
                            recent.initWithJSON(arrData.getJSONObject(i));
                            recents.add(recent);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                rclRecent.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                rclRecent.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}