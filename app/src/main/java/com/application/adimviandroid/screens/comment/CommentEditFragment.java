package com.application.adimviandroid.screens.comment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.CommentModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommentEditFragment extends Fragment {

    private MainActivity mActivity;
    private CommentModel comment;

    private ImageView imgBack;
    private TextView txtTitle;
    private EditText edtComment;
    private Button btnedit;

    public CommentEditFragment() {
        // Required empty public constructor
    }

    public CommentEditFragment(MainActivity mActivity, CommentModel comment) {
        this.mActivity = mActivity;
        this.comment = comment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (comment == null) {
            comment = new CommentModel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_edit, container, false);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("");
        edtComment = view.findViewById(R.id.edtComment);
        edtComment.setText(Html.fromHtml(comment.comment));
        btnedit = view.findViewById(R.id.btnEdit);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallEditComment();
            }
        });
        return view;
    }

    private void onCallEditComment() {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("postid", "" + comment.postID);
        param.put("comment", edtComment.getText().toString());
        param.put("type", "A");

        ApiUtil.onAPIConnectionResponse(ApiUtil.EDIT_COMMENT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                dialog.dismiss();
                mActivity.onBackPressed();
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