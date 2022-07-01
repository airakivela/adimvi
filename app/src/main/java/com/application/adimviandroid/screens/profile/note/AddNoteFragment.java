package com.application.adimviandroid.screens.profile.note;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.NoteModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddNoteFragment extends Fragment {

    private MainActivity mActivity;
    private NoteModel mNote;

    private ImageView imgBack, imgSave, imgRemove;
    private TextView txtTitle;
    private EditText edtTitle, edtDesc;

    private AlertDialog alertDialog;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    public AddNoteFragment(MainActivity mainActivity, NoteModel note) {
        this.mActivity = mainActivity;
        this.mNote = note;
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
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Notas");
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());

        edtTitle = view.findViewById(R.id.edtTitle);
        edtDesc = view.findViewById(R.id.edtDesc);
        if (mNote != null) {
            edtTitle.setText(mNote.title);
            edtDesc.setText(mNote.description);
        }
        imgRemove = view.findViewById(R.id.imgRemove);
        imgRemove.setOnClickListener(v -> mActivity.onBackPressed());
        imgSave = view.findViewById(R.id.imgSave);
        imgSave.setOnClickListener(v -> {
            if (edtDesc.getText().toString().isEmpty()) {
                alertDialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "El contenido está vacío.");
                alertDialog.show();
                return;
            }
            if (edtTitle.getText().toString().isEmpty()) {
                alertDialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "El título está vacío");
                alertDialog.show();
                return;
            }
            Map<String, String> param = new HashMap<>();
            param.put("userid", "" + SharedUtil.getSharedUserID());
            param.put("title", edtTitle.getText().toString());
            param.put("description", edtDesc.getText().toString());
            if (mNote == null) {
                onCallAddNote(param);
            } else {
                onCallEditNote(param);
            }
        });
    }

    private void onCallAddNote(Map<String, String> param) {
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.ADD_NOTE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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

    private void onCallEditNote(Map<String, String> param) {
        param.put("noteId", "" + mNote.noteID);
        ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.EDIT_NOTE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
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