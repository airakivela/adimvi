package com.application.adimviandroid.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.RoomModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FollowDialog extends Dialog {

    private Context mContext;
    private RoomModel mRoom;
    private FollowDilaogListener mListener;

    private ImageView imgUser, imgClose;
    private TextView txtUserName;
    private Button btnFollow, btnUnFollow;

    private boolean isClickdAnyFollowButton = false;

    public FollowDialog(@NonNull Context context, RoomModel room, FollowDilaogListener listener) {
        super(context);
        this.mContext = context;
        this.mRoom = room;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_follow);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgUser = findViewById(R.id.imgUser);
        txtUserName = findViewById(R.id.txtUserName);
        imgClose = findViewById(R.id.imgClose);
        btnFollow = findViewById(R.id.btnSeguir);
        btnUnFollow = findViewById(R.id.btnSiguiendo);
        txtUserName.setText(mRoom.adminName);
        imgClose.setOnClickListener(v -> {
            if (isClickdAnyFollowButton) {
                mListener.onClickCloseButton();
            }
            dismiss();
        });
        if (mRoom.adminAvatar.isEmpty()) {
            imgUser.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrl(mContext, imgUser, ApiUtil.ImageUrl + mRoom.adminAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        if (mRoom.isSiguiendo == 1) {
            btnFollow.setVisibility(View.GONE);
            btnUnFollow.setVisibility(View.VISIBLE);
        } else {
            btnFollow.setVisibility(View.VISIBLE);
            btnUnFollow.setVisibility(View.GONE);
        }
        btnUnFollow.setOnClickListener(v -> {
            onCallUserFollow(true);
        });
        btnFollow.setOnClickListener(v -> {
            onCallUserFollow(false);
        });
    }

    private void onCallUserFollow(boolean isFollow) {
        isClickdAnyFollowButton = true;
        Map<String, String> parma = new HashMap<>();
        parma.put("userid", "" + SharedUtil.getSharedUserID());
        parma.put("entityid", "" + mRoom.adminID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, parma, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                if (isFollow) {
                    btnUnFollow.setVisibility(View.GONE);
                    btnFollow.setVisibility(View.VISIBLE);
                } else {
                    btnUnFollow.setVisibility(View.VISIBLE);
                    btnFollow.setVisibility(View.GONE);
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

    public interface FollowDilaogListener {
        void onClickCloseButton();
    }
}
