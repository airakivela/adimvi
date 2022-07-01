package com.application.adimviandroid.screens.follow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.CommentAdapter;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.models.MuroModel;
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

public class WallCommentFragment extends Fragment {

    private MainActivity mActivity;
    private int messageID;
    private int toUserID;
    private int tabIndex;

    private ImageView imgBack, imgSend;
    private TextView txtTitle;
    private EditText edtComment;
    private RecyclerView rclComment;
    private NestedScrollView nsContainer;
    private ShimmerFrameLayout shimer;
    
    //message information//
    public ImageView imgUserAvatar, imgOriginWallUserAvatar, imgPost, imgOriginWallPost, imgOriginPost, imgUserVerify, imgOriginWallUserVerify;
    public TextView txtUserName, txtOriginWallUserName, txtCreated, txtOriginWallCreated, txtOriginPostCreated, txtContent, txtOriginWallContent,
            txtOriginPostTitle;
    public LinearLayout lltOriginWall;
    public RelativeLayout rltOriginPost;
    public CardView cardPostImg, cardOriginWallPostImg;

    private List<CommentModel> mComments = new ArrayList<>();
    private CommentAdapter mAdatper;

    private CommentModel edtModel = new CommentModel();

    private boolean isEdit = false;

    private AlertDialog dialog;
    private PromptDialog promptDialog;

    public WallCommentFragment() {
        // Required empty public constructor
    }

    public WallCommentFragment(MainActivity mAcitivy, int messageID, int toUserID, int tabIndex) {
        this.mActivity = mAcitivy;
        this.messageID = messageID;
        this.toUserID = toUserID;
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
        View view = inflater.inflate(R.layout.fragment_wall_comment, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Comentarios");
        imgSend = view.findViewById(R.id.imgSend);
        imgSend.setOnClickListener(v -> {
            if (isEdit) {
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
                promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", () -> {
                    onCallDeleteComment(commentID, position);
                });
                promptDialog.show();
            }

            @Override
            public void onClickEdit(CommentModel comment) {
                edtComment.setText(comment.comment);
                edtModel = comment;
                isEdit = true;
            }
        });
        rclComment.setAdapter(mAdatper);
        
        // message information //
        imgUserAvatar = view.findViewById(R.id.imgUser);
        imgOriginWallUserAvatar = view.findViewById(R.id.imgOriginWallUser);
        imgPost = view.findViewById(R.id.imgPost);
        imgOriginWallPost = view.findViewById(R.id.imgOriginWallPost);
        imgOriginPost = view.findViewById(R.id.imgOriginPostImge);
        imgUserVerify = view.findViewById(R.id.imgVerify);
        imgOriginWallUserVerify = view.findViewById(R.id.imgOriginWallVerify);
        txtUserName = view.findViewById(R.id.txtUseName);
        txtOriginWallUserName = view.findViewById(R.id.txtOriginWallUseName);
        txtCreated = view.findViewById(R.id.txtCreated);
        txtOriginWallCreated = view.findViewById(R.id.txtOriginWallCreated);
        txtOriginPostCreated = view.findViewById(R.id.txtOriginPostCreated);
        txtContent = view.findViewById(R.id.txtContent);
        txtOriginWallContent = view.findViewById(R.id.txtOriginWallContent);
        txtOriginPostTitle = view.findViewById(R.id.txtOriginPostContent);
        lltOriginWall = view.findViewById(R.id.lltOriginWall);
        rltOriginPost = view.findViewById(R.id.lltOriginPost);
        cardPostImg = view.findViewById(R.id.cardPostImg);
        cardOriginWallPostImg = view.findViewById(R.id.cardOriginWallPostImg);
    }

    private void handleUV(MuroModel muro) {
        AppUtil.loadImageByUrl(mActivity, imgUserAvatar, muro.userAvatar, ImagePlaceHolderType.USERIMAGE);
        if (muro.imageUrl.isEmpty()) {
            cardPostImg.setVisibility(View.GONE);
        } else {
            cardPostImg.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mActivity, imgPost, muro.imageUrl, ImagePlaceHolderType.POSTIMAGE);
        }
        imgUserVerify.setVisibility(muro.verify == 1 ? View.VISIBLE : View.GONE);
        txtCreated.setText(muro.created);
        txtUserName.setText(muro.username);
        txtContent.setText(muro.content);
        if (muro.rewallModel == null) {
            lltOriginWall.setVisibility(View.GONE);
        } else {
            lltOriginWall.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mActivity, imgOriginWallUserAvatar, muro.rewallModel.userAvatar, ImagePlaceHolderType.USERIMAGE);
            imgOriginWallUserVerify.setVisibility(muro.rewallModel.verify == 1 ? View.VISIBLE : View.GONE);
            txtOriginWallUserName.setText(muro.rewallModel.username);
            txtOriginWallCreated.setText(muro.rewallModel.created);
            txtOriginWallContent.setText(muro.rewallModel.content);
            if (muro.rewallModel.imageUrl.isEmpty()) {
                cardOriginWallPostImg.setVisibility(View.GONE);
            } else {
                cardOriginWallPostImg.setVisibility(View.VISIBLE);
                AppUtil.loadImageByUrl(mActivity, imgOriginWallPost, muro.rewallModel.imageUrl, ImagePlaceHolderType.POSTIMAGE);
            }
        }
        if (muro.repostModel == null) {
            rltOriginPost.setVisibility(View.GONE);
        } else {
            rltOriginPost.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mActivity, imgOriginPost, muro.repostModel.content, ImagePlaceHolderType.POSTIMAGE);
            txtOriginPostTitle.setText(muro.repostModel.title);
            txtOriginPostCreated.setText(muro.repostModel.created);
        }
    }

    private void initData() {
        mComments.clear();
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        nsContainer.setVisibility(View.GONE);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + messageID);
        param.put("login_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_WALL_COMMENT_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    nsContainer.setVisibility(View.VISIBLE);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray comments = response.getJSONArray("wallComments");
                    for (int i = 0; i < comments.length(); i++) {
                        CommentModel comment = new CommentModel();
                        comment.initWithJSON(comments.getJSONObject(i));
                        mComments.add(comment);
                    }
                    mAdatper.notifyDataSetChanged();
                    JSONObject muronInfo = response.getJSONObject("msgInfo");
                    MuroModel muroModel = new MuroModel();
                    muroModel.initWithJSON(muronInfo);
                    handleUV(muroModel);
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
        param.put("messageid", "" + commentID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELETE_WALL_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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
        param.put("fromuserid", "" + SharedUtil.getSharedUserID());
        param.put("touserid", "" + toUserID);
        param.put("messageid", "" + messageID);
        param.put("wall_comment", edtComment.getText().toString());
        ApiUtil.onAPIConnectionResponse(ApiUtil.REPLY_WALL_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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
        param.put("messageid", "" + edtModel.postID);
        param.put("wall_message", edtComment.getText().toString());
        ApiUtil.onAPIConnectionResponse(ApiUtil.EDIT_WALL_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                edtComment.setText("");
                isEdit = false;
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