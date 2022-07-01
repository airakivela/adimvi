package com.application.adimviandroid.screens.home;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.CategroyAdapter;
import com.application.adimviandroid.adapter.ExploreAdapter;
import com.application.adimviandroid.adapter.FeatureAdapter;
import com.application.adimviandroid.adapter.HomeSearchAdapter;
import com.application.adimviandroid.adapter.RecentWallUserAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.FeaturModel;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.screens.home.chatroom.ChatRoomListFragment;
import com.application.adimviandroid.screens.post.PostListByTagFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.HomeExploreSegmentType;
import com.application.adimviandroid.types.HomeSearchType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.MostActivePostListFragment;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByCategoryFragment;
import com.application.adimviandroid.ui.BadgeDrawable;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public static final int TAB_POSITION = 0;

    private MainActivity mActivity;
    private Toolbar toolbarHome;
    private EditText edtSearch;
    private ImageView imgUserAvatar, imgUserVerify, imgRoom;
    private TextView txtUserName, txtPost, txtUser, txtTag;
    private RecyclerView rclFeature, rclCategory, rclSearch, rclRecent;
    private ViewPager vpExplor;
    private DotsIndicator exploreIndicator;
    private TagContainerLayout tagView;
    private NestedScrollView nstContainer;
    private LinearLayout lltFilter, lltRecent;
    private ShimmerFrameLayout shimer;

    private final List<FeaturModel> features = new ArrayList<>();
    private final List<JSONObject> featureJSONS = new ArrayList<>();
    private FeatureAdapter featureAdapter;
    private ExploreAdapter exploreAdapter;
    private final List<CategoryModel> categories = new ArrayList<>();
    private CategroyAdapter categroyAdapter;
    private final List<TagModel> favouriteTags = new ArrayList<>();
    private final List<String> tagTitleArr = new ArrayList<>();
    private final List<JSONObject> filterArr = new ArrayList<>();
    private HomeSearchAdapter homeSearchAdapter;
    private final List<MuroModel> recentWallUserArr = new ArrayList<>();
    private RecentWallUserAdapter recentWallUserAdapter ;

    private SwipeRefreshLayout swpHome;

    private AlertDialog dialog;

    public HomeFragment() {

    }

    public HomeFragment(MainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        initUIView(view);
        initBadge();
        initData();
        initSearchTye();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void initBadge() {
        ///init category, feature and badge///
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("offset", "100");
        param.put("limit", "0");
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_CATEGORY_NEW, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    int totalNotify = obj.getInt("totalNotify");
                    //initNotificaitonBadge(totalNotify); //// setBadge////
                    mActivity.initNotificationBadge(totalNotify);
                    /// set featured post part////
                    features.clear();
                    featureJSONS.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray featuredArr = response.getJSONArray("featured");
                    for (int i = 0; i < featuredArr.length(); i++) {
                        FeaturModel feature = new FeaturModel();
                        feature.initWithJSON(featuredArr.getJSONObject(i));
                        features.add(feature);
                        featureJSONS.add(featuredArr.getJSONObject(i));
                    }
                    featureAdapter.notifyDataSetChanged();
                    categories.clear();
                    AppUtil.gCategories .clear();
                    JSONArray categoryArr = response.getJSONArray("postinfo");
                    for (int i = 0 ; i < categoryArr.length(); i++) {
                        CategoryModel category = new CategoryModel();
                        category.initWithJSON(categoryArr.getJSONObject(i));
                        categories.add(category);
                    }
                    AppUtil.gCategories.addAll(categories);
                    categroyAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.INTERNET_ERROR, AppConstant.SHOW_BANNER_TIME);
            }

            @Override
            public void onEventServerError(Exception e) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.SERVER_ERROR, AppConstant.SHOW_BANNER_TIME);
            }
        });
    }

    private void initData() {
        ///init user data///
        nstContainer.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        Map<String, String> paramUser = new HashMap<>();
        paramUser.put("userid", "" + SharedUtil.getSharedUserID());
        paramUser.put("login_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_PROIFLE, paramUser, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    nstContainer.setVisibility(View.VISIBLE);
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    swpHome.setRefreshing(false);
                    JSONArray response = obj.getJSONArray("response");
                    JSONObject userObject = response.getJSONObject(0);
                    int isVerified = userObject.getInt("isVerify");
                    imgUserVerify.setVisibility(isVerified == 1 ? View.VISIBLE : View.GONE);
                    String userName = userObject.getString("username");
                    txtUserName.setText("Hola " + userName);
                    if (userObject.getString("avatarblobid").trim().isEmpty()) {
                        imgUserAvatar.setImageResource(R.drawable.ic_user_placehoder);
                    } else {
                        String userAvatar = ApiUtil.ImageUrl + userObject.getString("avatarblobid");
                        AppUtil.loadImageByUrl(mActivity, imgUserAvatar, userAvatar, ImagePlaceHolderType.USERIMAGE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    nstContainer.setVisibility(View.VISIBLE);
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    swpHome.setRefreshing(false);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                nstContainer.setVisibility(View.VISIBLE);
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                swpHome.setRefreshing(false);
            }

            @Override
            public void onEventServerError(Exception e) {
                nstContainer.setVisibility(View.VISIBLE);
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                swpHome.setRefreshing(false);
            }
        });

        ///favourite tag data///
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_POPULAR_TAG, null, ApiUtil.APIMethod.GET, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    favouriteTags.clear();
                    tagTitleArr.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray tagArr = response.getJSONArray("tags");
                    for (int i = 0; i < tagArr.length(); i++) {
                        TagModel model = new TagModel();
                        model.initWithJSON(tagArr.getJSONObject(i));
                        favouriteTags.add(model);
                        tagTitleArr.add(model.tagTitle);
                    }
                    tagView.setTags(tagTitleArr);
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

        ///recent wall post user data///
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_RECENT_WALL_USER, paramUser, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                recentWallUserArr.clear();
                try {
                    int code = obj.getInt("code");
                    if (code != 200) {
                        lltRecent.setVisibility(View.GONE);
                    } else {
                        JSONObject response = obj.getJSONObject("response");
                        JSONArray recentWallUserList = response.getJSONArray("wallFollowPost");
                        JSONArray paidWallUserList = response.getJSONArray("paidWall");
                        if (recentWallUserList.length() == 0 && paidWallUserList.length() == 0) {
                            lltRecent.setVisibility(View.GONE);
                        } else {
                            lltRecent.setVisibility(View.VISIBLE);
                            for (int i = 0; i < paidWallUserList.length(); i++) {
                                JSONObject jsonObject = paidWallUserList.getJSONObject(i);
                                MuroModel model = new MuroModel();
                                model.initWithJSON(jsonObject);
                                recentWallUserArr.add(model);
                            }
                            for (int i = 0; i < recentWallUserList.length(); i++) {
                                JSONObject jsonObject = recentWallUserList.getJSONObject(i);
                                MuroModel model = new MuroModel();
                                model.initWithJSON(jsonObject);
                                recentWallUserArr.add(model);
                            }
                            recentWallUserAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                lltRecent.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                lltRecent.setVisibility(View.GONE);
            }
        });
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        toolbarHome = view.findViewById(R.id.toolbar_home);
        toolbarHome.inflateMenu(R.menu.home_menu);
        toolbarHome.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_chat:
                    mActivity.addFragment(new ChatRoomListFragment(mActivity, TAB_POSITION), TAB_POSITION);
                    break;
//                case R.id.menu_bell:
//                    mActivity.addFragment(new NotificationFragment(mActivity, TAB_POSITION), TAB_POSITION);
//                    break;
                case R.id.menu_search:
                    mActivity.addFragment(new SearchFragment(mActivity, TAB_POSITION), TAB_POSITION);
                    break;
            }
            return true;
        });

        imgUserAvatar = view.findViewById(R.id.img_user);
        imgUserAvatar.setOnClickListener(v -> mActivity.setSelectedBootomNavigationView(R.id.menu_profile));
        imgUserVerify = view.findViewById(R.id.img_verified);
        txtUserName = view.findViewById(R.id.txt_username);

        rclFeature = view.findViewById(R.id.rcl_featur);
        featureAdapter = new FeatureAdapter(mActivity, features, index -> {
            mActivity.addFragment(new PostDetailFragment(mActivity, index, TAB_POSITION), TAB_POSITION);
//            mActivity.addFragment(new SwipePostDetailFragment(mActivity, featureJSONS, TAB_POSITION, index), TAB_POSITION);
        });
        rclFeature.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rclFeature.setAdapter(featureAdapter);

        lltRecent = view.findViewById(R.id.llt_recent);
        rclRecent = view.findViewById(R.id.rcl_recent);
        rclRecent.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        recentWallUserAdapter = new RecentWallUserAdapter(mActivity, recentWallUserArr, (position) -> {
            AppUtil.gRecentWallUsers = recentWallUserArr.subList(position , recentWallUserArr.size());
            AppUtil.gVisitedRecentWallUsers.clear();
            for (int i = 0; i < AppUtil.gRecentWallUsers.size(); i++) {
                AppUtil.gVisitedRecentWallUsers.add(false);
            }
            mActivity.addFragment(new RecentWallUserFragment(mActivity, TAB_POSITION), TAB_POSITION);
        });
        rclRecent.setAdapter(recentWallUserAdapter);

        vpExplor = view.findViewById(R.id.vpExplore);
        exploreAdapter = new ExploreAdapter(mActivity, type -> {
            AppUtil.homeExploreSegmentType = HomeExploreSegmentType.HOY;
            mActivity.addFragment(new MostActivePostListFragment(mActivity, TAB_POSITION, type), TAB_POSITION);
        });
        vpExplor.setAdapter(exploreAdapter);

        exploreIndicator = view.findViewById(R.id.indicatorExplore);
        exploreIndicator.setViewPager(vpExplor);

        rclCategory = view.findViewById(R.id.rcl_category);
        rclCategory.setLayoutManager(new GridLayoutManager(mActivity, 2));
        categroyAdapter = new CategroyAdapter(mActivity, categories, categoryModel -> {
            mActivity.addFragment(new PostListByCategoryFragment(mActivity, categoryModel, TAB_POSITION), TAB_POSITION);
        });
        rclCategory.setAdapter(categroyAdapter);
        tagView = view.findViewById(R.id.tagFavourite);
        tagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                mActivity.addFragment(new PostListByTagFragment(mActivity, favouriteTags.get(position), TAB_POSITION), TAB_POSITION);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

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
        rclSearch = view.findViewById(R.id.rcl_search);
        rclSearch.setLayoutManager(new LinearLayoutManager(mActivity));
        homeSearchAdapter = new HomeSearchAdapter(mActivity, filterArr, object -> {
            edtSearch.setText("");
            try {
                if (AppUtil.homeSearchType == HomeSearchType.POST) {
                    mActivity.addFragment(new PostDetailFragment(mActivity, object.getInt("id"), TAB_POSITION), TAB_POSITION);
                } else if (AppUtil.homeSearchType == HomeSearchType.USERNAME) {
                    mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, object.getInt("id"), null, null, false), TAB_POSITION);
                } else {
                    TagModel tag = new TagModel();
                    tag.tagID = object.getInt("id");
                    tag.tagTitle = object.getString("title");
                    mActivity.addFragment(new PostListByTagFragment(mActivity, tag, TAB_POSITION), TAB_POSITION);
                }
            } catch (JSONException e) {

            }
        });
        rclSearch.setAdapter(homeSearchAdapter);
        lltFilter = view.findViewById(R.id.llt_filter);
        nstContainer = view.findViewById(R.id.nsContainer);
        nstContainer.setOnTouchListener((v, event) -> edtSearch.getText().toString().length() == 0 ? false : true);
        txtUser = view.findViewById(R.id.txtUser);
        txtTag = view.findViewById(R.id.txtTag);
        txtPost = view.findViewById(R.id.txtPost);
        txtUser.setOnClickListener(this);
        txtTag.setOnClickListener(this);
        txtPost.setOnClickListener(this);

        swpHome = view.findViewById(R.id.swpHome);
        swpHome.setOnRefreshListener(() -> initData());

        imgRoom = view.findViewById(R.id.imgRoom);
        imgRoom.setOnClickListener(this);
        Glide.with(mActivity).load(R.drawable.ic_room).into(imgRoom);
        initChatRoom();
        initBadge();
    }

    private void initChatRoom() {
        FireChatUtil.mFireDBRoomIDS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    Glide.with(mActivity).load(R.drawable.ic_room).into(imgRoom);
                } else {
                    Glide.with(mActivity).load(R.drawable.gif_room).into(imgRoom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void initNotificaitonBadge(int badgeCnt) {
//        MenuItem menuItem = toolbarHome.getMenu().findItem(R.id.menu_bell);
//        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
//        BadgeDrawable badgeDrawable;
//        Drawable reuse = icon.findDrawableByLayerId(R.id.badge_count);
//        if (reuse != null && reuse instanceof BadgeDrawable) {
//            badgeDrawable = (BadgeDrawable) reuse;
//        } else {
//            badgeDrawable = new BadgeDrawable(mActivity);
//        }
//        badgeDrawable.setCount(String.valueOf(badgeCnt));
//        icon.mutate();
//        icon.setDrawableByLayerId(R.id.badge_count, badgeDrawable);
//    }

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
            case R.id.imgRoom:
                mActivity.addFragment(new ChatRoomListFragment(mActivity, TAB_POSITION), TAB_POSITION);
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