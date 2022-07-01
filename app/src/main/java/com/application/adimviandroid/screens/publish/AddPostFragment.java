package com.application.adimviandroid.screens.publish;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.DraftPostAdapter;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.DraftPostModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.auth.AuthWebActivity;
import com.application.adimviandroid.screens.post.PostDetailFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.AddPostDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.canhub.cropper.*;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.onecode369.wysiwyg.WYSIWYG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddPostFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private int postID;
    private int tabIndex;

    private ImageView imgUpload, imgLogo, imgBold, imgItalic, imgUnderline, imgAlignLeft, imgAlignRight, imgAlignCenter,
            imgCamera, imgCheck1Info, imgCheck2Info, imgTemp;
    private TextView txtTermsCondition, txtImage, txtVideo;
    private NestedScrollView nstContainer;
    private ShimmerFrameLayout shimer;
    private LinearLayout lltDraft, lltImage, lltVideo, lltVideoData, lltPrice;
    private RecyclerView rclDraft;
    private EditText edtTitle, edtCategory, edtVideoUrl, edtTag, edtPrice;
    private Button btnSelectImage, btnPublish, btnDraft;
    private WYSIWYG richEditor;
    private CheckBox chk1, chk2, chk3;
    private RadioGroup radioGroup;

    private AlertDialog dialog;
    private AddPostDialog addPostDialog;
    private List<String> fontType = new ArrayList<>();

    private boolean isPostImage = true;
    private File uploadImage;
    private List<DraftPostModel> mDrafts = new ArrayList<>();
    private DraftPostAdapter mAdapter;

    private int uploadType = 1;
    private String uploadTypeDraftList = "";
    private String type = "publish";
    private String draftType = "draft_edit";
    private String updatePostType = "";
    private int draftPostID = 0;
    private int categoryID = 0;
    private int notify = 0;

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            if (result != null) {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    if (isPostImage) {
                        imgUpload.setImageURI(null);
                        imgUpload.setImageURI(result.getUriContent());
                        imgLogo.setVisibility(View.GONE);
                        uploadImage = new File(result.getUriFilePath(mActivity, true));
                    } else {
                        File file = new File(result.getUriFilePath(mActivity, true));
                        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.UPLOAD_POST_CONTENT_IMAGE, null, "imageUrl", file, new ApiUtil.APIManagerCallback() {
                            @Override
                            public void onEventCallBack(JSONObject obj) {
                                dialog.dismiss();
                                try {
                                    String url = obj.getString("url");
                                    if (!richEditor.hasFocus()) {
                                        richEditor.requestFocus();
                                    }
                                    richEditor.insertImage(url, "postContentImage");
                                    imgTemp.setImageURI(null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
            }
        }
    });

    public AddPostFragment() {

    }

    public AddPostFragment(MainActivity mainActivity, int postID, int tabIndex) {
        this.mActivity = mainActivity;
        this.postID = postID;
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
        if (addPostDialog != null && addPostDialog.isShowing()) {
            addPostDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        initUIView(view);
        initDraftList();
        if (postID != 0) {
            draftType = "publish_edit";
            updatePostType = "publish";
            draftPostID = postID;
            Map<String, String> param = new HashMap<>();
            param.put("postid", "" + postID);
            param.put("type", "publish");
            initDraftPostData(param);
        }
        return view;
    }

    private void initDraftList() {
        nstContainer.setVisibility(View.GONE);
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        mDrafts.clear();
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_DRAFT_POST_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                nstContainer.setVisibility(View.VISIBLE);
                try {
                    JSONArray draftArr = obj.getJSONArray("draftPost");
                    if (draftArr.length() == 0) {
                        lltDraft.setVisibility(View.GONE);
                    } else {
                        lltDraft.setVisibility(View.VISIBLE);
                        for (int i = 0; i < draftArr.length(); i++) {
                            DraftPostModel draft = new DraftPostModel();
                            draft.initWithJSON(draftArr.getJSONObject(i));
                            mDrafts.add(draft);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                nstContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.stopShimmer();
                shimer.setVisibility(View.GONE);
                nstContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initDraftPostData(Map<String, String> param) {
        richEditor.setHtml("");
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_DRAFT_POST_DETAIL, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONObject draftPost = response.getJSONObject("drafPost");
                    edtTitle.setText(draftPost.getString("post_title"));
                    edtCategory.setText(draftPost.getString("category_name"));
                    categoryID = draftPost.getInt("categoryid");
                    if (draftPost.getString("file_type").equals("Video")) {
                        uploadTypeDraftList = "Video";
                        onHandleImageVideo(1);
                        AppUtil.loadImageByUrl(mActivity, imgUpload, draftPost.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
                        edtVideoUrl.setText(draftPost.getString("post_image"));
                        imgLogo.setVisibility(View.GONE);
                        uploadType = 2;
                    } else {
                        uploadTypeDraftList = "Image";
                        onHandleImageVideo(0);
                        AppUtil.loadImageByUrl(mActivity, imgUpload, draftPost.getString("post_image"), ImagePlaceHolderType.POSTIMAGE);
                        imgLogo.setVisibility(View.GONE);
                        uploadType = 1;
                    }
                    if (draftPost.getInt("pricer") == 1) {
                        radioGroup.check(R.id.radio2);
                    } else {
                        radioGroup.check(R.id.radio1);
                    }
                    if (draftPost.getInt("notify") == 1) {
                        notify = 1;
//                        chk3.setChecked(true);
                    } else {
//                        chk3.setChecked(false);
                        notify = 0;
                    }
                    edtTag.setText(draftPost.getString("tags"));
                    richEditor.setHtml(draftPost.getString("post_description"));
                    edtPrice.setText(draftPost.getString("price"));
                    if (draftPost.getInt("promotional_image") == 1) {
                        chk2.setChecked(true);
                    } else {
                        chk2.setChecked(false);
                    }
                    if (draftPost.getInt("adimvi_promotions") == 1) {
                        chk1.setChecked(false);
                    } else {
                        chk1.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    private void initUIView(View view) {

        txtImage = view.findViewById(R.id.txtImage);
        txtVideo = view.findViewById(R.id.txtVideo);

        imgUpload = view.findViewById(R.id.imgUpload);
        imgLogo = view.findViewById(R.id.imgLogo);
        imgBold = view.findViewById(R.id.imgBold);
        imgItalic = view.findViewById(R.id.imgItalic);
        imgUnderline = view.findViewById(R.id.imgUnderline);
        imgAlignLeft = view.findViewById(R.id.imgLeft);
        imgAlignRight = view.findViewById(R.id.imgRight);
        imgAlignCenter = view.findViewById(R.id.imgCenter);
        imgCamera = view.findViewById(R.id.imgCamera);
        imgCheck1Info = view.findViewById(R.id.imgCheck1Info);
        imgCheck2Info = view.findViewById(R.id.imgCheck2Info);
        imgTemp = view.findViewById(R.id.imgTemp);

        txtTermsCondition = view.findViewById(R.id.txtTermsCondition);

        nstContainer = view.findViewById(R.id.nstPostContainer);

        lltDraft = view.findViewById(R.id.lltDraft);
        lltImage = view.findViewById(R.id.lltImage);
        lltVideo = view.findViewById(R.id.lltVideo);
        lltVideoData = view.findViewById(R.id.lltVideoData);
        lltPrice = view.findViewById(R.id.lltPrice);

        rclDraft = view.findViewById(R.id.rclDraft);
        rclDraft.setLayoutManager(new LinearLayoutManager(mActivity));
        rclDraft.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DraftPostAdapter(mActivity, mDrafts, id -> {
            updatePostType = "draft";
            postID = id;
            draftPostID = id;
            Map<String, String> param = new HashMap<>();
            param.put("postid", "" + id);
            param.put("type", "draft");
            initDraftPostData(param);
        });
        rclDraft.setAdapter(mAdapter);
        shimer = view.findViewById(R.id.shimer);

        edtTitle = view.findViewById(R.id.edtTitle);
        edtCategory = view.findViewById(R.id.edtCategory);
        edtVideoUrl = view.findViewById(R.id.edtVideoUrl);
        edtTag = view.findViewById(R.id.edtTag);
        edtPrice = view.findViewById(R.id.edtPrice);

        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnPublish = view.findViewById(R.id.btnPublication);
        btnDraft = view.findViewById(R.id.btnDraft);

        richEditor = view.findViewById(R.id.richEditor);
        AppUtil.setWebViewThemeMode(mActivity, richEditor);
        richEditor.setPadding(4, 4, 4, 4);
        richEditor.setPlaceholder("Empieza a escribir aquí");

        chk1 = view.findViewById(R.id.checkbox1);
        chk2 = view.findViewById(R.id.checkbox2);
        chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chk1.setChecked(isChecked);
            }
        });
        int i = SharedUtil.getSharedUserAD();
        chk2.setClickable(SharedUtil.getSharedUserAD() == 1);
        chk3 = view.findViewById(R.id.checkbox3);

        lltImage.setOnClickListener(this);
        lltVideo.setOnClickListener(this);
        edtCategory.setOnClickListener(this);
        btnSelectImage.setOnClickListener(this);
        imgCheck1Info.setOnClickListener(this);
        imgCheck2Info.setOnClickListener(this);
        txtTermsCondition.setOnClickListener(this);
        imgBold.setOnClickListener(this);
        imgItalic.setOnClickListener(this);
        imgUnderline.setOnClickListener(this);
        imgAlignLeft.setOnClickListener(this);
        imgAlignCenter.setOnClickListener(this);
        imgAlignRight.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
        btnDraft.setOnClickListener(this);
        btnPublish.setOnClickListener(this);

        radioGroup = view.findViewById(R.id.radioGroup);
        if (postID == 0) {
            radioGroup.check(R.id.radio1);
            lltPrice.setVisibility(View.GONE);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio1) {
                lltPrice.setVisibility(View.GONE);
            } else {
                lltPrice.setVisibility(View.VISIBLE);
            }
        });

        onHandleImageVideo(0);
    }

    private void onHandleImageVideo(int type) {
        if (type == 0) {
            lltImage.setBackgroundResource(R.drawable.round_lightgray6);
            lltVideo.setBackgroundResource(0);
            btnSelectImage.setVisibility(View.VISIBLE);
            lltVideoData.setVisibility(View.GONE);
            txtImage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            txtVideo.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            lltImage.setBackgroundResource(0);
            lltVideo.setBackgroundResource(R.drawable.round_lightgray6);
            btnSelectImage.setVisibility(View.GONE);
            lltVideoData.setVisibility(View.VISIBLE);
            txtImage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            txtVideo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lltImage:
                uploadType = 1;
                onHandleImageVideo(0);
                break;
            case R.id.lltVideo:
                uploadType = 2;
                onHandleImageVideo(1);
                break;
            case R.id.edtCategory:
                onPopCategory();
                break;
            case R.id.btnSelectImage:
                isPostImage = true;
                onSelectImage();
                break;
            case R.id.imgCheck1Info:
                onPopNormalAlert(R.string.check1content);
                break;
            case R.id.imgCheck2Info:
                onPopNormalAlert(R.string.check2content);
                break;
            case R.id.txtTermsCondition:
                Intent intent = new Intent(mActivity, AuthWebActivity.class);
                intent.putExtra("title", "Información");
                intent.putExtra("url", "https://www.adimvi.com/appAPI/index.php/front/terms");
                mActivity.startActivity(intent);
                break;
            case R.id.imgBold:
                richEditor.setBold();
                imgBold.setColorFilter(mActivity.getColor(onHandlefontType(String.valueOf(R.id.imgBold)) ? R.color.black_white : R.color.lightGray));
                break;
            case R.id.imgItalic:
                richEditor.setItalic();
                imgItalic.setColorFilter(mActivity.getColor(onHandlefontType(String.valueOf(R.id.imgItalic)) ? R.color.black_white : R.color.lightGray));
                break;
            case R.id.imgUnderline:
                richEditor.setUnderline();
                imgUnderline.setColorFilter(mActivity.getColor(onHandlefontType(String.valueOf(R.id.imgUnderline)) ? R.color.black_white : R.color.lightGray));
                break;
            case R.id.imgLeft:
                richEditor.setAlignLeft();
                imgAlignLeft.setColorFilter(mActivity.getColor(R.color.black_white));
                imgAlignRight.setColorFilter(mActivity.getColor(R.color.tagColor));
                imgAlignCenter.setColorFilter(mActivity.getColor(R.color.tagColor));
                break;
            case R.id.imgRight:
                richEditor.setAlignRight();
                imgAlignLeft.setColorFilter(mActivity.getColor(R.color.tagColor));
                imgAlignRight.setColorFilter(mActivity.getColor(R.color.black_white));
                imgAlignCenter.setColorFilter(mActivity.getColor(R.color.tagColor));
                break;
            case R.id.imgCenter:
                richEditor.setAlignCenter();
                imgAlignLeft.setColorFilter(mActivity.getColor(R.color.tagColor));
                imgAlignRight.setColorFilter(mActivity.getColor(R.color.tagColor));
                imgAlignCenter.setColorFilter(mActivity.getColor(R.color.black_white));
                break;
            case R.id.imgCamera:
                isPostImage = false;
                onSelectImage();
                break;
            case R.id.btnPublication:
                onPostPublish();
                break;
            case R.id.btnDraft:
                onPostDraft();
                break;
        }
    }

    private void onPostPublish() {
        String title = edtTitle.getText().toString();
        String content = richEditor.getHtml();
        String tag = edtTag.getText().toString();
        if (title.isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Introduce el título de tu post");
            return;
        }
        if (content.isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Tu comentario se ha añadido.");
            return;
        }
        if (tag.isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, introduce  etiquetas en tu post.");
            return;
        }
        if (!chk3.isChecked()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, lee y verifica los términos y condiciones");
            return;
        }
        type = "publish";
        if (uploadTypeDraftList.isEmpty()) {
            if (uploadType == 1) {
                onCallUploadingImage();
            }
            if (uploadType == 2) {
                onCallUploadingVideo();
            }
        } else {
            draftType = "publish_edit";
            if (uploadType == 2) {
                onCallUploadVideoInDraftPost();
            }
            if (uploadType == 1) {
                onCallUploadingImageInDraft();
            }
        }
    }

    private void onPostDraft() {
        type = "draft";
        if (uploadTypeDraftList.isEmpty()) {
            if (uploadType == 1) {
                onCallUploadingImage();
            }
            if (uploadType == 2) {
                onCallUploadingVideo();
            }
        } else {
            if (uploadType == 2) {
                draftType = "draft_update";
                onCallUploadVideoInDraftPost();
            }
            if (uploadType == 1) {
                draftType = "draft_update";
                onCallUploadingImageInDraft();
            }
        }


    }

    private void onCallUploadingImage() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("title", edtTitle.getText().toString());
        param.put("categoryid", "" + categoryID);
        param.put("tags", edtTag.getText().toString().replace(" ", ","));
        param.put("description", richEditor.getHtml());
        param.put("notify", "" + notify);
        param.put("userad", chk2.isChecked() ? "1" : "0");
        param.put("adimviad", chk1.isChecked() ? "1" : "0");
        param.put("price", edtPrice.getText().toString());
        param.put("type", type);
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.ADD_NEW_POST, param, "imageUrl[]", uploadImage, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                handleUV();
                if (type.equals("publish")) {
                    try {
                        int postID = obj.getInt("postID");
                        addPostDialog = new AddPostDialog(mActivity, () -> mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex));
                        addPostDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Publicacion guardada en borrador.");
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void onCallUploadingVideo() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("title", edtTitle.getText().toString());
        param.put("categoryid", "" + categoryID);
        param.put("tags", edtTag.getText().toString().replace(" ", ","));
        param.put("description", richEditor.getHtml());
        param.put("notify", "" + notify);
        param.put("userad", chk2.isChecked() ? "1" : "0");
        param.put("adimviad", chk1.isChecked() ? "1" : "0");
        param.put("price", edtPrice.getText().toString());
        param.put("type", type);
        param.put("videoUrl", edtVideoUrl.getText().toString());
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.ADD_NEW_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                handleUV();
                if (type.equals("publish")) {
                    try {
                        int postID = obj.getInt("postID");
                        dialog = AppUtil.showPostDialog(mActivity, "Mensaje", "¡Tu publicación se ha subido con éxito! Ve y échale un vistazo.", () -> {
                            mActivity.addFragment(new PostDetailFragment(mActivity, postID, tabIndex), tabIndex);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Publicacion guardada en borrador.");
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void onCallUploadingImageInDraft() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("title", edtTitle.getText().toString());
        param.put("categoryid", "" + categoryID);
        param.put("tags", edtTag.getText().toString().replace(" ", ","));
        param.put("description", richEditor.getHtml());
        param.put("notify", "" + notify);
        param.put("userad", chk2.isChecked() ? "1" : "0");
        param.put("adimviad", chk1.isChecked() ? "1" : "0");
        param.put("price", edtPrice.getText().toString());
        param.put("type", draftType);
        param.put("postid", "" + draftPostID);
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.UPDATE_POST, param, "imageUrl[]", uploadImage, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                handleUV();
                progressDialog.dismiss();
                if (type.equals("publish")) {
                    dialog = AppUtil.showPostDialog(mActivity, "Mensaje", "¡Tu publicación se ha subido con éxito! Ve y échale un vistazo.", () -> {
                        mActivity.addFragment(new PostDetailFragment(mActivity, draftPostID, tabIndex), tabIndex);
                    });
                }else {
                    dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Publicacion guardada en borrador.");
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void onCallUploadVideoInDraftPost() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        param.put("title", edtTitle.getText().toString());
        param.put("categoryid", "" + categoryID);
        param.put("tags", edtTag.getText().toString().replace(" ", ","));
        param.put("description", richEditor.getHtml());
        param.put("notify", "" + notify);
        param.put("userad", chk2.isChecked() ? "1" : "0");
        param.put("adimviad", chk1.isChecked() ? "1" : "0");
        param.put("price", edtPrice.getText().toString());
        param.put("type", draftType);
        param.put("videoUrl", edtVideoUrl.getText().toString());
        param.put("postid", "" + draftPostID);
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.UPDATE_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                progressDialog.dismiss();
                handleUV();
                if (type.equals("publish")) {
                    dialog = AppUtil.showPostDialog(mActivity, "Mensaje", "¡Tu publicación se ha subido con éxito! Ve y échale un vistazo.", () -> {
                        mActivity.addFragment(new PostDetailFragment(mActivity, draftPostID, tabIndex), tabIndex);
                    });
                }else {
                    dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Publicacion guardada en borrador.");
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void handleUV() {
        edtTitle.setText("");
        edtCategory.setText("");
        edtVideoUrl.setText("");
        edtPrice.setText("");
        edtTag.setText("");
        chk1.setChecked(false);
        chk2.setChecked(false);
        chk3.setChecked(false);
        radioGroup.check(R.id.radio1);
        richEditor.setHtml("");
        imgUpload.setImageResource(0);
        imgLogo.setVisibility(View.VISIBLE);
        initDraftList();
    }

    private boolean onHandlefontType(String id) {
        if (fontType.isEmpty()) {
            fontType.add(id);
        } else {
            if (fontType.contains(id)) {
                fontType.remove(id);
            } else {
                fontType.add(id);
            }
        }
        if (fontType.contains(id)) {
            return true;
        } else {
            return false;
        }
    }

    private void onPopCategory() {
        PopupMenu menu = new PopupMenu(mActivity, edtTitle);
        for (CategoryModel categoryModel: AppUtil.gCategories) {
            menu.getMenu().add(categoryModel.title);
        }
        menu.show();
        menu.setOnMenuItemClickListener(item -> {
            edtCategory.setText(item.getTitle());
            for (CategoryModel model: AppUtil.gCategories) {
                if (model.title.equals(item.getTitle())) {
                    categoryID = model.categoryID;
                    break;
                }
            }
            return true;
        });
    }

    private void onPopNormalAlert(int strID) {
        dialog = AppUtil.showNormalDialog(mActivity, "Información", mActivity.getString(strID));
    }

    private void onSelectImage() {
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
}