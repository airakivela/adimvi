package com.application.adimviandroid.screens.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.ProfileHeaderView;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private MainActivity mAcitivty;

    private ImageView imgBack, imgBg, imgBgProfile, imgUserAD;
    private CheckBox chkAD, chkEmail, chkMessage, chkWall;
    private TextView txtTitle, txtAD, txtCover, txtProfile;
    private EditText edtFullName, edtEmail, edtPaypalEmail, edtLocation, edtWeb, edtSocial, edtName, edtAboutMe, edtOldPwd, edtNewPwd, edtConfirmPwd, edtUsrAD;
    private LinearLayout lltAD, lltMain;
    private ProfileHeaderView headerView;
    private Button btnSave, btnSavePwd, btnSelectUserBgImg, btnSelectUserBg, btnSelectADImg;
    private ShimmerFrameLayout shimmer;

    private AlertDialog alertDialog;
    private int IMAGE_INDEX;

    private File file1, file2, file3;
    private boolean isSelectedFile1 = false, isSelectedFile2 = false, isSelectedFile3 = false;

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            if (result != null) {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    if (IMAGE_INDEX == 0) {
                        imgBg.setImageURI(null);
                        imgBg.setImageURI(result.getUriContent());
                        txtProfile.setText("Imagen seleccionada");
                        headerView.setImageProfile(result.getUriContent());
                        isSelectedFile1 = true;
                        file1 = new File(result.getUriFilePath(mAcitivty, true));
                    } else if (IMAGE_INDEX == 1) {
                        imgBgProfile.setImageURI(null);
                        imgBgProfile.setImageURI(result.getUriContent());
                        txtCover.setText("Imagen seleccionada");
                        headerView.setImageProfileBG(result.getUriContent());
                        isSelectedFile2 = true;
                        file2 = new File(result.getUriFilePath(mAcitivty, true));
                    } else {
                        imgUserAD.setImageURI(null);
                        imgUserAD.setImageURI(result.getUriContent());
                        txtAD.setText("Imagen seleccionada");
                        isSelectedFile3 = true;
                        file3 = new File(result.getUriFilePath(mAcitivty, true));
                    }
                }
            }
        }
    });

    public AccountFragment() {
        // Required empty public constructor
    }

    public AccountFragment(MainActivity mainActivity) {
        this.mAcitivty = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mAcitivty == null) {
            mAcitivty = (MainActivity)getActivity();
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mAcitivty.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Mi cuenta");

        txtAD = view.findViewById(R.id.txtAD);
        txtCover = view.findViewById(R.id.txtCover);
        txtProfile = view.findViewById(R.id.txtProfile);

        edtAboutMe = view.findViewById(R.id.edtAboutMe);
        edtFullName = view.findViewById(R.id.edtFullName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPaypalEmail = view.findViewById(R.id.edtPaypal);
        edtLocation = view.findViewById(R.id.edtLocation);
        edtName = view.findViewById(R.id.edtName);
        edtWeb = view.findViewById(R.id.edtWeb);
        edtSocial = view.findViewById(R.id.edtSocial);
        edtOldPwd = view.findViewById(R.id.edtOldPwd);
        edtNewPwd = view.findViewById(R.id.edtNewPwd);
        edtConfirmPwd = view.findViewById(R.id.edtConfirmPwd);
        edtUsrAD = view.findViewById(R.id.edtImageUrl);

        imgBg = view.findViewById(R.id.frg_imgUser);
        imgBgProfile = view.findViewById(R.id.imgUserBg);
        imgUserAD = view.findViewById(R.id.imgUserAD);

        lltAD = view.findViewById(R.id.lltAD);
        lltMain = view.findViewById(R.id.lltMain);

        headerView = view.findViewById(R.id.profileHeader);
        headerView.initHeader(SharedUtil.getSharedUserID(), new ProfileHeaderView.HeaderViewListener() {
        });

        chkMessage = view.findViewById(R.id.chk1);
        chkWall = view.findViewById(R.id.chk2);
        chkEmail = view.findViewById(R.id.chk3);
        chkAD = view.findViewById(R.id.chk4);
        lltAD.setVisibility(View.GONE);
        chkAD.setOnCheckedChangeListener((buttonView, isChecked) -> lltAD.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        btnSave = view.findViewById(R.id.btnSave);
        btnSavePwd = view.findViewById(R.id.btnSavePwd);
        btnSelectUserBg = view.findViewById(R.id.btnProfileImage);
        btnSelectUserBgImg = view.findViewById(R.id.btnProfileBgImage);
        btnSelectADImg = view.findViewById(R.id.btnADImage);
        btnSave.setOnClickListener(this);
        btnSavePwd.setOnClickListener(this);
        btnSelectUserBg.setOnClickListener(this);
        btnSelectUserBgImg.setOnClickListener(this);
        btnSelectADImg.setOnClickListener(this);

        shimmer = view.findViewById(R.id.shimer);
    }

    private void initData() {
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
        lltMain.setVisibility(View.GONE);
        Map<String, String> param = new HashMap<>();
        param.put("login_userid", "" + SharedUtil.getSharedUserID());
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_PROIFLE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    shimmer.setVisibility(View.GONE);
                    shimmer.stopShimmer();
                    lltMain.setVisibility(View.VISIBLE);
                    JSONArray response = obj.getJSONArray("response");
                    JSONObject data = response.getJSONObject(0);
                    edtFullName.setText(data.getString("username"));
                    edtEmail.setText(data.getString("email"));
                    edtPaypalEmail.setText(data.getString("paypal"));
                    edtAboutMe.setText(data.getString("about"));
                    edtLocation.setText(data.getString("location"));
                    edtName.setText(data.getString("name"));
                    edtWeb.setText(data.getString("website"));
                    edtSocial.setText(data.getString("social-networks"));
                    String avatar = data.getString("avatarblobid");
                    if (!avatar.isEmpty()) {
                        AppUtil.loadImageByUrl(mAcitivty, imgBg,ApiUtil.ImageUrl + avatar, ImagePlaceHolderType.USERIMAGE);
                    }
                    String bg = data.getString("coverblobid");
                    if (!bg.isEmpty()) {
                        AppUtil.loadImageByUrl(mAcitivty, imgBgProfile, ApiUtil.ImageUrl + bg, ImagePlaceHolderType.BACKGROUNDIMAGE);
                    }
                    try {
                        int flags = data.getInt("flags");
                        if (flags == 4 || flags == 0 |flags == 5 || flags == 64) {
                            chkMessage.setChecked(true);
                            chkWall.setChecked(true);
                        } else if (flags == 20 || flags == 16 || flags == 21 || flags == 80) {
                            chkMessage.setChecked(false);
                            chkWall.setChecked(true);
                        } else if (flags == 260 || flags == 256 || flags == 261 || flags == 320) {
                            chkMessage.setChecked(true);
                            chkWall.setChecked(false);
                        } else if (flags == 276 || flags == 272 || flags == 277 || flags == 336 ) {
                            chkMessage.setChecked(false);
                            chkWall.setChecked(false);
                        } else {
                            chkMessage.setChecked(false);
                            chkWall.setChecked(false);
                        }
                    } catch (JSONException e) {
                        chkMessage.setChecked(false);
                        chkWall.setChecked(false);
                    }
                    int userAD = data.getInt("fadSense");
                    chkAD.setChecked(userAD == 1);
                    edtUsrAD.setText(data.getString("uadimageurl"));
                    String userADImageURL = data.getString("uadblobid");
                    if (!userADImageURL.isEmpty()) {
                        AppUtil.loadImageByUrl(mAcitivty, imgUserAD,ApiUtil.ImageUrl + userADImageURL, ImagePlaceHolderType.POSTIMAGE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimmer.setVisibility(View.GONE);
                shimmer.stopShimmer();
                lltMain.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimmer.setVisibility(View.GONE);
                shimmer.stopShimmer();
                lltMain.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                onCallUpdateProfile();
                break;
            case R.id.btnSavePwd:
                onCallUpdatePassword();
                break;
            case R.id.btnProfileImage:
                IMAGE_INDEX = 0;
                onSetCamera();
                break;
            case R.id.btnProfileBgImage:
                IMAGE_INDEX = 1;
                onSetCamera();
                break;
            case R.id.btnADImage:
                IMAGE_INDEX = 2;
                onSetCamera();
        }
    }

    private void onCallUpdateProfile() {
        ProgressDialog prgoressDialog = AppUtil.onShowProgressDialog(mAcitivty, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("user_id", "" + SharedUtil.getSharedUserID());
        param.put("email", edtEmail.getText().toString());
        param.put("full_name", edtFullName.getText().toString());
        param.put("paypal", edtPaypalEmail.getText().toString());
        param.put("about_me", edtAboutMe.getText().toString());
        param.put("location", edtLocation.getText().toString());
        param.put("links_websites", edtWeb.getText().toString());
        param.put("social_networks", edtSocial.getText().toString());
        param.put("handle", edtFullName.getText().toString());
        param.put("publications_wall", chkWall.isChecked() ? "1" : "0");
        param.put("private_messages", chkMessage.isChecked() ? "1" : "0");
        param.put("subscribe_email", chkEmail.isChecked() ? "1" : "0");
        param.put("full_name1", edtName.getText().toString());
        param.put("uadimageurl", edtUsrAD.getText().toString());
        param.put("fadsense", chkAD.isChecked() ? "1" : "0");

        file1 = !isSelectedFile1 ? null : file1;
        file2 = !isSelectedFile2 ? null : file2;
        file3 = !isSelectedFile3 ? null : file3;
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.EDIT_PROFILE, param, "profileImage", file1, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    if (obj.getInt("code") == 200) {
                        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.EDIT_PROFILE, param, "coverImage", file2, new ApiUtil.APIManagerCallback() {
                            @Override
                            public void onEventCallBack(JSONObject obj) {
                                try {
                                    if (obj.getInt("code") == 200) {
                                        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.EDIT_PROFILE, param, "adsImage", file3, new ApiUtil.APIManagerCallback() {
                                            @Override
                                            public void onEventCallBack(JSONObject obj) {
                                                prgoressDialog.dismiss();
                                                alertDialog = AppUtil.showNormalDialog(mAcitivty, "Perfil actualizado", "¡Tu datos se han guardado!");
                                                isSelectedFile1 = false;
                                                isSelectedFile2 = false;
                                                isSelectedFile3 = false;
                                                initData();
                                                SharedUtil.setSharedUserAD(chkAD.isChecked() ? 1 : 0);
                                            }

                                            @Override
                                            public void onEventInternetError(Exception e) {
                                                prgoressDialog.dismiss();
                                            }

                                            @Override
                                            public void onEventServerError(Exception e) {
                                                prgoressDialog.dismiss();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onEventInternetError(Exception e) {
                                prgoressDialog.dismiss();
                            }

                            @Override
                            public void onEventServerError(Exception e) {
                                prgoressDialog.dismiss();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                prgoressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                prgoressDialog.dismiss();
            }
        });
    }

    private Bitmap getImageUSERAD() {
        return ((BitmapDrawable)imgUserAD.getDrawable()).getBitmap();
    }

    private void onCallUpdatePassword() {
        if (edtOldPwd.getText().toString().isEmpty()) {
            alertDialog = AppUtil.showNormalDialog(mAcitivty, "Mensaje", "Por favor, introduce tu contraseña anterior.");
            alertDialog.show();
            return;
        }

        if (edtNewPwd.getText().toString().isEmpty()) {
            alertDialog = AppUtil.showNormalDialog(mAcitivty, "Mensaje", "Por favor, introduce tu nueva contraseña.");
            alertDialog.show();
            return;
        }

        if (edtConfirmPwd.getText().toString().equals(edtNewPwd.getText().toString())) {
            alertDialog = AppUtil.showNormalDialog(mAcitivty, "Mensaje", "Por favor, repite la nueva contraseña.");
            alertDialog.show();
            return;
        }

        ProgressDialog dialog = AppUtil.onShowProgressDialog(mAcitivty, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("user_id", "" + SharedUtil.getSharedUserID());
        param.put("oldPass", edtOldPwd.getText().toString());
        param.put("newPass", edtNewPwd.getText().toString());
        param.put("confirmPass", edtConfirmPwd.getText().toString());

        ApiUtil.onAPIConnectionResponse(ApiUtil.CHANGE_PASSWORD, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                alertDialog = AppUtil.showNormalDialog(mAcitivty, "Contraseña", "Tu contraseña se ha cambiado");
                alertDialog.show();
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

    private void onSetCamera() {
        if (ContextCompat.checkSelfPermission(mAcitivty, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mAcitivty, new String[] {Manifest.permission.CAMERA}, 10001);
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
                    BannerUtil.onShowWaringAlert(mAcitivty.getContentView(), AppConstant.PERMISSION_DENIED, AppConstant.SHOW_BANNER_TIME);
                }
                return;
            }
        }
    }
}