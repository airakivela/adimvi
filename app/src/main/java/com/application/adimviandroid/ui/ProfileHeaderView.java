package com.application.adimviandroid.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.application.adimviandroid.R;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileHeaderView extends LinearLayout {

    private Context mContext;

    private ImageView imgBg, imgSetting, imgVerify;
    private ShapeableImageView imgUser;
    private LinearLayout lltSeguir, lltSiguiendo;
    private TextView txtPublication, txtVotos, txtRespestas, txtSeguidores, txtSiguiendo, txtUserName;
    private RelativeLayout rltContainer;
    private ShimmerFrameLayout shimer;

    private HeaderViewListener mListener;
    private int userID;

    public ProfileHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_profile_header, this, true);
        imgBg = findViewById(R.id.imgUserBackground);
        imgSetting = findViewById(R.id.imgSetting);
        imgVerify = findViewById(R.id.imgVerify);
        imgUser = findViewById(R.id.imgUser);
        lltSeguir = findViewById(R.id.lltSeguir);
        lltSiguiendo = findViewById(R.id.lltSiguiendo);
        txtPublication = findViewById(R.id.txtPublication);
        txtVotos = findViewById(R.id.txtVotos);
        txtRespestas = findViewById(R.id.txtRespuestas);
        txtSeguidores = findViewById(R.id.txtSeguidores);
        txtSiguiendo = findViewById(R.id.txtSeguiendo);
        txtUserName = findViewById(R.id.txtUsername);
        rltContainer = findViewById(R.id.rltContainer);
        shimer = findViewById(R.id.shimer);

        imgSetting.setOnClickListener(v -> mListener.onClickSetting(userID));

        lltSiguiendo.setOnClickListener(v -> onCallSetFollow());
        lltSeguir.setOnClickListener(v -> onCallSetFollow());
    }

    private void onCallSetFollow() {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mContext, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("entityid", "" + userID);
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_FOLLOWING, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                lltSeguir.setVisibility(lltSeguir.getVisibility() == VISIBLE ? GONE : VISIBLE);
                lltSiguiendo.setVisibility(lltSiguiendo.getVisibility() == VISIBLE ? GONE : VISIBLE);
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

    public void initHeader(int userID, HeaderViewListener listener) {
        this.userID = userID;
        this.mListener = listener;

        shimer.setVisibility(VISIBLE);
        shimer.startShimmer();
        rltContainer.setVisibility(GONE);
        Map<String, String> param = new HashMap<>();
        param.put("login_userid", "" + SharedUtil.getSharedUserID());
        param.put("userid", "" + userID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_PROIFLE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONArray response = obj.getJSONArray("response");
                    JSONObject data = response.getJSONObject(0);
                    txtUserName.setText(data.getString("username"));
                    mListener.onSetUserName(data.getString("username"), data.getInt("isVerify"));
                    txtPublication.setText(data.getString("totalPost"));
                    txtVotos.setText(data.getString("positiveVotes"));
                    txtRespestas.setText(data.getString("totalReply"));
                    txtSeguidores.setText(data.getString("totalFollowers"));
                    txtSiguiendo.setText(data.getString("totalFollowing"));
                    String avatar = data.getString("avatarblobid");
                    if (!avatar.isEmpty()) {
                        AppUtil.loadImageByUrl(mContext, imgUser,ApiUtil.ImageUrl + avatar, ImagePlaceHolderType.USERIMAGE);
                    }
                    String bg = data.getString("coverblobid");
                    if (!bg.isEmpty()) {
                        AppUtil.loadImageByUrl(mContext, imgBg, ApiUtil.ImageUrl + bg, ImagePlaceHolderType.BACKGROUNDIMAGE);
                    }
                    imgVerify.setVisibility(data.getInt("isVerify") == 0 ? GONE : VISIBLE);
                    if (SharedUtil.getSharedUserID() == userID) {
                        lltSeguir.setVisibility(GONE);
                        lltSiguiendo.setVisibility(View.GONE);
                        imgSetting.setVisibility(VISIBLE);
                    } else {
                        imgSetting.setVisibility(GONE);
                        if (data.getInt("followup") == 1) {
                            lltSiguiendo.setVisibility(View.VISIBLE);
                            lltSeguir.setVisibility(View.GONE);
                        } else {
                            lltSiguiendo.setVisibility(View.GONE);
                            lltSeguir.setVisibility(View.VISIBLE);
                        }
                    }
                    shimer.setVisibility(GONE);
                    shimer.stopShimmer();
                    rltContainer.setVisibility(VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(GONE);
                shimer.stopShimmer();
                rltContainer.setVisibility(VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(GONE);
                shimer.stopShimmer();
                rltContainer.setVisibility(VISIBLE);
            }
        });
    }

    public void setImageProfile(Uri uri) {
        imgUser.setImageURI(uri);
    }

    public void setImageProfileBG(Uri uri) {
        imgBg.setImageURI(uri);
    }

    public Bitmap getImageProfile() {
        return ((BitmapDrawable)imgUser.getDrawable()).getBitmap();
    }

    public Bitmap getImageProfileBG() {
        return ((BitmapDrawable)imgBg.getDrawable()).getBitmap();
    }

    public interface HeaderViewListener {
        default void onClickSetting(int userid){};
        default void onSetUserName(String name, int verify){};
    }

}
