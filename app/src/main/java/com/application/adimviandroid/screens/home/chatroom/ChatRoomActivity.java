package com.application.adimviandroid.screens.home.chatroom;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.RoomAdminMessageAdapter;
import com.application.adimviandroid.adapter.RoomOtherMessageAdapter;
import com.application.adimviandroid.models.RoomMessageModel;
import com.application.adimviandroid.models.RoomModel;
import com.application.adimviandroid.types.ChatRoomActivityResultCode;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.ui.AnimButton;
import com.application.adimviandroid.ui.ImageDialog;
import com.application.adimviandroid.ui.VoicePlayView;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.DirManager;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.application.adimviandroid.utils.StringUtil;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private RoomModel room;

    private ImageView imgRoomAdmin, imgAdminVerify, imgCamera, imgBG, imgExtra;
    private EditText edtMessage;
    private RecyclerView rclAdmin, rclOther;
    private TextView txtAdmin, txtTyping, txtMemeberCnt;
    private CardView crdClose, crdExtra;
    private ConstraintLayout csSend;
    private RecordView recordView;
    private AnimButton recordButton;
    private VoicePlayView voicePlayView;
    private RelativeLayout rltContainer;

    private File mUpload;
    private final List<RoomMessageModel> adminMessages = new ArrayList<>();
    private final List<RoomMessageModel> otherMessages = new ArrayList<>();
    private RoomAdminMessageAdapter adminMessageAdapter;
    private RoomOtherMessageAdapter otherMessageAdapter;

    private AlertDialog dialog;
    private ImageDialog imgDialog;

    private final long delay = 5000;
    private long lastTextEdit = 0;
    private final Handler handler = new Handler();
    private boolean isAlreadyTyping = false;

