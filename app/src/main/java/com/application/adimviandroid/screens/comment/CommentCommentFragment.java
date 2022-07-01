package com.application.adimviandroid.screens.comment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.CommentAdapter;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.screens.MainActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentCommentFragment extends Fragment {

    private MainActivity mActivity;
    private CommentModel originComment;
    private int tabIndex;

    private ImageView imgUseer, imgBack, imgVerify, imgSend;
    private TextView txtTitle, txtUserName, txtCreated, txtComment;
    private EditText edtComment;
    private RecyclerView rclComment;
    private NestedScrollView nsContainer;
    private ShimmerFrameLayout shimer;

    private List<CommentModel> mComments = new ArrayList<>();
    private CommentAdapter mAdatper;

    private CommentModel edtCommentModel = new CommentModel();

    private AlertDialog dialog;
    private PromptDialog promptDialog;

    private boolean isEditComment = false;

    public CommentCommentFragment() {

    }

    public CommentCommentFragment(MainActivity mActivity, CommentModel originComment, int tabIndex) {
        this.mActivity = mActivity;
        this.originComment = originComment;
        this.tabIndex = tabIndex;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (originComment == null) {
            originComment = new CommentModel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_comment, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Comentarios");
        imgUseer = view.findViewById(R.id.imgUser);
        AppUtil.loadImageByUrl(mActivity, imgUseer, originComment.userAvatar, ImagePlaceHolderType.USERIMAGE);
        txtUserName = view.findViewById(R.id.txt_username);
        txtUserName.setText(originComment.userName);
        imgVerify = view.findViewById(R.id.img_verified);
        imgVerify.setVisibility(originComment.verify == 1 ? View.VISIBLE : View.GONE);
        txtCreated = view.findViewById(R.id.txtCreated);
        txtCreated.setText(originComment.created);
        txtComment = view.findViewById(R.id.txtComment);
        txtComment.setText(originComment.comment);
        imgSend = view.findViewById(R.id.imgSend);
        imgSend.setOnClickListener(v -> {
            if (isEditComment) {
                onCallEditComment();
            } else {
                onCallAddComment();
            }
        });
        edtComment = view.findViewById(R.id.edtComment);
        nsContainer = view.findViewById(R.id.nsContainer);
        shimer = view.findViewById(R.id.shimer);
        rclComment = view.findViewById(R.id.rclComments);
        rclComment.setLayoutManager(new LinearLayoutManager(mActivity));
        rclComment.setItemAnimator(new DefaultItemAnimator());
        mAdatper = new CommentAdapter(mActivity, mComments, CommentCellType.COMMENTCOMMENT, new CommentAdapter.CommentCellListener() {
            @Override
            public void onClickUserImage(int userID) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
            }

            @Override
            public void onClickDelete(int commentID, int position) {
                promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", () -> onCallDeleteComment(commentID, position));
                promptDialog.show();
            }

            @Override
            public void onClickEdit(CommentModel comment) {
                edtCommentModel = comment;
                edtComment.setText(comment.comment);
                isEditComment = true;
            }

            @Override
            public void onClickMention(int userid) {
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userid, null, null, false), tabIndex);
            }
        });
        rclComment.setAdapter(mAdatper);
    }

    private void initData() {
        isEditComment = false;
        mComments.clear();
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        nsContainer.setVisibility(View.GONE);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + originComment.postID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_COMMENTCOMMENT_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    nsContainer.setVisibility(View.VISIBLE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray comments = response.getJSONArray("CommentComment");
                    for (int i = 0; i < comments.length(); i++) {
                        CommentModel comment = new CommentModel();
                        comment.initWithJSON(comments.getJSONObject(i));
                        mComments.add(comment);
                    }
                    mAdatper.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                nsContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                nsContainer.setVisibility(View.VISIBLE);
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
                mAdatper.notifyDataSetChanged();
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

    private void onCallAddComment() {
        if (edtComment.getText().toString().isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Introduce un comentario.");
            return;
        }
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("postid", "" + originComment.postID);
        param.put("comment", edtComment.getText().toString());
        param.put("type", "C");
        param.put("categoryid", "" + originComment.categoryID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_POST_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                edtComment.setText("");
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

    private void onCallEditComment() {
        if (edtComment.getText().toString().isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Introduce un comentario.");
            return;
        }
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + edtCommentModel.postID);
        param.put("comment", edtComment.getText().toString());
        param.put("type", "C");
        ApiUtil.onAPIConnectionResponse(ApiUtil.EDIT_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                edtComment.setText("");
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
}