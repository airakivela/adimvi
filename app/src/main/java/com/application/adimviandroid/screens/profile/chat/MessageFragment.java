package com.application.adimviandroid.screens.profile.chat;

import android.Manifest;
import android.app.AlertDialog;
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
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.MessageAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.models.MessageModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageFragment extends Fragment {

    private MainActivity mActivity;
    private ChatModel targetUser;
    private int tabIndex;

    private EditText edtMessage;
    private RecyclerView rclMessage;
    private ImageView imgCamera, imgSend, imgBack, imgUser, imgVerify;
    private TextView txtTitle, txtTyping, txtOnline;
    private LinearLayout lltUser;

    private File mUploadFile;
    private List<MessageModel> mMessages = new ArrayList<>();
    private MessageAdapter mAdapter;

    private ImageDialog imgDialog;
    private AlertDialog alertDialog;

    //typing indicator
    private final long delay = 5000;
    private long lastTextEdit = 0;
    private final Handler handler = new Handler();
    private boolean isAlreadyTyping = false;
    private int isTargetUserON = 0;

    private final Runnable inputFinishChecker = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() > (lastTextEdit + delay)) {
                isAlreadyTyping = false;
                FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
            }
        }
    };

    private final ValueEventListener observePrivateMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            initData();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener typingIndicatorListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean isTypeing = false;
            if (!snapshot.exists()) {
                isTypeing = false;
            } else {
                isTypeing = snapshot.getValue(Boolean.class);
            }
            handleTypingIndicator(isTypeing);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener targetUserOnlineListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean targetUserON = false;
            if (!snapshot.exists()) {
                targetUserON =  false;
            } else {
                targetUserON = snapshot.getValue(Boolean.class);
            }
            handleTargetUserON(targetUserON);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            AppUtil.isCameraOn = false;
            FireChatUtil.setPrivateMessaageChannelUserIsON(targetUser.userID);
            if (result != null) {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    imgCamera.setImageURI(null);
                    imgCamera.setImageURI(result.getUriContent());
                    mUploadFile = new File(result.getUriFilePath(mActivity, true));
                }
            }
        }
    });

    public MessageFragment() {
        // Required empty public constructor
    }

    public MessageFragment(MainActivity mainActivity, ChatModel user, int tabIndex) {
        this.mActivity = mainActivity;
        this.targetUser = user;
        this.tabIndex = tabIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
        if (targetUser == null) {
            targetUser = new ChatModel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (imgDialog != null && imgDialog.isShowing()) {
            imgDialog.dismiss();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
        FireChatUtil.removePrivateMessageChanelIsON(targetUser.userID);
    }

    @Override
    public void onResume() {
        super.onResume();
        FireChatUtil.privateMsgRef(targetUser.userID).child("privateMessageID").addValueEventListener(observePrivateMessageListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("typingIndicator").child(String.valueOf(targetUser.userID)).addValueEventListener(typingIndicatorListener);
        FireChatUtil.setPrivateMessaageChannelUserIsON(targetUser.userID);
        FireChatUtil.privateMsgRef(targetUser.userID).child("isChanelOn").child(String.valueOf(targetUser.userID)).addValueEventListener(targetUserOnlineListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
        FireChatUtil.removePrivateMessageChanelIsON(targetUser.userID);
        FireChatUtil.privateMsgRef(targetUser.userID).child("privateMessageID").removeEventListener(observePrivateMessageListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("typingIndicator").child(String.valueOf(targetUser.userID)).removeEventListener(typingIndicatorListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("isChanelOn").child(String.valueOf(targetUser.userID)).removeEventListener(targetUserOnlineListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
        FireChatUtil.removePrivateMessageChanelIsON(targetUser.userID);
        FireChatUtil.privateMsgRef(targetUser.userID).child("privateMessageID").removeEventListener(observePrivateMessageListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("typingIndicator").child(String.valueOf(targetUser.userID)).removeEventListener(typingIndicatorListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("isChanelOn").child(String.valueOf(targetUser.userID)).removeEventListener(targetUserOnlineListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        initUIView(view);
        initFBMessage();
        return view;
    }

    private void handleTypingIndicator(boolean isTyping) {
        if (isTyping) {
            txtTyping.setVisibility(View.VISIBLE);
            txtOnline.setVisibility(View.GONE);
        } else {
            txtTyping.setVisibility(View.GONE);
            if (isTargetUserON == 1) {
                txtOnline.setVisibility(View.VISIBLE);
            } else {
                txtOnline.setVisibility(View.GONE);
            }
        }
    }

    private void handleTargetUserON(boolean targetUserON) {
        isTargetUserON = targetUserON ? 1 : 0;
        txtOnline.setVisibility(targetUserON ? View.VISIBLE : View.GONE);
    }

    private void initFBMessage() {
        FireChatUtil.removeOthersPrivateMessageTypingIndicator(targetUser.userID);
        FireChatUtil.privateMsgRef(targetUser.userID).child("privateMessageID").addValueEventListener(observePrivateMessageListener);
        FireChatUtil.privateMsgRef(targetUser.userID).child("typingIndicator").child(String.valueOf(targetUser.userID)).addValueEventListener(typingIndicatorListener);
        FireChatUtil.setPrivateMessaageChannelUserIsON(targetUser.userID);
        FireChatUtil.privateMsgRef(targetUser.userID).child("isChanelOn").child(String.valueOf(targetUser.userID)).addValueEventListener(targetUserOnlineListener);
    }

    private void initData() {
        Map<String, String> param = new HashMap<>();
        param.put("fromuserid", "" + SharedUtil.getSharedUserID());
        param.put("touserid", "" + targetUser.userID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_MESSAGES, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    mMessages.clear();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray messages = response.getJSONArray("message");
                    for (int i = 0; i < messages.length(); i++) {
                        MessageModel message = new MessageModel();
                        message.initWithJSON(messages.getJSONObject(i));
                        mMessages.add(message);
                    }
                    new Handler().postDelayed(() -> rclMessage.scrollToPosition(0), 200);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void initUIView(View view) {
        rclMessage = view.findViewById(R.id.rclMessage);
        rclMessage.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager rclLayoutManager = new LinearLayoutManager(mActivity);
        rclLayoutManager.setStackFromEnd(false);
        rclLayoutManager.setReverseLayout(true);
        rclMessage.setLayoutManager(rclLayoutManager);
        mAdapter = new MessageAdapter(mActivity, mMessages, bitmap -> {
            if(bitmap == null) {
                alertDialog = AppUtil.showNormalDialog(mActivity, "Attention", "Image not ready");
            } else {
                imgDialog = new ImageDialog(mActivity, bitmap);
                imgDialog.show();
            }
        });
        rclMessage.setAdapter(mAdapter);

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("" + targetUser.userName);
        txtTyping = view.findViewById(R.id.txtTyping);
        txtOnline = view.findViewById(R.id.txtOnline);
        imgUser = view.findViewById(R.id.imgUser);
        if (targetUser.imgAvatar.isEmpty()) {
            imgUser.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrl(mActivity, imgUser, targetUser.imgAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        imgVerify = view.findViewById(R.id.imgVerify);
        imgVerify.setVisibility(targetUser.verified == 1 ? View.VISIBLE : View.GONE);

        lltUser = view.findViewById(R.id.lltUser);
        lltUser.setOnClickListener(v -> mActivity.addFragment(new ProfileFragment(mActivity, tabIndex, targetUser.userID, null, null, false), tabIndex));

        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        imgCamera = view.findViewById(R.id.imgCameraMessage);
        imgSend = view.findViewById(R.id.imgSend);
        imgCamera.setOnClickListener(v -> {
            AppUtil.isCameraOn = true;
            onSetCamera();
        });
        imgSend.setOnClickListener(v -> {
            String strMessage = edtMessage.getText().toString();
            if (mUploadFile == null && strMessage.isEmpty()) {
                return;
            } else {
                if (mUploadFile != null) {
                    uploadImageMessage(strMessage);
                } else {
                    uploadTextMessage(strMessage);
                }
            }
        });
        edtMessage = view.findViewById(R.id.edtMessage);
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(inputFinishChecker);
                if (isAlreadyTyping) {
                    return;
                } else {
                    FireChatUtil.setPrivateMessageTypingIndicator(targetUser.userID, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                } else {
                    FireChatUtil.removePrivateMessageTypingIndicator(targetUser.userID);
                }
            }
        });
    }

    private void uploadTextMessage(String strMessage) {
        Map<String, String> param = new HashMap<>();
        param.put("fromuserid","" + SharedUtil.getSharedUserID());
        param.put("touserid","" + targetUser.userID);
        param.put("content", strMessage);
        param.put("image_name", "");
        param.put("isON", "" + isTargetUserON);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SEND_MESSAGES, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                reloadUI();
            }

            @Override
            public void onEventInternetError(Exception e) {
                Log.d("error", e.getMessage());
            }

            @Override
            public void onEventServerError(Exception e) {
                Log.d("error", e.getMessage());
            }
        });
    }

    private void uploadImageMessage(String strMessage) {
        Map<String, String> param = new HashMap<>();
        param.put("fromuserid","" + SharedUtil.getSharedUserID());
        param.put("touserid","" + targetUser.userID);
        param.put("content", strMessage);
        param.put("isON", "" + isTargetUserON);
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.SEND_MESSAGES, param, "image_name", mUploadFile, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                reloadUI();
            }

            @Override
            public void onEventInternetError(Exception e) {
                Log.d("error", e.getMessage());
            }

            @Override
            public void onEventServerError(Exception e) {
                Log.d("error", e.getMessage());
            }
        });
    }

    private void reloadUI() {
        mUploadFile = null;
        imgCamera.setImageResource(R.drawable.ic_camera_outline);
        edtMessage.setText("");
        FireChatUtil.addPrivateMessage(targetUser.userID);
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

    public ChatModel getChatModel() {
        return this.targetUser;
    }

}