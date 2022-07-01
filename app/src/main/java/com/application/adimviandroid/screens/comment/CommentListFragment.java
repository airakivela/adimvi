package com.application.adimviandroid.screens.comment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.CommentAdapter;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.CommentCellType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.PromptDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentListFragment extends Fragment {

    private MainActivity mActivity;
    private int postID, tabIndex, seleectedCommentID;

    private RecyclerView rclComment;
    private ShimmerFrameLayout shimer;
    private ImageView imgBack, imgPost;
    private TextView txtTitle, txtNoData, txtPostTitle;
    private LinearLayout lltMain, lltPost;

    private List<CommentModel> mComments = new ArrayList<>();
    private CommentAdapter mAdapter;

    private PromptDialog promptDialog;

    public CommentListFragment() {
        // Required empty public constructor
    }

    public CommentListFragment(MainActivity mainActivity, int postID, int tabIndex, int seleectedCommentID) {
        this.mActivity = mainActivity;
        this.postID = postID;
        this.tabIndex = tabIndex;
        this.seleectedCommentID = seleectedCommentID;
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
        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Comentarios");
        rclComment = view.findViewById(R.id.rclComments);
        rclComment.setLayoutManager(new LinearLayoutManager(mActivity));
        rclComment.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CommentAdapter(mActivity, mComments, CommentCellType.ORIGINCOMMENT, new CommentAdapter.CommentCellListener() {
            @Override
            public void onClickUserImage(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
            }

            @Override
            public void onClickImageSelectLike(int commentID) {
                onCallSetLikePost(commentID, 1);
            }

            @Override
            public void onClickImageSelectedLike(int commentID) {
                onCallSetLikePost(commentID, 1);
            }

            @Override
            public void onClickImageSelectDisLike(int commentID) {
                onCallSetLikePost(commentID, 0);
            }

            @Override
            public void onClickImageSelectedDisLike(int commentID) {
                onCallSetLikePost(commentID, 0);
            }

            @Override
            public void onClickComment(CommentModel comment) {
                mActivity.addFragment(new CommentCommentFragment(mActivity, comment, tabIndex), tabIndex);
            }

            @Override
            public void onClickEdit(CommentModel comment) {
                mActivity.addFragment(new CommentEditFragment(mActivity, comment), tabIndex);
            }

            @Override
            public void onClickDelete(int commentID, int position) {
                promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", new PromptDialog.PromptDialogListener() {
                    @Override
                    public void onClickOKUBListener() {
                        onCallDeleteComment(commentID, position);
                    }
                });
                promptDialog.show();
            }

            @Override
            public void onClickMention(int userid) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userid, null, null, false), tabIndex);
            }
        });
        mAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rclComment.setAdapter(mAdapter);
        shimer = view.findViewById(R.id.shimer);
        txtNoData = view.findViewById(R.id.txtNoData);
        imgPost = view.findViewById(R.id.imgPost);
        imgPost.setOnClickListener(v -> mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex));
        txtPostTitle = view.findViewById(R.id.txtPostTitle);
        lltMain = view.findViewById(R.id.lltMain);
        lltPost = view.findViewById(R.id.lltOriginPost);
        lltPost.setOnClickListener(v -> mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex));
    }

    private void initData() {
        mComments.clear();
        txtNoData.setVisibility(View.GONE);
        lltMain.setVisibility(View.GONE);
        rclComment.setVisibility(View.GONE);
        lltPost.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_COMMENT_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                lltMain.setVisibility(View.VISIBLE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    lltPost.setVisibility(seleectedCommentID == 0 ? View.GONE : View.VISIBLE);
                    JSONObject postData = response.getJSONObject("postData");
                    AppUtil.loadImageByUrl(mActivity, imgPost, postData.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
                    txtPostTitle.setText(postData.getString("post_title"));
                    JSONArray comments = response.getJSONArray("postComment");
                    if (comments.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclComment.setVisibility(View.VISIBLE);
                        for (int i = 0; i < comments.length(); i++) {
                            CommentModel comment = new CommentModel();
                            comment.initWithJSON(comments.getJSONObject(i));
                            if (comment.postID == seleectedCommentID) {
                                comment.isSelectedComment = true;
                            } else {
                                comment.isSelectedComment = false;
                            }
                            mComments.add(comment);
                        }
                        Collections.sort(mComments, (o1, o2) -> Integer.valueOf(o2.votCnt).compareTo(o1.votCnt));
                        mAdapter.notifyDataSetChanged();
                        if (seleectedCommentID != 0) {
                            int position = 0;
                            for (int i = 0; i < mComments.size() - 1; i++) {
                                if (seleectedCommentID == mComments.get(i).postID) {
                                    position = i;
                                    break;
                                }
                            }
                            rclComment.scrollToPosition(position);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    txtNoData.setVisibility(View.VISIBLE);
                    rclComment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                lltMain.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                lltMain.setVisibility(View.GONE);
            }
        });
    }

    private void onCallSetLikePost(int commentID, int status) {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + commentID);
        param.put("like_dislike_type", "" + status);
        param.put("postComment", "qw");
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_LIKE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                initData();
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

    private void onCallDeleteComment(int commentID, int position) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + commentID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELET_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                mComments.remove(position);
                mAdapter.notifyDataSetChanged();
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