package com.application.adimviandroid.screens.profile;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.ProfilePageAdapter;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.chat.MessageListFragment;
import com.application.adimviandroid.screens.profile.note.NoteListFragment;
import com.application.adimviandroid.screens.profile.viewpager.MuroFragment;
import com.application.adimviandroid.screens.profile.viewpager.PerfilFragment;
import com.application.adimviandroid.screens.profile.viewpager.PostFragment;
import com.application.adimviandroid.screens.profile.viewpager.SeguidoresFragment;
import com.application.adimviandroid.screens.profile.viewpager.SiguiendoFragment;
import com.application.adimviandroid.ui.NotificationBlockDialog;
import com.application.adimviandroid.ui.ProfileHeaderView;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    public static final int TAB_POSITION = 4;

    private MainActivity mActivity;
    private int tabIndex;
    private boolean hasNewSale;
    private int selectedUserID;
    private RewallModel reWall;
    private RepostModel rePost;
    private boolean isShowMuro;

    private ImageView imgMenu, imgBack, imgWallet, imgMore, imgVerify, imgBell;
    private TabLayout tabBar;
    private ViewPager viewPager;
    private TextView txtTitle, txtWallet;
    private ConstraintLayout csWallet;
    private CardView crdWallet;
    private ProfileHeaderView headerView;

    private ProfilePageAdapter mPageAdpater;
    private List<Fragment> mFragmanets;

    private NotificationBlockDialog blockDialog;
    private int isPostBlock;
    private int isMuroBlock;

    public ProfileFragment() {

    }

    public ProfileFragment(MainActivity mainActivity, int tabIndex, int userID, RewallModel rewall, RepostModel repost, boolean isShowMuro) {
        this.mActivity = mainActivity;
        this.tabIndex = tabIndex;
        this.selectedUserID = userID;
        this.reWall = rewall;
        this.rePost = repost;
        this.isShowMuro = isShowMuro;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (selectedUserID == 0) {
            selectedUserID = SharedUtil.getSharedUserID();
            tabIndex = TAB_POSITION;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (blockDialog != null && blockDialog.isShowing()) {
            blockDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUIView(view);
        initUpadateViewCount();
        initUserSale();
        initBlockState();
        return view;
    }

    private void initUpadateViewCount() {
        if (selectedUserID == SharedUtil.getSharedUserID()) {
            return;
        }
        //// rewall count must increase by selected user id ////
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + selectedUserID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.UPDATE_USER_REWALL_COUNT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {

            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }

    private  void initBlockState() {
        Map<String, String> parma = new HashMap<>();
        parma.put("userid1", "" + SharedUtil.getSharedUserID());
        parma.put("userid2", "" + selectedUserID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_NOTIFICATION_BLOCK, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    isMuroBlock = obj.getInt("isMuroBlock");
                    isPostBlock = obj.getInt("isPostBlock");
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

    private void initUserSale() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", String.valueOf(SharedUtil.getSharedUserID()));

        ApiUtil.onAPIConnectionResponse(ApiUtil.SALES_NOTIFY, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONObject("response");
                    int totalNotify = response.getInt("total_notify");
                    if (totalNotify != 0) {
                        crdWallet.setVisibility(View.VISIBLE);
                        txtWallet.setText("" + totalNotify);
                        hasNewSale = true;
                    } else {
                        crdWallet.setVisibility(View.GONE);
                        hasNewSale = false;
                    }
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

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setVisibility((TAB_POSITION == tabIndex && SharedUtil.getSharedUserID() == selectedUserID) ? View.GONE : View.VISIBLE);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        imgMenu = view.findViewById(R.id.imgMenu);
        imgMenu.setVisibility(SharedUtil.getSharedUserID() == selectedUserID ? View.VISIBLE : View.GONE);
        imgMenu.setOnClickListener(v -> {
            mActivity.addFragment(new MenuFragment(mActivity, tabIndex), tabIndex);
        });
        txtTitle = view.findViewById(R.id.txtTitle);
        imgWallet = view.findViewById(R.id.imgWallet);
        imgBell = view.findViewById(R.id.imgNotificationBlock);
        imgBell.setVisibility(SharedUtil.getSharedUserID() == selectedUserID ? View.GONE : View.VISIBLE);
        imgBell.setOnClickListener(v -> {
            blockDialog = new NotificationBlockDialog(mActivity, isPostBlock, isMuroBlock, (isPostBlocked, isMuroBlocked) -> {
                onCallNotificationBlock(isPostBlocked, isMuroBlocked);
            });
            blockDialog.show();
        });
        csWallet = view.findViewById(R.id.csWallet);
        crdWallet = view.findViewById(R.id.crdWallet);
        txtWallet = view.findViewById(R.id.txtVentas);
        csWallet.setVisibility(SharedUtil.getSharedUserID() == selectedUserID ? View.VISIBLE : View.GONE);
        csWallet.setOnClickListener(v -> {
            mActivity.addFragment(new WalletFragment(mActivity, hasNewSale, tabIndex), tabIndex);
        });
        imgMore = view.findViewById(R.id.imgMore);
        imgMore.setVisibility(SharedUtil.getSharedUserID() == selectedUserID ? View.VISIBLE : View.GONE);
        imgMore.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(mActivity, imgMore);
            for (int i = 0; i < AppConstant.MENU_MORE.size(); i++) {
                String str = AppConstant.MENU_MORE.get(i);
                if (i == AppConstant.MENU_MORE.size() - 1) {
                    SpannableString item = new SpannableString(str);
                    item.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_dark)), 0, item.length(), 0);
                    menu.getMenu().add(Menu.NONE, i, 1, item);
                } else {
                    menu.getMenu().add(str);
                }
            }
            menu.show();
            menu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals(AppConstant.MENU_MORE.get(0))) {
                    mActivity.addFragment(new AccountFragment(mActivity), tabIndex);
                } else if (item.getTitle().equals(AppConstant.MENU_MORE.get(1))) {
                    mActivity.addFragment(new MessageListFragment(mActivity), tabIndex);
                } else if (item.getTitle().equals(AppConstant.MENU_MORE.get(2))) {
                    mActivity.addFragment(new MyFavoriteFragment(mActivity), tabIndex);
                } else if (item.getTitle().equals(AppConstant.MENU_MORE.get(3))) {
                    mActivity.addFragment(new NoteListFragment(mActivity), tabIndex);
                } else {
                    AppUtil.logoutUser(mActivity);
                }
                return true;
            });
        });
        imgVerify = view.findViewById(R.id.imgVerify);
        tabBar = view.findViewById(R.id.tabBar);
        mFragmanets = new ArrayList<>(
                Arrays.asList(new PerfilFragment(mActivity, selectedUserID, tabIndex),
                        new MuroFragment(mActivity, selectedUserID, rePost, reWall, tabIndex),
                        new PostFragment(mActivity, selectedUserID, tabIndex, listener),
                        new SeguidoresFragment(mActivity, selectedUserID, tabIndex),
                        new SiguiendoFragment(mActivity, selectedUserID, tabIndex))
        );
        mPageAdpater = new ProfilePageAdapter(mActivity, getChildFragmentManager(), mFragmanets);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(mPageAdpater);
        viewPager.setOffscreenPageLimit(1);
        tabBar.setupWithViewPager(viewPager);
        for (int i = 0; i < AppConstant.PROFILETABS.size(); i++) {
            tabBar.getTabAt(i).setText(AppConstant.PROFILETABS.get(i));
        }
        if (reWall != null || rePost != null || isShowMuro) {
            viewPager.setCurrentItem(1);
        }

        headerView = view.findViewById(R.id.profileHeader);
        headerView.initHeader(selectedUserID, new ProfileHeaderView.HeaderViewListener() {
            @Override
            public void onClickSetting(int userid) {
                mActivity.addFragment(new AccountFragment(mActivity), tabIndex);
            }

            @Override
            public void onSetUserName(String name, int verify) {
                imgVerify.setVisibility(verify == 1 ? View.VISIBLE : View.GONE);
                setUserName(name);
            }
        });
    }

    private void onCallNotificationBlock(boolean isPostBlock, boolean isMuroBlock) {
        Map<String, String> params = new HashMap<>();
        params.put("userid1", "" + SharedUtil.getSharedUserID());
        params.put("userid2", "" + selectedUserID);
        params.put("isPostBlock", "" + (isPostBlock ? 1 : 0));
        params.put("isMuroBlock", "" + (isMuroBlock ? 1 : 0));
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_NOTIFICATION_BLOCK, params, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                initBlockState();
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }

    private PostFragment.PostFragmentListener listener = new PostFragment.PostFragmentListener() {
        @Override
        public void onClickRemuroButton(RepostModel repostModel) {
            mFragmanets.remove(1);
            mFragmanets.set(1, new MuroFragment(mActivity, selectedUserID, repostModel, null, tabIndex));
            mPageAdpater.notifyDataSetChanged();
            viewPager.setCurrentItem(1);
        }
    };

    private void setUserName(String name) {
        txtTitle.setText(name);
    }

}