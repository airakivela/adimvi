package com.application.adimviandroid.screens.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.HomeSearchAdapter;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByTagFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.HomeSearchType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements  View.OnClickListener{

    private MainActivity mActivity;
    private int tabIndex;

    private EditText edtSearch;
    private RecyclerView rclSearch;
    private TextView txtPost, txtUser, txtTag,  txtTitle;
    private HomeSearchAdapter homeSearchAdapter;
    private LinearLayout lltFilter;
    private ImageView imgBack;
    private final List<JSONObject> filterArr = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(MainActivity mActivity, int tabIndex) {
        this.mActivity = mActivity;
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        initSearchTye();
        return view;
    }

    private void initView(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Buscador");
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        edtSearch = view.findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    lltFilter.setVisibility(View.VISIBLE);
                    onCallSearchAPI();
                } else {
                    lltFilter.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        homeSearchAdapter = new HomeSearchAdapter(mActivity, filterArr, object -> {
            edtSearch.setText("");
            try {
                if (AppUtil.homeSearchType == HomeSearchType.POST) {
                    mActivity.addFragment(new PostDetailFragment(mActivity, object.getInt("id"), tabIndex), tabIndex);
                } else if (AppUtil.homeSearchType == HomeSearchType.USERNAME) {
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, object.getInt("id"), null, null, false), tabIndex);
                } else {
                    TagModel tag = new TagModel();
                    tag.tagID = object.getInt("id");
                    tag.tagTitle = object.getString("title");
                    mActivity.addFragment(new PostListByTagFragment(mActivity, tag, tabIndex), tabIndex);
                }
            } catch (JSONException e) {

            }
        });
        rclSearch = view.findViewById(R.id.rcl_search);
        rclSearch.setLayoutManager(new LinearLayoutManager(mActivity));
        rclSearch.setAdapter(homeSearchAdapter);
        lltFilter = view.findViewById(R.id.llt_filter);
        rclSearch = view.findViewById(R.id.rcl_search);
        rclSearch.setLayoutManager(new LinearLayoutManager(mActivity));
        txtUser = view.findViewById(R.id.txtUser);
        txtTag = view.findViewById(R.id.txtTag);
        txtPost = view.findViewById(R.id.txtPost);
        txtUser.setOnClickListener(this);
        txtTag.setOnClickListener(this);
        txtPost.setOnClickListener(this);
    }

    private void initSearchTye() {
        switch (AppUtil.homeSearchType) {
            case POST:
                txtPost.setTextColor(mActivity.getResources().getColor(R.color.darkGray));
                txtUser.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                txtTag.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                break;
            case USERNAME:
                txtPost.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                txtUser.setTextColor(mActivity.getResources().getColor(R.color.darkGray));
                txtTag.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                break;
            case TAG:
                txtPost.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                txtUser.setTextColor(mActivity.getResources().getColor(R.color.lightGray));
                txtTag.setTextColor(mActivity.getResources().getColor(R.color.darkGray));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPost:
                AppUtil.homeSearchType = HomeSearchType.POST;
                break;
            case R.id.txtUser:
                AppUtil.homeSearchType = HomeSearchType.USERNAME;
                break;
            case R.id.txtTag:
                AppUtil.homeSearchType = HomeSearchType.TAG;
                break;
        }
        initSearchTye();
        onCallSearchAPI();
    }

    private void onCallSearchAPI() {
        Map<String, String> parma = new HashMap<>();
        parma.put("search", edtSearch.getText().toString());
        parma.put("offset", "100");
        parma.put("limit", "0");
        parma.put("type", AppUtil.homeSearchType.val);
        ApiUtil.onAPIConnectionResponse(ApiUtil.HOME_SEARCH_NEW, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    filterArr.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray searchArr = response.getJSONArray("search");
                    for (int i = 0; i < searchArr.length(); i++) {
                        filterArr.add(searchArr.getJSONObject(i));
                    }
                    homeSearchAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }
}