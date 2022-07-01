package com.application.adimviandroid.screens.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.NotificationAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.comment.CommentCommentFragment;
import com.application.adimviandroid.screens.comment.CommentListFragment;
import com.application.adimviandroid.screens.follow.WallCommentFragment;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.profile.chat.MessageFragment;
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

public class NotificationFragment extends Fragment implements View.OnClickListener {

    public static final int TAB_POSITION = 3;

    private MainActivity mActivity;
//    private int tabIndex;

    private TextView txtNoData;
    private ImageView imgBack, imgAllSeen;
    private RecyclerView rclNotification;
    private SwipeRefreshLayout swpRclPost;
    private ShimmerFrameLayout shimer;

    private int unreadMessage = 0;

    private List<JSONObject> notificationArr = new ArrayList<>();
    private NotificationAdapter notificationAdapter;

    private AlertDialog alertDialog;

    public NotificationFragment() {

    }

    public NotificationFragment(MainActivity mainActivity) {
        mActivity = mainActivity;
//        this.tabIndex = tabIndex;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        txtNoData = view.findViewById(R.id.txtNoData);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setVisibility(View.INVISIBLE);
        imgBack.setOnClickListener(this);
        imgAllSeen = view.findViewById(R.id.imgAllSeen);
        imgAllSeen.setOnClickListener(this);
        rclNotification = view.findViewById(R.id.rcl_notification);
        rclNotification.setLayoutManager(new LinearLayoutManager(mActivity));
        rclNotification.setItemAnimator(new DefaultItemAnimator());

        swpRclPost = view.findViewById(R.id.swpRclPost);
        swpRclPost.setOnRefreshListener(() -> {
            notificationArr.clear();
            initData();
        });

        notificationAdapter = new NotificationAdapter(mActivity, notificationArr, new NotificationAdapter.OnClickNotificationCellListener() {
            @Override
            public void onClickNotificationCell(JSONObject object) {
                try {
                    String key = object.getString("filter_date");
                    onCallSeenNotfication(key);
                    openScreenByNotificationType(object);
                    if (object.getInt("readStatus") == 0) {
                        mActivity.initNotificationBadge(unreadMessage--);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClickUserProfile(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, userID, null, null, false), TAB_POSITION);
            }
        });
        rclNotification.setAdapter(notificationAdapter);
    }

    private void initData() {
        rclNotification.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        unreadMessage = 0;
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_NOTIFICATIONS, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                notificationArr.clear();
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                swpRclPost.setRefreshing(false);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray notify = response.getJSONArray("notify");
                    if (notify.length() > 0) {
                        rclNotification.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < notify.length(); i++) {
                            notificationArr.add(notify.getJSONObject(i));
                            int readStatus = 0;
                            try {
                                readStatus = notify.getJSONObject(i).getInt("readStatus");
                            } catch (JSONException e) {
                                readStatus = 0;
                            }
                            if (readStatus == 0) {
                                unreadMessage++;
                            }
                        }
                        notificationAdapter.notifyDataSetChanged();
                        mActivity.initNotificationBadge(unreadMessage);
                    } else {
                        rclNotification.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                swpRclPost.setRefreshing(false);
                rclNotification.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                swpRclPost.setRefreshing(false);
                rclNotification.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                mActivity.onBackPressed();
                break;
            case R.id.imgAllSeen:
                onCallAllSeenNotification();
                break;
        }
    }

    private void onCallAllSeenNotification() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_ALL_SEEN_NOTIFICATION, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                AppUtil.onDismissProgressDialog(dialog);
                initData();
                alertDialog = AppUtil.showNormalDialog(mActivity, "Notificaciones vistas", "Todas tus nuevas notificaciones han sido marcadas como leídas.");
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(dialog);
            }
        });
    }

    private void onCallSeenNotfication(String key) {
        Map<String, String> param = new HashMap<>();
        param.put("key", key);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_SEEN_NOTIFICATION, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    private void openScreenByNotificationType(JSONObject object) {
        try {
            int status = object.getInt("status");
            if (status == 7) {
                ChatModel model = new ChatModel();
                model.userID = object.getInt("userid");
                model.userName = object.getString("username");
                model.imgAvatar = object.getString("avatarblobid").isEmpty() ? "" : ApiUtil.ImageUrl + object.getString("avatarblobid");
                model.verified = object.getInt("verify");
                mActivity.addFragment(new MessageFragment(mActivity, model, TAB_POSITION), TAB_POSITION);
            } else if (status == 4) {
                mActivity.addFragment(new WallCommentFragment(mActivity, object.getInt("postid"), object.getInt("userid"), TAB_POSITION), TAB_POSITION);
            } else if (status == 3) {
                CommentModel originComment = new CommentModel();
                originComment.postID = object.getInt("postid");
                originComment.comment = object.getString("origin_comment");
                originComment.userName = object.getString("origin_comment_user_name");
                originComment.verify = object.getInt("origin_comment_user_verify");
                originComment.created = object.getString("created");
                originComment.categoryID = object.getInt("categoryid");
                originComment.userAvatar = ApiUtil.ImageUrl + object.getString("origin_comment_user_avatar");
                mActivity.addFragment(new CommentCommentFragment(mActivity, originComment, TAB_POSITION), TAB_POSITION);
            } else if (status == 11) {
                mActivity.addFragment(new WallCommentFragment(mActivity, object.getInt("postid"), object.getInt("userid"), TAB_POSITION), TAB_POSITION);
            } else if (status == 1) {
                mActivity.addFragment(new CommentListFragment(mActivity, object.getInt("postid"), TAB_POSITION, 0), TAB_POSITION);
            } else if (status == 10) {
                if (object.getString("type").equals("post_comment_mention")) {
                    if (object.getString("postid1").equals("")) {
                        mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, object.getInt("userid"), null, null, true), TAB_POSITION);
                    } else {
                        mActivity.addFragment(new CommentListFragment(mActivity, object.getInt("postid"), TAB_POSITION, object.getInt("postid1")), TAB_POSITION);
                    }
                } else {
                    mActivity.addFragment(new PostDetailFragment(mActivity, object.getInt("postid"), TAB_POSITION), TAB_POSITION);
                }
            } else if (status == 9) {
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, object.getInt("userid"), null, null, false), TAB_POSITION);
            } else if (status == 8) {
                boolean isProfileMuro = false;
                if (object.getString("type").equals("wall_post")
                        || object.getString("type").equals("wall_post_heart")
                        || object.getString("type").equals("rewall_post")) {
                    isProfileMuro = true;
                }
                mActivity.addFragment(new ProfileFragment(mActivity, TAB_POSITION, object.getInt("userid"), null, null, isProfileMuro), TAB_POSITION);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}