package com.application.adimviandroid.screens.publish;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.AddStoryAdapter;
import com.application.adimviandroid.models.AddStoryModel;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.auth.AuthWebActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStoryFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private int tabIndex;
    private int storyID;

    private EditText edtCategory, edtTag;
    private RecyclerView rclStory;
    private ImageView imgAddStory, imgStoryBefore, imgStoryAfter;
    private TextView txtTerms;
    private CheckBox chkTerms;
    private Button btnPublish, btnDraft;

    private AlertDialog dialog;

    private int categoryID = 0;

    private AddStoryAdapter mStoryAdapter;
    private List<AddStoryModel> mStories = new ArrayList<>(
            Arrays.asList(new AddStoryModel(null, ""))
    );

    public AddStoryFragment() {

    }

    public AddStoryFragment(MainActivity mainActivity, int tabIndex, int storyID) {
        this.mActivity = mainActivity;
        this.tabIndex = tabIndex;
        this.storyID = storyID;
    }

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            mStories.get(((LinearLayoutManager) rclStory.getLayoutManager()).findFirstVisibleItemPosition()).imgFile = new File(result.getUriFilePath(mActivity, true));
            mStoryAdapter.notifyDataSetChanged();
        }
    });

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_stroy, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        edtCategory = view.findViewById(R.id.edtCategory);
        edtTag = view.findViewById(R.id.edtTag);
        edtCategory.setOnClickListener(this);
        rclStory = view.findViewById(R.id.rcl_story);
        rclStory.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rclStory.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rclStory);
        mStoryAdapter = new AddStoryAdapter(mActivity, mStories, () -> {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.CAMERA}, 10001);
                return;
            }
            cropImage.launch(AppUtil.options);
        });
        rclStory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    handleDirectionButtons(currentPosition);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        rclStory.setAdapter(mStoryAdapter);
        imgAddStory = view.findViewById(R.id.imgStoryAdd);
        imgAddStory.setOnClickListener(this);

        imgStoryAfter = view.findViewById(R.id.imgStoryAfter);
        imgStoryAfter.setOnClickListener(this);
        imgStoryBefore = view.findViewById(R.id.imgStoryBefore);
        imgStoryBefore.setOnClickListener(this);

        handleDirectionButtons(0);

        txtTerms = view.findViewById(R.id.txtTermsCondition);
        chkTerms = view.findViewById(R.id.checkbox3);
        btnPublish = view.findViewById(R.id.btnPublication);
        btnDraft = view.findViewById(R.id.btnDraft);
        txtTerms.setOnClickListener(this);
        btnPublish.setOnClickListener(this);
        btnDraft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtCategory:
                onPopCategory();
                break;
            case R.id.imgStoryAdd:
                if (mStories.size() == 7) {
                    BannerUtil.onShowWaringAlert(mActivity.getContentView(), "Maximum story number...", AppConstant.SHOW_BANNER_TIME);
                    return;
                }
                if (mStories.get(mStories.size() - 1).imgFile == null || mStories.get(mStories.size() - 1).content.isEmpty()) {
                    dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Please insert story image and content");
                    return;
                }
                mStories.add(new AddStoryModel(null, ""));
                mStoryAdapter.notifyDataSetChanged();
                rclStory.scrollToPosition(mStories.size() - 1);
                handleDirectionButtons(mStories.size() - 1);
                break;
            case R.id.imgStoryAfter:
                onClickDirection(true);
                break;
            case R.id.imgStoryBefore:
                onClickDirection(false);
                break;
            case R.id.btnPublication:
                onCallPublishStoryAPI("publish_story");
                break;
            case R.id.btnDraft:
                break;
            case R.id.txtTermsCondition:
                Intent intent = new Intent(mActivity, AuthWebActivity.class);
                intent.putExtra("title", "Información");
                intent.putExtra("url", "https://www.adimvi.com/appAPI/index.php/front/terms");
                mActivity.startActivity(intent);
                break;
        }
    }

    private void onCallPublishStoryAPI(String publishType) {
        String tag = edtTag.getText().toString();
        if (tag.isEmpty()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, introduce  etiquetas en tu post.");
            return;
        }
        if (!chkTerms.isChecked()) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, lee y verifica los términos y condiciones");
            return;
        }
        if (categoryID == 0) {
            dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Please select category");
            return;
        }
        List<String> extraStoryContents = new ArrayList<>();
        List<File> extraStoryFiles = new ArrayList<>();
        for (int i = 0; i < mStories.size(); i++) {
            extraStoryFiles.add(mStories.get(i).imgFile);
            extraStoryContents.add(mStories.get(i).content);
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", "" + SharedUtil.getSharedUserID());
        params.put("extraContent", TextUtils.join("////", extraStoryContents));
        params.put("type", publishType);
        params.put("categoryid", "" + categoryID);
        params.put("tags", edtTag.getText().toString().replace(" ", ","));
//        ApiUtil.uploadMultiImages(ApiUtil.PUBLISH_STORY, params, extraStoryFiles.toArray(new File[extraStoryFiles.size()]), new ApiUtil.APIManagerCallback() {
//            @Override
//            public void onEventCallBack(JSONObject obj) {
//
//            }
//
//            @Override
//            public void onEventInternetError(Exception e) {
//
//            }
//
//            @Override
//            public void onEventServerError(Exception e) {
//
//            }
//        });
    }

    private void handleDirectionButtons(int pageIndex) {
        if (mStories.size() == 1) {
            imgStoryBefore.setColorFilter(mActivity.getColor(R.color.lightGray));
            imgStoryAfter.setColorFilter(mActivity.getColor(R.color.lightGray));
            return;
        }
        if (pageIndex == 0) {
            imgStoryBefore.setColorFilter(mActivity.getColor(R.color.lightGray));
            imgStoryAfter.setColorFilter(mActivity.getColor(R.color.black_white));
        } else if (pageIndex == mStories.size() - 1) {
            imgStoryBefore.setColorFilter(mActivity.getColor(R.color.black_white));
            imgStoryAfter.setColorFilter(mActivity.getColor(R.color.lightGray));
        } else {
            imgStoryBefore.setColorFilter(mActivity.getColor(R.color.black_white));
            imgStoryAfter.setColorFilter(mActivity.getColor(R.color.black_white));
        }
    }

    private void onClickDirection(boolean isAfterOrBefore) {
        int currentPosition = ((LinearLayoutManager) rclStory.getLayoutManager()).findFirstVisibleItemPosition();
        if (currentPosition == 0 && !isAfterOrBefore) {
            return;
        }
        if (currentPosition == mStories.size() - 1 && isAfterOrBefore) {
            return;
        }
        if (isAfterOrBefore) {
            rclStory.smoothScrollToPosition(currentPosition + 1);
        } else {
            rclStory.smoothScrollToPosition(currentPosition - 1);
        }
    }

    private void onPopCategory() {
        PopupMenu menu = new PopupMenu(mActivity, edtCategory);
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