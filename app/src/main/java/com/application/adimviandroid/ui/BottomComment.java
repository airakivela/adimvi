package com.application.adimviandroid.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.CommentAdapter;
import com.application.adimviandroid.adapter.MentionAdapter;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.screens.comment.CommentCommentFragment;
import com.application.adimviandroid.screens.comment.CommentEditFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.CommentCellType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomComment extends LinearLayout {

    private Context mContext;
    private List<CommentModel> mComments = new ArrayList<>();
    private BottomCommentListener mListener;
    private int postID, categoryID;
    private ArrayAdapter<MentionUserModel> mentionAdapter;

    private RecyclerView rclComment;
    private SocialAutoCompleteTextView edtComment;
    private ImageView imgSend;
    private TextView txtNoData, txtCommentCnt;
    private ShimmerFrameLayout shimer;

    private CommentAdapter mAdatper;

    public BottomComment(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_bottom_comment, this, true);

        edtComment = findViewById(R.id.edtComment);
        mentionAdapter = new MentionAdapter(context, AppUtil.mMentionUsers);
        edtComment.setMentionAdapter(mentionAdapter);

        rclComment = findViewById(R.id.rclComments);
        rclComment.setLayoutManager(new LinearLayoutManager(mContext));
        rclComment.setItemAnimator(new DefaultItemAnimator());
        rclComment.setNestedScrollingEnabled(true);

        mAdatper = new CommentAdapter(mContext, mComments, CommentCellType.ORIGINCOMMENT, new CommentAdapter.CommentCellListener() {
            @Override
            public void onClickUserImage(int userID) {
                mListener.onClickUserImage(userID);
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
                mListener.onClickComment(comment);
            }

            @Override
            public void onClickEdit(CommentModel comment) {
                mListener.onClickEdit(comment);
            }

            @Override
            public void onClickDelete(int commentID, int position) {

                PromptDialog promptDialog = new PromptDialog(mContext, " ¿Estás segur@ de que deseas eliminar esta publicación?", new PromptDialog.PromptDialogListener() {
                    @Override
                    public void onClickOKUBListener() {
                        onCallDeleteComment(commentID, position);
                    }
                });
                promptDialog.show();
            }

            @Override
            public void onClickMention(int userid) {
                mListener.onClickMention(userid);
            }
        });
        rclComment.setAdapter(mAdatper);

        imgSend = findViewById(R.id.imgSend);
        imgSend.setOnClickListener(v -> {
            if (edtComment.getText().toString().isEmpty()) {
                AppUtil.showNormalDialog(mContext, "Mensaje", "Por favor, escribe un comentario.");
                return;
            }
            Map<String, String> param = new HashMap<>();
            param.put("userid", "" + SharedUtil.getSharedUserID());
            param.put("postid", "" + this.postID);
            param.put("mentions", TextUtils.join(",", edtComment.getMentions()));
            param.put("categoryid", "" + categoryID);
            param.put("comment", edtComment.getText().toString().replace("@", ""));
            param.put("type", "A");
            ProgressDialog dialog = AppUtil.onShowProgressDialog(mContext, AppConstant.LOADING, false);
            ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
                @Override
                public void onEventCallBack(JSONObject obj) {
                    dialog.dismiss();
                    AppUtil.showNormalDialog(mContext, "Nuevo comentario", "¡Tu comentario ha sido añadido!");
                    edtComment.setText("");
                    initData();
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
        });
        shimer = findViewById(R.id.shimer);
        txtNoData = findViewById(R.id.txtNoData);
        txtCommentCnt = findViewById(R.id.txtCommentCnt);
    }

    public void initBottomCommentView(int postID, int categoryID, BottomCommentListener listener) {
        this.mListener = listener;
        this.postID = postID;
        this.categoryID = categoryID;
        initData();
    }

    private void initData() {
        mComments.clear();
        txtNoData.setVisibility(View.GONE);
        rclComment.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        txtCommentCnt.setVisibility(View.GONE);
        shimer.startShimmer();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_COMMENT_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray comments = response.getJSONArray("postComment");
                    if (comments.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        txtCommentCnt.setVisibility(View.VISIBLE);
                        txtCommentCnt.setText("0 comentarios");
                        rclComment.setVisibility(View.GONE);
                    } else {
                        rclComment.setVisibility(View.VISIBLE);
                        txtCommentCnt.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < comments.length(); i++) {
                            CommentModel comment = new CommentModel();
                            comment.initWithJSON(comments.getJSONObject(i));
                            mComments.add(comment);
                        }
                        txtCommentCnt.setText(comments.length() + " comentarios");
                        Collections.sort(mComments, (o1, o2) -> Integer.valueOf(o2.votCnt).compareTo(o1.votCnt));
                        mAdatper.notifyDataSetChanged();
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
                txtCommentCnt.setVisibility(View.GONE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                txtCommentCnt.setVisibility(View.GONE);
            }
        });
    }

    private void onCallSetLikePost(int commentID, int status) {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + commentID);
        param.put("like_dislike_type", "" + status);
        param.put("postComment", "qw");
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mContext, AppConstant.LOADING, false);
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
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mContext, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + commentID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELET_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                mComments.remove(position);
                mAdatper.notifyDataSetChanged();
                txtCommentCnt.setText((mComments.size() - 1) + " comentarios");
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

    public interface BottomCommentListener {
        void onClickUserImage(int userID);
        void onClickComment(CommentModel comment);
        void onClickEdit(CommentModel comment);
        void onClickMention(int userid);
    }
}
