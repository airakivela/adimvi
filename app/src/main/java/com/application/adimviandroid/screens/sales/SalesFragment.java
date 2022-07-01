package com.application.adimviandroid.screens.sales;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.SalesAdapter;
import com.application.adimviandroid.models.BuyPostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesFragment extends Fragment {

//    public static final int TAB_POSITION = 3;

    private MainActivity mActivity;
    private String title;
    private int tabIndex;

    private TextView txtTitle, txtNoData;
    private ImageView imgBack;
    private RecyclerView rclSale;

    private SalesAdapter adapter;
    private List<BuyPostModel> mBuyPosts = new ArrayList<>();

    public SalesFragment() {

    }

    public SalesFragment(MainActivity mainActivity, String title, int tabIndex) {
        this.mActivity = mainActivity;
        this.title = title;
        this.tabIndex = tabIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        txtNoData = view.findViewById(R.id.txtNoData);
        rclSale = view.findViewById(R.id.rcl_sales);
        rclSale.setItemAnimator(new DefaultItemAnimator());
        rclSale.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new SalesAdapter(mActivity, mBuyPosts);
        rclSale.setAdapter(adapter);

        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    private void initData() {
        mBuyPosts.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.BUY_POST_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray buyPost = response.getJSONArray("buyPost");
                    if (buyPost.length() == 0) {
                        rclSale.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclSale.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < buyPost.length(); i++) {
                            JSONObject item = buyPost.getJSONObject(i);
                            BuyPostModel model = new BuyPostModel();
                            model.initWithJSON(item);
                            mBuyPosts.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    rclSale.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                rclSale.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                rclSale.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}