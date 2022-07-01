package com.application.adimviandroid.screens.profile.viewpager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.WallAdapter;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.models.RepostModel;
import com.application.adimviandroid.models.RewallModel;
import com.application.adimviandroid.models.TagModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.follow.WallCommentFragment;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.screens.post.PostListByTagFragment;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.types.MuroCellType;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.ui.MuroFeatureGuideDialog;
import com.application.adimviandroid.ui.MuroFeatureSuccessDialog;
import com.application.adimviandroid.ui.PromptDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MuroFragment extends Fragment {

    public static MuroFragment instance;

    private MainActivity mActivity;
    private int selectedUserID, tabIndex;
    private RepostModel repost;
    private RewallModel rewall;

    private LinearLayout lltMain;
    private ConstraintLayout ctlRepost, ctlRewall;
    private ImageView imgRePost, imgRewallUser, imgRewallUserVerify, imgRewallContent, imgCamera, imgSend, imgClose, imgTag;
    private CardView crdImgRewall;
    private TextView txtRepostTitle, txtRepostCreated, txtRepostCancel, txtRewallUserName, txtRewallCancel, txtRewallCreated, txtRewallContent, txtNoData;
    private EditText edtContent;
    private RecyclerView rclMuro;
    private ShimmerFrameLayout shimmer;
    private EditText edtTags;
    private FlexboxLayout flTags;
    private ConstraintLayout cslMuro;

    private ImageDialog imageDialog;
    private PromptDialog promptDialog;
    private AlertDialog alertDialog;
    private MuroFeatureSuccessDialog muroFeatureSuccessDialog;
    private MuroFeatureGuideDialog muroFeatureGuideDialog;
    private List<MuroModel> mMuros = new ArrayList<>();
    private WallAdapter mAdpater;

    private int SUBMIT_TYPE = 0; /// 0: add normal, 1: remuro, 2: edit remuro ///
    private File uploadFile;

    private List<String> strTags = new ArrayList<>();

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private List<SkuDetails> skuProducts = new ArrayList<>();
    private BillingClient billingClient;
    private MuroModel selectedMuroForDestacar;

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            if (result != null) {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    imgCamera.setImageURI(null);
                    imgCamera.setImageURI(result.getUriContent());
                    uploadFile = new File(result.getUriFilePath(mActivity, true));
                }
            }
        }
    });

    public MuroFragment() {
        // Required empty public constructor
    }

    public MuroFragment(MainActivity mainActivity, int selectedUserID, RepostModel repost, RewallModel rewall, int tabIndex) {
        this.mActivity = mainActivity;
        this.selectedUserID = selectedUserID;
        this.tabIndex = tabIndex;
        this.repost = repost;
        this.rewall = rewall;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        instance = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (imageDialog != null && imageDialog.isShowing()) {
            imageDialog.dismiss();
        }

        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        if (muroFeatureSuccessDialog != null && muroFeatureSuccessDialog.isShowing()) {
            muroFeatureSuccessDialog.dismiss();
        }

        if (muroFeatureGuideDialog != null && muroFeatureGuideDialog.isShowing()) {
            muroFeatureGuideDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_muro, container, false);
        initUIView(view);
        initData();
        initIAP();
        return view;
    }

    private void initData() {
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
        txtNoData.setVisibility(View.GONE);
        rclMuro.setVisibility(View.GONE);
        mMuros.clear();

        Map<String, String> param = new HashMap<>();
        param.put("touserid", "" + selectedUserID);
        param.put("login_userId", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.WALL_POST_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray wallPost = response.getJSONArray("wallPost");
                    if (wallPost.length() == 0) {
                        rclMuro.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclMuro.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        for (int i = 0; i < wallPost.length(); i++) {
                            MuroModel muro = new MuroModel();
                            muro.initWithJSON(wallPost.getJSONObject(i));
                            mMuros.add(muro);
                        }
                        mAdpater.notifyDataSetChanged();
                        cslMuro.setVisibility(mActivity.getPublication() ? View.VISIBLE : View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                rclMuro.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                rclMuro.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initIAP() {
        purchasesUpdatedListener = (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase: list) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), "User Cancelled", AppConstant.SHOW_BANNER_TIME);
            } else {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), "Error Occured", AppConstant.SHOW_BANNER_TIME);
            }
        };

        billingClient = BillingClient.newBuilder(mActivity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d("Service ====> ", "Disconnected");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<String> skuList = new ArrayList<>();
                    skuList.add("md199");
                    SkuDetailsParams.Builder params = new SkuDetailsParams().newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(), (billingResult1, list) -> {
                        skuProducts.clear();
                        skuProducts.addAll(list);
                        Collections.sort(skuProducts, (o1, o2) -> o1.getPriceAmountMicros() > o2.getPriceAmountMicros() ? 0 : -1);
                    });
                }
            }
        });
    }

    private void onPurchase() {
        if (skuProducts.size() > 0) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuProducts.get(0)).build();
            int reponseCode = billingClient.launchBillingFlow(mActivity, billingFlowParams).getResponseCode();
        }
    }

    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Map<String, String> param = new HashMap<>();
                param.put("messageid", "" + selectedMuroForDestacar.messageID);
                param.put("userid", "" + SharedUtil.getSharedUserID());
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                ApiUtil.onAPIConnectionResponse(ApiUtil.SET_FEATURE_WALL, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback(){
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        AppUtil.onDismissProgressDialog(dialog);
                        selectedMuroForDestacar.paid = 1;
                        mAdpater.notifyDataSetChanged();
                        muroFeatureSuccessDialog = new MuroFeatureSuccessDialog(mActivity);
                        muroFeatureSuccessDialog.show();
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
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    private void initUIView(View view) {
        shimmer = view.findViewById(R.id.shimer);
        lltMain = view.findViewById(R.id.lltMain);
        rclMuro = view.findViewById(R.id.rclMuro);
        cslMuro = view.findViewById(R.id.cslMuro);
        txtNoData = view.findViewById(R.id.txtNoData);
        rclMuro.setLayoutManager(new LinearLayoutManager(mActivity));
        rclMuro.setItemAnimator(new DefaultItemAnimator());
        mAdpater = new WallAdapter(mActivity, mMuros, MuroCellType.PROFILE, new WallAdapter.WallAdapterListener() {
            @Override
            public void onClickUserAvatar(int userID) {
                if (selectedUserID == userID) {
                    return;
                }
                mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, userID, null, null, false), tabIndex);
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
                mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex);
            }

            @Override
            public void onClickComment(int postID, int fromUserID) {
                mActivity.addFragment(new WallCommentFragment(mActivity, postID, fromUserID, tabIndex), tabIndex);
            }

            @Override
            public void onClickLike(int postID) {
                onCallSetFavorite(postID);
            }

            @Override
            public void onClickRemuro(MuroModel model) {
                SUBMIT_TYPE = 1;
                repost = null;
                rewall = new RewallModel();
                rewall.id = model.messageID;
                rewall.userAvatar = model.userAvatar;
                rewall.imageUrl = model.imageUrl;
                rewall.created = model.created;
                rewall.verify = model.verify;
                rewall.username = model.username;
                rewall.content = model.content;
                if (selectedUserID == SharedUtil.getSharedUserID() && tabIndex == ProfileFragment.TAB_POSITION) {
                    handleMuro(repost, rewall);
                } else {
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, SharedUtil.getSharedUserID(), rewall, null, false), tabIndex);
                }
            }

            @Override
            public void onClickDelete(int id) {
                promptDialog = new PromptDialog(mActivity, " ¿Estás segur@ de que deseas eliminar esta publicación?", () -> onCallDeleteWall(id));
                promptDialog.show();
            }

            @Override
            public void onClickEdit(MuroModel model) {
                SUBMIT_TYPE = 2;
                rewall = new RewallModel();
                rewall.id = model.messageID;
                edtContent.setText(model.content);
            }

            @Override
            public void onClickOriginWallRemuro(RewallModel rewallModel) {
                repost = null;
                rewall = rewallModel;
                if (selectedUserID == SharedUtil.getSharedUserID() && tabIndex == ProfileFragment.TAB_POSITION) {
                    handleMuro(repost, rewall);
                } else {
                    mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, SharedUtil.getSharedUserID(), rewall, null, false), tabIndex);
                }

            }

            @Override
            public void onClickTag(TagModel tagModel) {
                mActivity.addFragment(new PostListByTagFragment(mActivity, tagModel, tabIndex), tabIndex);
            }

            @Override
            public void onClickDestacar(MuroModel muro) {
                muroFeatureGuideDialog = new MuroFeatureGuideDialog(mActivity, () -> {
                    selectedMuroForDestacar = muro;
                    onPurchase();
                });
                muroFeatureGuideDialog.show();
            }
        });
        rclMuro.setAdapter(mAdpater);

        ctlRepost = view.findViewById(R.id.ctlRepost);
        ctlRewall = view.findViewById(R.id.ctlRewall);
        imgRePost = view.findViewById(R.id.imgRePost);
        imgRewallUser = view.findViewById(R.id.imgReWallUser);
        imgRewallUserVerify = view.findViewById(R.id.imgRewallUserVerify);
        imgRewallContent = view.findViewById(R.id.imgRewall);
        imgCamera = view.findViewById(R.id.imgCameraMessage);
        imgCamera.setOnClickListener(v -> onSetCamera());
        imgSend = view.findViewById(R.id.imgSend);
        imgSend.setOnClickListener(v -> onCallSubmitMuro());
        crdImgRewall = view.findViewById(R.id.crdImgRewall);
        txtRepostCancel = view.findViewById(R.id.txtRepostCancel);
        txtRepostCreated = view.findViewById(R.id.txtRepostCreated);
        txtRepostTitle = view.findViewById(R.id.txtRepostTitle);
        txtRewallCancel = view.findViewById(R.id.txtRewallCancel);
        txtRewallCreated = view.findViewById(R.id.txtRewallCreated);
        txtRewallContent = view.findViewById(R.id.txtRewallContent);
        txtRewallUserName = view.findViewById(R.id.txtRewallUserName);
        txtRepostCancel.setOnClickListener(v -> {
            repost = null;
            handleMuro(repost, rewall);
        });
        txtRewallCancel.setOnClickListener(v -> {
            rewall = null;
            handleMuro(repost, rewall);
        });
        edtContent = view.findViewById(R.id.edtContent);

        edtContent.setOnTouchListener((v, event) -> {
            if (edtContent.hasFocus()) {
                v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });
        handleMuro(repost, rewall);

        imgClose = view.findViewById(R.id.imgClose);
        imgTag = view.findViewById(R.id.imgTag);
        flTags = view.findViewById(R.id.flTag);
        imgClose.setVisibility(View.GONE);
        flTags.setVisibility(View.GONE);
        edtTags = view.findViewById(R.id.edtTag);
        imgTag.setOnClickListener(v -> {
            imgClose.setVisibility(View.VISIBLE);
            flTags.setVisibility(View.VISIBLE);
        });
        imgClose.setOnClickListener(v -> {
            closeTag();
        });
        edtTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.isEmpty()) {
                    return;
                }
                if (str.contains(" ")) {
                    String strTag = "#" + str.trim();
                    if (!strTags.contains(strTag)) {
                        addTag("#" + str.trim());
                        strTags.add(strTag);
                    }
                    edtTags.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void closeTag() {
        edtTags.setText("");
        imgClose.setVisibility(View.GONE);
        flTags.setVisibility(View.GONE);
        while (flTags.getChildCount() > 1) {
            flTags.removeViewAt(0);
        }
        strTags.clear();
    }

    private void addTag(String strTag) {
        Chip chip = new Chip(mActivity);
        chip.setText(strTag);
        chip.setCloseIconVisible(true);
        flTags.addView(chip, flTags.getChildCount() - 1);
        chip.setOnCloseIconClickListener(v -> {
            flTags.removeView(chip);
            strTags.remove(chip.getText());
        });
    }

    private void handleMuro(RepostModel repost, RewallModel rewall) {
        if (repost == null) {
            ctlRepost.setVisibility(View.GONE);
        } else {
            ctlRepost.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mActivity, imgRePost, repost.userAvatar, ImagePlaceHolderType.POSTIMAGE);
            txtRepostTitle.setText(repost.title);
            txtRepostCreated.setText(repost.created);
        }

        if (rewall == null) {
            ctlRewall.setVisibility(View.GONE);
        } else {
            ctlRewall.setVisibility(View.VISIBLE);
            AppUtil.loadImageByUrl(mActivity, imgRewallUser, rewall.userAvatar, ImagePlaceHolderType.USERIMAGE);
            txtRewallCreated.setText(rewall.created);
            txtRewallUserName.setText(rewall.username);
            imgRewallUserVerify.setVisibility(rewall.verify == 1 ? View.VISIBLE : View.GONE);
            txtRewallContent.setText(rewall.content);
            if (rewall.imageUrl.isEmpty()) {
                crdImgRewall.setVisibility(View.GONE);
                imgRewallContent.setVisibility(View.GONE);
            } else {
                imgRewallContent.setVisibility(View.VISIBLE);
                crdImgRewall.setVisibility(View.VISIBLE);
                AppUtil.loadImageByUrl(mActivity, imgRewallContent, rewall.imageUrl, ImagePlaceHolderType.POSTIMAGE);
            }
        }
    }

    private void onCallSetFavorite(int id) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + id);
        param.put("userid", "" + selectedUserID);
        param.put("login_userId", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_WALL_FAVOURITE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    private void onCallDeleteWall(int id) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + id);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELETE_WALL_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    private void onCallSubmitMuro() {
        if (edtContent.getText().toString().isEmpty()) {
            alertDialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Escribe algún comentario");
            return;
        }
        if (SUBMIT_TYPE == 2) {
            onCallEditRemuro();
        } else {
            if (uploadFile == null) {
                onCallAddComment();
            } else {
                onCallUploadImage();
            }
        }
    }

    private void onCallAddComment() {
        String tags = TextUtils.join(",, ", strTags);
        Map<String, String> param = new HashMap<>();
        if (repost != null) {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("repost", "" + repost.id);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        } else if (rewall != null) {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("rewall", "" + rewall.id);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        } else {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        }

        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.ADD_NEW_WALL, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                initData();
                alertDialog = AppUtil.showNormalDialog(mActivity, "Nuevo muro", "Tu publicación ha sido añadida al muro");
                edtContent.setText("");
                handleMuro(null, null);
                closeTag();
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                handleMuro(null, null);
                closeTag();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                handleMuro(null, null);
                closeTag();
            }
        });
    }

    private void onCallUploadImage() {
        String tags = TextUtils.join(",, ", strTags);
        Map<String, String> param = new HashMap<>();
        if (repost != null) {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("repost", "" + repost.id);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        } else if (rewall != null) {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("rewall", "" + rewall.id);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        } else {
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + selectedUserID);
            param.put("wall_message", edtContent.getText().toString());
            param.put("tags", tags);
        }

        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.ADD_NEW_WALL, param, "imageUrl", uploadFile, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                initData();
                edtContent.setText("");
                alertDialog = AppUtil.showNormalDialog(mActivity, "Nuevo muro", "Tu publicación ha sido añadida al muro");
                imgCamera.setImageURI(null);
                imgCamera.setImageResource(R.drawable.ic_camera_darkgray);
                handleMuro(null, null);
                closeTag();
                uploadFile = null;
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                imgCamera.setImageURI(null);
                imgCamera.setImageResource(R.drawable.ic_camera_darkgray);
                handleMuro(null, null);
                closeTag();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                imgCamera.setImageURI(null);
                imgCamera.setImageResource(R.drawable.ic_camera_darkgray);
                handleMuro(null, null);
                closeTag();
            }
        });
    }

    private void onCallEditRemuro() {
        Map<String, String> param = new HashMap<>();
        param.put("messageid", "" + rewall.id);
        param.put("wall_message", edtContent.getText().toString());

        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.EDIT_WALL_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                initData();
                edtContent.setText("");
                handleMuro(null, null);
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                handleMuro(null, null);
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
                edtContent.setText("");
                handleMuro(null, null);
            }
        });
    }


    private void onSetCamera() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.CAMERA}, 10001);
            return;
        }
        cropImage.launch(AppUtil.options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cropImage.launch(AppUtil.options);
                } else {
                    BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.PERMISSION_DENIED, AppConstant.SHOW_BANNER_TIME);
                }
                return;
            }
        }
    }

    public int getSelectedUserID() {
        return selectedUserID;
    }

}