//    private Recorder recorder;
    private File recordFile;
    private MediaRecorder recorder;

    private boolean isCloseLeaveByUser = false;

    public boolean isShowVoiceView = false;

    private final Runnable inputFinishChecker = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() > (lastTextEdit + delay)) {
                if (room.adminID == SharedUtil.getSharedUserID()) {
                    FireChatUtil.removeAdminTyping(room);
                    isAlreadyTyping = false;
                }
            }
        }
    };

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            AppUtil.isCameraOn = false;
            if (room.adminID == SharedUtil.getSharedUserID()) {
                FireChatUtil.setAdminTyping(room);
            }
            if (result != null) {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    imgExtra.setImageURI(null);
                    imgExtra.setImageURI(result.getUriContent());
                    imgCamera.setVisibility(View.GONE);
                    imgExtra.setVisibility(View.VISIBLE);
                    mUpload = new File(result.getUriFilePath(ChatRoomActivity.this, true));
                }
            }
        }
    });

    /// firebase value event listener  ///
    private final ValueEventListener observeCloseRoomListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.exists()) {
                Intent returnIntent = new Intent();
                setResult(ChatRoomActivityResultCode.ROOMCLOSE.resultCode, returnIntent);
                finish();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener adminTypingListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean isTyping = false;
            if (!snapshot.exists()) {
                isTyping = false;
            } else {
                isTyping = snapshot.getValue(boolean.class);
            }
            handleTypingIndicator(isTyping);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener observeMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            new Handler().postDelayed(ChatRoomActivity.this::initRoomMessages, 2000);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void handleTypingIndicator(boolean isTyping) {
        if (room.adminID == SharedUtil.getSharedUserID()) {
            txtTyping.setVisibility(View.GONE);
        } else {
            txtTyping.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (imgDialog != null && imgDialog.isShowing()) {
            imgDialog.dismiss();
        }
        if (!isCloseLeaveByUser) {
            if (room.adminID == SharedUtil.getSharedUserID()) {
                closeRoom();
            } else {
                leaveRoom(false);
            }
        }

        FireChatUtil.mFireDBRoomIDS.child(String.valueOf(room.roomID)).removeEventListener(observeCloseRoomListener);
        FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("isTyping").removeEventListener(adminTypingListener);
        FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("roomMessages").removeEventListener(observeMessageListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Gson gson = new Gson();
        room = gson.fromJson(getIntent().getStringExtra("ROOMMODEL"), RoomModel.class);

        if (!room.title.isEmpty()) {
            dialog = AppUtil.showNormalDialog(this, "Tema de la sala", room.title);
        }

        initFire();
        initUIView();
    }

    private void initFire() {
        FireChatUtil.mFireDBRoomIDS.child(String.valueOf(room.roomID)).addValueEventListener(observeCloseRoomListener);
        FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("isTyping").addValueEventListener(adminTypingListener);
        FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("roomMessages").addValueEventListener(observeMessageListener);
    }

    private void initUIView() {
        rltContainer = findViewById(R.id.rltContainer);
        imgBG = findViewById(R.id.imgBG);
        imgRoomAdmin = findViewById(R.id.imgAdminUser);
        imgAdminVerify = findViewById(R.id.imgAdminVerify);
        crdClose = findViewById(R.id.crdClose);
        crdClose.setOnClickListener(this);
        imgCamera = findViewById(R.id.imgCamera);
        imgExtra = findViewById(R.id.imgExtra);
        crdExtra = findViewById(R.id.crdCamera);
        imgExtra.setVisibility(View.GONE);
        imgCamera.setVisibility(View.VISIBLE);
        crdExtra.setOnClickListener(this);

        txtAdmin = findViewById(R.id.txtAdminName);
        txtTyping = findViewById(R.id.txtTyping);
        txtMemeberCnt = findViewById(R.id.txtMemberCnt);

        voicePlayView = findViewById(R.id.voicePlayView);

        rclAdmin = findViewById(R.id.rclAdminChat);
        rclAdmin.setItemAnimator(new DefaultItemAnimator());
        rclAdmin.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
        adminMessageAdapter = new RoomAdminMessageAdapter(this, adminMessages, new RoomAdminMessageAdapter.RoomAdminListener() {
            @Override
            public void onClickMessageExtra(Bitmap bitmap) {
                imgDialog = new ImageDialog(ChatRoomActivity.this, bitmap);
                imgDialog.show();
            }

            @Override
            public void onClickVoiceCell(RoomMessageModel messageModel) {
                isShowVoiceView = true;
                onShowVoicePlayerView(messageModel);
            }
        });
        rclAdmin.setAdapter(adminMessageAdapter);

        rclOther = findViewById(R.id.rclOtherChat);
        rclOther.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
        rclOther.setItemAnimator(new DefaultItemAnimator());
        otherMessageAdapter = new RoomOtherMessageAdapter(this, otherMessages, new RoomOtherMessageAdapter.RoomOtheerMessageListener() {
            @Override
            public void onClickExtra(Bitmap bitmap) {
                imgDialog = new ImageDialog(ChatRoomActivity.this, bitmap);
                imgDialog.show();
            }

            @Override
            public void onClickVoiceCell(RoomMessageModel model) {
                isShowVoiceView = true;
                onShowVoicePlayerView(model);
            }
        });
        rclOther.setAdapter(otherMessageAdapter);

        loadUIWithRoom(room);

        csSend = findViewById(R.id.csSend);
        recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);
        recordView.setCancelBounds(0);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);

        recordView.setTimeLimit(60000);
        recordButton.setOnRecordClickListener(v -> sendRoomMessage());
        recordView.setRecordPermissionHandler(() -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(ChatRoomActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
            if (recordPermissionAvailable) {
                return true;
            }
            ActivityCompat.
                    requestPermissions(ChatRoomActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            0);

            return false;
        });
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                hideOrShowRecord(false);
                handleRecord();
                if (room.adminID == SharedUtil.getSharedUserID()) {
                    handler.removeCallbacks(inputFinishChecker);
                    if (isAlreadyTyping) {
                        return;
                    } else {
                        FireChatUtil.setAdminTyping(room);
                        isAlreadyTyping = true;
                    }
                }

            }

            @Override
            public void onCancel() {
                hideOrShowRecord(true);
                stopRecord(true, -1);
                isAlreadyTyping = false;
                FireChatUtil.removeAdminTyping(room);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                hideOrShowRecord(true);
                isAlreadyTyping = false;
                FireChatUtil.removeAdminTyping(room);
                stopRecord(false, recordTime);
            }

            @Override
            public void onLessThanSecond() {
                hideOrShowRecord(true);
                isAlreadyTyping = false;
                FireChatUtil.removeAdminTyping(room);
                stopRecord(true, -1);
            }
        });

        edtMessage = findViewById(R.id.edtMessage);
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (room.adminID == SharedUtil.getSharedUserID()) {
                    handler.removeCallbacks(inputFinishChecker);
                    if (isAlreadyTyping) {
                        return;
                    } else {
                        FireChatUtil.setAdminTyping(room);
                        isAlreadyTyping = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (room.adminID == SharedUtil.getSharedUserID()) {
                    if (s.length() > 0) {
                        lastTextEdit = System.currentTimeMillis();
                        handler.postDelayed(inputFinishChecker, delay);
                        changeSendButtonState(true);
                    } else {
                        isAlreadyTyping = false;
                        FireChatUtil.removeAdminTyping(room);
                        changeSendButtonState(false);
                    }
                } else {
                    if (s.length() > 0) {
                        changeSendButtonState(true);
                    } else {
                        changeSendButtonState(false);
                    }
                }
            }
        });
    }

    private void loadUIWithRoom(RoomModel room) {
        imgBG.setImageResource(room.background);
        txtAdmin.setText(room.adminName);
        if (room.adminAvatar.isEmpty()) {
            imgRoomAdmin.setImageResource(R.drawable.ic_user_placehoder);
        } else {
            AppUtil.loadImageByUrl(this, imgRoomAdmin, room.adminAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        txtMemeberCnt.setText(StringUtil.convertThousand(room.memberCnt));
        imgAdminVerify.setVisibility(room.adminVerify == 1 ? View.VISIBLE : View.GONE);
    }

    private void changeSendButtonState(boolean setTyping) {
        if (setTyping) {
            recordButton.goToState(AnimButton.TYPING_STATE);
            recordButton.setListenForRecord(false);
        } else {
            recordButton.goToState(AnimButton.RECORDING_STATE);
            recordButton.setListenForRecord(true);
        }
    }

    private void stopRecord(boolean isCancelled, long recordTime) {
        try {
            if (recorder != null) {
//                recorder.stopRecording();
                recorder.stop();
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isCancelled) {
            recordFile.delete();
            return;
        }

        RoomMessageModel roomMessageModel = new RoomMessageModel();
        roomMessageModel.userID = SharedUtil.getSharedUserID();
        roomMessageModel.userName = SharedUtil.getSharedUserName();
        roomMessageModel.senderAvatar = SharedUtil.getSharedUserAvatar();
        roomMessageModel.format = "3";
        roomMessageModel.content = "" + recordTime;
        roomMessageModel.extra = recordFile.getAbsolutePath();

        if (room.adminID == SharedUtil.getSharedUserID()) {
            adminMessages.add(0, roomMessageModel);
            adminMessageAdapter.notifyDataSetChanged();
        } else {
            otherMessages.add(0, roomMessageModel);
            otherMessageAdapter.notifyDataSetChanged();
        }

        /// send voice message
        Map<String, String> param = new HashMap<>();
        param.put("fromuserid", "" + SharedUtil.getSharedUserID());
        param.put("touserid", "" + room.roomID);
        param.put("content", "" + recordTime);
        ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.ADD_VOICE_ROOM_MESSAGE, param, "audio", recordFile, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                hideOrShowRecord(true);
                FireChatUtil.setRoomMessage(room);
            }

            @Override
            public void onEventInternetError(Exception e) {
                Log.d("error2", e.getMessage());
            }

            @Override
            public void onEventServerError(Exception e) {
                Log.d("error1", e.getMessage());
            }
        });
    }

    private void hideOrShowRecord(boolean hideRecord) {
        if (hideRecord) {
            recordView.setVisibility(View.GONE);
            csSend.setVisibility(View.VISIBLE);
        } else {
            recordView.setVisibility(View.VISIBLE);
            csSend.setVisibility(View.GONE);
        }
    }

    private void handleRecord() {
        recordFile = DirManager.generateFile(SharedUtil.getSharedUserID());
//        recorder = OmRecorder.wav(new PullTransport.Default(gtMic(), audioChunk -> {
//
//        }), recordFile);
//        new Handler().postDelayed(() -> recorder.startRecording(), 575);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(recordFile.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PullableSource gtMic() {
        return new PullableSource.AutomaticGainControl(
                new PullableSource.Default(
                        new AudioRecordConfig.Default(
                                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100
                        )
                )
        );
    }

    private void initRoomMessages() {
        Map<String, String> param = new HashMap<>();
        param.put("roomID", "" + room.roomID);
        param.put("loggedin_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_ROOM_MESSAGE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    adminMessages.clear();
                    otherMessages.clear();
                    JSONObject roomInfo = obj.getJSONObject("room");
                    room.initWithJSON(roomInfo);
                    loadUIWithRoom(room);
                    JSONArray msgInfo = obj.getJSONArray("message");
                    for (int i = 0; i < msgInfo.length(); i++) {
                        JSONObject object = msgInfo.getJSONObject(i);
                        RoomMessageModel messageModel = new RoomMessageModel();
                        messageModel.initWithJSON(object);
                        if (messageModel.userID == room.adminID) {
                            adminMessages.add(messageModel);
                        } else {
                            otherMessages.add(messageModel);
                        }
                    }
                    adminMessageAdapter.notifyDataSetChanged();
                    otherMessageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                Log.d("error", e.toString());
            }

            @Override
            public void onEventServerError(Exception e) {
                Log.d("error", e.toString());
            }
        });
    }

    private void closeRoom() {
        Map<String, String> param = new HashMap<>();
        param.put("roomID", "" + room.roomID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.CLOSE_ROOM, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                FireChatUtil.closeRoom(room);
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }

    private void leaveRoom(boolean isShowFollowDialog) {
        Map<String, String> param = new HashMap<>();
        param.put("roomID", "" + room.roomID);
        param.put("loggedin_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.LEAVE_ROOM, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                FireChatUtil.mFireDBRoomIDS.child(String.valueOf(room.roomID)).removeEventListener(observeCloseRoomListener);
                FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("isTyping").removeEventListener(adminTypingListener);
                FireChatUtil.mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("roomMessages").removeEventListener(observeMessageListener);
                FireChatUtil.setRoomMessage(room);
                FireChatUtil.joinLeaveRoom(room, false);
                Intent returnIntent = new Intent();
                Gson gson = new Gson();
                String roomStr = gson.toJson(room);
                returnIntent.putExtra("RETURNROOMMOEL", roomStr);
                returnIntent.putExtra("ISSHOWFOLLOWDIALOG", isShowFollowDialog);
                setResult(ChatRoomActivityResultCode.ROOMLEAVE.resultCode, returnIntent);
                finish();
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }

    private void sendRoomMessage() {
        String msgTxt = edtMessage.getText().toString();
        if (msgTxt.isEmpty() && mUpload == null) {
            return;
        } else {
            Map<String, String> param = new HashMap<>();
            param.put("type", "GROUP");
            param.put("fromuserid", "" + SharedUtil.getSharedUserID());
            param.put("touserid", "" + room.roomID);
            param.put("content", msgTxt);
            ApiUtil.onAPIConnectionFileUploadResponse(ApiUtil.ADD_ROOM_MESSAGE, param, "extra", mUpload, new ApiUtil.APIManagerCallback() {
                @Override
                public void onEventCallBack(JSONObject obj) {
                    FireChatUtil.setRoomMessage(room);
                    reloadUI();
                    changeSendButtonState(false);
                }

                @Override
                public void onEventInternetError(Exception e) {
                }

                @Override
                public void onEventServerError(Exception e) {
                }
            });
        }
    }

    private void onSetCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 10001);
            return;
        }
        cropImage.launch(AppUtil.options);
    }

    private void reloadUI() {
        mUpload = null;
        imgExtra.setVisibility(View.GONE);
        imgCamera.setVisibility(View.VISIBLE);
        edtMessage.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001: {
                if (grantResults.length > 0
                        && grantResults[0] == PERMISSION_GRANTED) {
                    cropImage.launch(AppUtil.options);
                } else {
                    BannerUtil.onShowWaringAlert(rltContainer, AppConstant.PERMISSION_DENIED, AppConstant.SHOW_BANNER_TIME);
                }
                return;
            }
        }
    }

    private void onShowVoicePlayerView(RoomMessageModel model) {
        voicePlayView.setVisibility(View.VISIBLE);
        voicePlayView.initView(model, () -> {
            isShowVoiceView = false;
            voicePlayView.setVisibility(View.GONE);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crdClose:
                isCloseLeaveByUser = true;
                String alertContent = "";
                String title = "";
                if (room.adminID == SharedUtil.getSharedUserID()) {
                    alertContent = "¿Deseas salir y abandonar la sala de chat?";
                    title = "Salir de la sala";
                } else {
                    title = "Abandonar la sala";
                    alertContent = "¿Deseas salir de la sala de chat en vivo?";
                }
                AppUtil.showNormalDialogWithCallBack(this, title, alertContent, () -> {
                    if (room.adminID == SharedUtil.getSharedUserID()) {
                        closeRoom();
                    } else {
                        leaveRoom(true);
                    }
                });
                break;
            case R.id.crdCamera:
                AppUtil.isCameraOn = true;
                onSetCamera();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}