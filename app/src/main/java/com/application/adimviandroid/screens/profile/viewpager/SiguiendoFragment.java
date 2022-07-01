package com.application.adimviandroid.screens.profile.viewpager;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.FollowAdapter;
import com.application.adimviandroid.models.FollowModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiguiendoFragment extends Fragment {

    private MainActivity mActivity;
    private int selectedUserID;
    private int tabIndex;

    private RecyclerView rclFollow;
    private TextView txtNoData;
    private ShimmerFrameLayout shimer;

    private List<FollowModel> mFollows = new ArrayList<>();
    private FollowAdapter mAdapter;

    public SiguiendoFragment() {
        // Required empty public constructor
    }

    public SiguiendoFragment(MainActivity mActivity, int selectedUserID, int tabIndex) {
        this.mActivity = mActivity;
        this.selectedUserID = selectedUserID;
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
        View view = inflater.inflate(R.layout.fragment_siguiendo, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        rclFollow = view.findViewById(R.id.rclFollow);
        rclFollow.setLayoutManager(new LinearLayoutManager(mActivity));
        rclFollow.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FollowAdapter(mActivity, mFollows, new FollowAdapter.FollowAdpaterListener() {
            @Override
            public void onClickUserAvatar(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
            }

            @Override
            public void onClickFollow(int userID) {
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                Map<String, String> parma = new HashMap<>();
                parma.put("userid", "" + SharedUtil.getSharedUserID());
                parma.put("entityid", "" + userID);
                ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onEventInternetError(Exception e) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onEventServerError(Exception e) {
                        dialog.dismiss();
                    }
                });
            }
        });
        rclFollow.setAdapter(mAdapter);
        txtNoData = view.findViewById(R.id.txtNoData);
    }

    private void initData() {
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        rclFollow.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        mFollows.clear();

        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + selectedUserID);
        param.put("login_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_FOLLOWING_NEW, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.stopShimmer();
                    shimer.setVisibility(View.GONE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray posts = response.getJSONArray("userFollowing");
                    if (posts.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        rclFollow.setVisibility(View.GONE);
                    } else {
                        rclFollow.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < posts.length(); i++) {
                            FollowModel follow = new FollowModel();
                            follow.initWithJSON(posts.getJSONObject(i));
                            mFollows.add(follow);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    txtNoData.setVisibility(View.VISIBLE);
                    rclFollow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclFollow.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                rclFollow.setVisibility(View.GONE);
            }
        });
    }
}