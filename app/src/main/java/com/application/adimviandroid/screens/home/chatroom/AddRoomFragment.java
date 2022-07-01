package com.application.adimviandroid.screens.home.chatroom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.RoomBGAdapter;
import com.application.adimviandroid.models.RoomBGModel;
import com.application.adimviandroid.models.RoomModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.auth.AuthWebActivity;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddRoomFragment extends Fragment {

    private MainActivity mActivity;
    private int tabIndex;

    private EditText edtRoomTitle;
    private RecyclerView rclRoomBG;
    private CheckBox chkTerms;
    private TextView txtTerms, txtTitle;
    private ImageView imgBack;
    private Button btnAddRoom;

    private int selectedBGIndex = 0;

    private RoomBGAdapter bgAdapter;

    private AlertDialog dialog;

    public AddRoomFragment() {
        // Required empty public constructor
    }

    ActivityResultLauncher<Intent> chatRoomActivityLauch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

    });

    public AddRoomFragment(MainActivity activity, int tabIndex) {
        this.mActivity = activity;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_room, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Chats en vivo");
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        edtRoomTitle = view.findViewById(R.id.edtTitle);
        rclRoomBG = view.findViewById(R.id.rclRoomBG);
        rclRoomBG.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        rclRoomBG.setItemAnimator(new DefaultItemAnimator());

        bgAdapter = new RoomBGAdapter(mActivity, AppConstant.GROUPBG, index -> {
            if (AppConstant.GROUPBG.get(index).isSelected) {
                return;
            } else {
                selectedBGIndex = index;
                for (int i = 0; i < AppConstant.GROUPBG.size(); i++) {
                    if (i == index) {
                        AppConstant.GROUPBG.get(i).isSelected = true;
                    } else {
                        AppConstant.GROUPBG.get(i).isSelected = false;
                    }
                }
                bgAdapter.notifyDataSetChanged();
            }
        });
        rclRoomBG.setAdapter(bgAdapter);

        chkTerms = view.findViewById(R.id.checkTerm);
        txtTerms = view.findViewById(R.id.txtTermsCondition);
        txtTerms.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AuthWebActivity.class);
            intent.putExtra("title", "Información");
            intent.putExtra("url", "https://www.adimvi.com/appAPI/index.php/front/terms");
            mActivity.startActivity(intent);
        });

        btnAddRoom = view.findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(v -> {
            String roomTitle = edtRoomTitle.getText().toString();
            if (roomTitle.isEmpty()) {
                dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Introduce el título de tu directos");
                return;
            }
            if (!chkTerms.isChecked()) {
                dialog = AppUtil.showNormalDialog(mActivity, "Mensaje", "Por favor, lee y verifica los términos y condiciones");
                return;
            }
            Map<String, String> param = new HashMap<>();
            param.put("title", roomTitle);
            param.put("bgIndex", "" + selectedBGIndex);
            param.put("adminID", "" + SharedUtil.getSharedUserID());
            onCallAddRoom(param);
        });
    }

    private void onCallAddRoom(Map<String, String> param) {
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        ApiUtil.onAPIConnectionResponse(ApiUtil.ADD_ROOM, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                AppUtil.onDismissProgressDialog(progressDialog);
                try {
                    int roomID = obj.getInt("response");
                    RoomModel room = new RoomModel();
                    room.roomID = roomID;
                    room.title = edtRoomTitle.getText().toString();
                    room.adminID = SharedUtil.getSharedUserID();
                    room.adminName = SharedUtil.getSharedUserName();
                    room.memberCnt = 1;
                    room.background = AppConstant.GROUPBG.get(selectedBGIndex).bgResID;
                    FireChatUtil.createRoom(room, () -> {
                        Gson gson = new Gson();
                        String strRoom = gson.toJson(room);
                        Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                        intent.putExtra("ROOMMODEL", strRoom);
                        chatRoomActivityLauch.launch(intent);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.INTERNET_ERROR, AppConstant.SHOW_BANNER_TIME);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), AppConstant.SERVER_ERROR, AppConstant.SHOW_BANNER_TIME);
            }
        });
    }
}