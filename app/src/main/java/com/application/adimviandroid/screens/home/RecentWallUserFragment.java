package com.application.adimviandroid.screens.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.RecentWallAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.follow.WallCommentFragment;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByTagFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.profile.chat.MessageFragment;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.SwipeTouchListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecentWallUserFragment extends Fragment {

    private int selectedTab;
    private MainActivity mActivity;
    private ImageDialog imageDialog;

    private ImageView imgClose;
    private RecyclerView rclRecentWall;
    private RelativeLayout rclContent;

    private RecentWallAdapter recentWallAdapter;

    public RecentWallUserFragment() {
        // Required empty public constructor
    }

    public RecentWallUserFragment(MainActivity mainActivity, int selectedTab) {
        this.mActivity = mainActivity;
        this.selectedTab = selectedTab;
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
        if (imageDialog != null && imageDialog.isShowing()) {
            imageDialog.dismiss();
        }
        mActivity.hideShowBottomNavigationBar(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.hideShowBottomNavigationBar(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_wall, container, false);
        initUIView(view);
        mActivity.hideShowBottomNavigationBar(true);
        return view;
    }

    private void initUIView(View view) {
        imgClose = view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(v -> mActivity.onBackPressed());

        rclContent = view.findViewById(R.id.rclContent);

        rclRecentWall = view.findViewById(R.id.rclRecentWall);
        rclRecentWall.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rclRecentWall.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclRecentWall);
        recentWallAdapter = new RecentWallAdapter(mActivity, new RecentWallAdapter.RecentWallListener() {
            @Override
            public void onClickUserAvatar(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, selectedTab, userID, null, null, false), selectedTab);
            }

            @Override
            public void onClickPostImage(Bitmap bitmap) {
                imageDialog = new ImageDialog(mActivity, bitmap);
                imageDialog.show();
            }

            @Override
            public void onClickOriginWallImage(Bitmap bitmap) {
                imageDialog = new ImageDialog(mActivity, bitmap);
                imageDialog.show();
            }

            @Override
            public void onClickOriginPost(int postID) {
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, selectedTab), selectedTab);
            }

            @Override
            public void onClickComment(int postID, int fromUserID) {
                mActivity.addFragment(new WallCommentFragment(mActivity, postID, fromUserID, selectedTab), selectedTab);
            }

            @Override
            public void onClickLike(int position) {
                onCallSetFavorite(position);
            }

            @Override
            public void onClickRemuro(MuroModel model) {
                RewallModel rewall = new RewallModel();
                rewall.id = model.messageID;
                rewall.userAvatar = model.userAvatar;
                rewall.imageUrl = model.imageUrl;
                rewall.created = model.created;
                rewall.verify = model.verify;
                rewall.username = model.username;
                rewall.content = model.content;
                mActivity.addFragment(new ProfileFragment(mActivity, selectedTab, SharedUtil.getSharedUserID(), rewall, null, false), selectedTab);
            }

            @Override
            public void onClickTag(TagModel tagModel) {
                mActivity.addFragment(new PostListByTagFragment(mActivity, tagModel, selectedTab), selectedTab);
            }

            @Override
            public void onClickFollow(int position) {
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                Map<String, String> parma = new HashMap<>();
                parma.put("userid", "" + SharedUtil.getSharedUserID());
                parma.put("entityid", "" + AppUtil.gRecentWallUsers.get(position).fromuserID);
                ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        dialog.dismiss();
                        MuroModel model = AppUtil.gRecentWallUsers.get(position);
                        if (model.followStatus == 0) {
                            model.followStatus = 1;
                        } else {
                            model.followStatus = 0;
                        }
                        recentWallAdapter.notifyDataSetChanged();
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

            @Override
            public void onClickMessage(ChatModel model) {
                mActivity.addFragment(new MessageFragment(mActivity, model, selectedTab), selectedTab);
            }

            @Override
            public void onClickOriginWallRemuro(RewallModel rewall) {
                mActivity.addFragment(new ProfileFragment(mActivity, selectedTab, SharedUtil.getSharedUserID(), rewall, null, false), selectedTab);
            }
        });
        rclRecentWall.addOnItemTouchListener(new SwipeTouchListener(mActivity, rclRecentWall, new SwipeTouchListener.OnTouchActionListener() {
            @Override
            public void onLeftSwipe(View view, int position) {
                if ((position == AppUtil.gVisitedRecentWallUsers.size() - 1) && !rclRecentWall.canScrollHorizontally(1)) {
                    ObjectAnimator animY = ObjectAnimator.ofFloat(rclContent, View.SCALE_Y, 1f, 0.3f);
                    ObjectAnimator animX = ObjectAnimator.ofFloat(rclContent, View.SCALE_X, 1f, 0.3f);
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.playTogether(animX, animY);
                    animSet.setDuration(500);
                    animSet.setStartDelay(0);
                    animSet.setInterpolator(null);
                    animSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mActivity.onBackPressed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animSet.start();
                }
            }

            @Override
            public void onRightSwipe(View view, int position) {

            }

            @Override
            public void onClick(View view, int position) {

            }
        }));
        rclRecentWall.setAdapter(recentWallAdapter);
    }

    private void onCallSetFavorite(int position) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + AppUtil.gRecentWallUsers.get(position).messageID);
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("login_userId", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_WALL_FAVOURITE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                MuroModel model = AppUtil.gRecentWallUsers.get(position);
                if (model.favourite.equals("0")) {
                    model.favourite = "1";
                    model.totalFav = "" + (Integer.valueOf(model.totalFav) + 1);
                } else {
                    model.favourite = "0";
                    model.totalFav = "" + (Integer.valueOf(model.totalFav) - 1);
                }
                recentWallAdapter.notifyDataSetChanged();
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
}