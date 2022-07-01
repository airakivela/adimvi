package com.application.adimviandroid.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.MainFragmentPagerAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.screens.follow.FollowFragment;
import com.application.adimviandroid.screens.home.HomeFragment;
import com.application.adimviandroid.screens.home.NotificationFragment;
import com.application.adimviandroid.screens.home.chatroom.ChatRoomListFragment;
import com.application.adimviandroid.screens.post.PostContentFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.profile.chat.MessageFragment;
import com.application.adimviandroid.screens.publish.PublishFragment;
import com.application.adimviandroid.ui.AppGuideDialog;
import com.application.adimviandroid.ui.NonSwipeableViewPager;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Long firstClick = 1L;
    Long secondClick = 0L;

    private View content;
    private BottomNavigationView bottomNavigationView;
    private NonSwipeableViewPager viewPager;

    private int mCurrentTabPosition;
    public MainFragmentPagerAdapter mPagerAdapter;

    private boolean isPublicationable;

    private AppGuideDialog appGuideDialog;

    private boolean myKeyboardVisible = false;

    public final ViewTreeObserver.OnGlobalLayoutListener mLayoutkeyboardVisibiltyListener = () -> {
        final Rect rectangle = new Rect();
        final View contentView = getContentView();
        contentView.getWindowVisibleDisplayFrame(rectangle);
        int screenHeight = contentView.getRootView().getHeight();
        int keyPadHeight = screenHeight - rectangle.bottom;
        boolean isKeyboardNowVisible = keyPadHeight > screenHeight * 0.15;
        if (myKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                hideShowBottomNavigationBar(false);
            } else {
                if (mPagerAdapter.getItem(mCurrentTabPosition) instanceof PostContentFragment) {
                    hideShowBottomNavigationBar(false);
                } else {
                    hideShowBottomNavigationBar(true);
                }
            }
            myKeyboardVisible = isKeyboardNowVisible;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initUpdateDevice();
        initUIView();
        initUserData();
        initNewPost();
        initAllMembers();

        if (!SharedUtil.getPassGuide()) {
            appGuideDialog = new AppGuideDialog();
            appGuideDialog.show(getSupportFragmentManager(), "theme");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutkeyboardVisibiltyListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutkeyboardVisibiltyListener);
    }

    private void initAllMembers() {
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_ALL_MEMBERS, null, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray resArr = response.getJSONArray("search");
                    for (int i = 0; i < resArr.length(); i++) {
                        MentionUserModel mention = new MentionUserModel();
                        mention.initWithJSON(resArr.getJSONObject(i));
                        AppUtil.mMentionUsers.add(mention);
                    }
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

    private void initUpdateDevice() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("Failed", "getInstanceId failed", task.getException());
                return;
            }

            Map<String, String> param = new HashMap<>();
            param.put("userid", "" + SharedUtil.getSharedUserID());
            param.put("device", "android");
            param.put("fcmID", task.getResult());
            ApiUtil.onAPIConnectionResponse(ApiUtil.UPDATE_FCM_DEVICE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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
        });
    }

    private void initUserData() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", String.valueOf(SharedUtil.getSharedUserID()));
        param.put("login_userid", String.valueOf(SharedUtil.getSharedUserID()));

        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_PROIFLE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONArray response = obj.getJSONArray("response");
                    JSONObject userObject = response.getJSONObject(0);
                    String userAvatar = ApiUtil.ImageUrl + userObject.getString("avatarblobid");
                    SharedUtil.setSharedUserAvatar(userAvatar);
                    String username = userObject.getString("username");
                    SharedUtil.setSharedUserName(username);
                    int userAD = userObject.getInt("fadSense");
                    SharedUtil.setSharedUserAD(userAD);
                    if (!userAvatar.isEmpty()) {
                        Glide.with(getApplicationContext()).asBitmap().optionalCircleCrop().load(userAvatar)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            bottomNavigationView.getMenu().findItem(R.id.menu_profile).setIconTintList(null);
                                            bottomNavigationView.getMenu().findItem(R.id.menu_profile).setIconTintMode(null);
                                        }
                                        else {
                                            Drawable drawable = bottomNavigationView.getMenu().findItem(R.id.menu_profile).getIcon();
                                            drawable.mutate();
                                            drawable.setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
                                            drawable.setAlpha(0);
                                        }
                                        Drawable profileImage = new BitmapDrawable(getResources(), resource);
                                        profileImage.mutate();
                                        profileImage.setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
                                        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setIcon(profileImage);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
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

    public void initNotificationBadge(int badgeCnt) {
        if (badgeCnt != 0) {
            bottomNavigationView.getOrCreateBadge(R.id.menu_notification).setNumber(badgeCnt);
            bottomNavigationView.getOrCreateBadge(R.id.menu_notification).setVisible(true);
        } else {
            bottomNavigationView.getOrCreateBadge(R.id.menu_notification).setVisible(false);
        }
    }

    private void initNewPost() {
        boolean hasNewPost = SharedUtil.getSharedHasNewPost();
        bottomNavigationView.getOrCreateBadge(R.id.menu_follow).setVisible(hasNewPost);
    }

    public void hideShowBottomNavigationBar(boolean value) {
        bottomNavigationView.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void initUIView() {
        content = findViewById(R.id.content);
        mPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.contentFrame);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setMyScroll();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    mCurrentTabPosition = HomeFragment.TAB_POSITION;
                    viewPager.setCurrentItem(mCurrentTabPosition);
                    break;
                case R.id.menu_follow:
                    SharedUtil.setSharedHasNewPost(false);
                    bottomNavigationView.getOrCreateBadge(R.id.menu_follow).setVisible(false);
                    mCurrentTabPosition = FollowFragment.TAB_POSITION;
                    viewPager.setCurrentItem(mCurrentTabPosition);
                    break;
                case R.id.menu_post:
                    mCurrentTabPosition = PublishFragment.TAB_POSITION;
                    viewPager.setCurrentItem(mCurrentTabPosition);
                    break;
                case R.id.menu_notification:
                    mCurrentTabPosition = NotificationFragment.TAB_POSITION;
                    viewPager.setCurrentItem(mCurrentTabPosition);
                    break;
                case R.id.menu_profile:
                    mCurrentTabPosition = ProfileFragment.TAB_POSITION;
                    viewPager.setCurrentItem(mCurrentTabPosition);
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        if (getIntent().hasExtra("PUSH_NOTIFICATION")) {
            if (getIntent().hasExtra("SALE_NOTIFICATION")) {
//                bottomNavigationView.setSelectedItemId(R.id.menu_sale);
//                bottomNavigationView.getOrCreateBadge(R.id.menu_sale).setVisible(false);
            } else if (getIntent().hasExtra("SIGUIENDO_NOTIFICATION")) {
                bottomNavigationView.setSelectedItemId(R.id.menu_follow);
                bottomNavigationView.getOrCreateBadge(R.id.menu_follow).setVisible(false);
            } else if (getIntent().hasExtra("FOLLOW_CHAT_ROOM")) {
                addFragment(new ChatRoomListFragment(MainActivity.this, 0), 0);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.menu_notification);
            }
            getIntent().removeExtra("PUSH_NOTIFICATION");
            getIntent().removeExtra("SALE_NOTIFICATION");
            getIntent().removeExtra("SIGUIENDO_NOTIFICATION");
            getIntent().removeExtra("FOLLOW_CHAT_ROOM");
        }
    }

    public View getContentView() {
        return content;
    }

    public void setSelectedBootomNavigationView(int index) {
        bottomNavigationView.setSelectedItemId(index);
    }

    public void addFragment(Fragment fragment, int tabPosition) {
        mPagerAdapter.updateFragment(fragment, tabPosition);
    }

    public void onBackPressed() {
        if (!mPagerAdapter.removeFragment(mPagerAdapter.getItem(mCurrentTabPosition), mCurrentTabPosition)) {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        secondClick = System.currentTimeMillis();
        if ((secondClick - firstClick) / 1000 < 2) {
            super.onBackPressed();
        } else {
            firstClick = System.currentTimeMillis();
            BannerUtil.onShowWaringAlert(content, getString(R.string.appExit), 1500);
        }
    }

    public void setPublication(boolean isPublicationable) {
        this.isPublicationable = isPublicationable;
    }

    public boolean getPublication() {
        return isPublicationable;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mPagerAdapter.getItem(mCurrentTabPosition) instanceof MessageFragment && !AppUtil.isCameraOn) {
            ChatModel targetUser = ((MessageFragment) mPagerAdapter.getItem(mCurrentTabPosition)).getChatModel();
            FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
            FireChatUtil.removePrivateMessageChanelIsON(targetUser.userID);
        }
    }
}