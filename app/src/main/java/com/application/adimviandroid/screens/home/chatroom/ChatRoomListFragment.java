package com.application.adimviandroid.screens.home.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.RoomNormalAdapter;
import com.application.adimviandroid.adapter.RoomSiguiendoAdapter;
import com.application.adimviandroid.models.RoomModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.types.ChatRoomActivityResultCode;
import com.application.adimviandroid.ui.FollowDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.FireChatUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomListFragment extends Fragment {

    private MainActivity mActivity;
    private int tabIndex;

    private ImageView imgBack;
    private TextView txtTitle;
    private EditText edtSearch;
    private RecyclerView rclSiguiendo, rclRooms;
    private ShimmerFrameLayout shimer;
    private NestedScrollView nsContainer;
    private LinearLayout lltMain;

    private RoomSiguiendoAdapter siguiendoAdapter;
    private RoomNormalAdapter normalAdapter;
    private final List<RoomModel> siguiendoRooms = new ArrayList<>();
    private final List<RoomModel> normalRooms = new ArrayList<>();
    private final List<RoomModel> filteredNormalRooms = new ArrayList<>();
    private FollowDialog mFollowDialog;

    ActivityResultLauncher<Intent> chatRoomActivityLauch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
        if (result.getResultCode() == ChatRoomActivityResultCode.ROOMLEAVE.resultCode) {
            Intent returnIntent = result.getData();
            String strRoom = returnIntent.getStringExtra("RETURNROOMMOEL");
            Gson gson = new Gson();
            RoomModel roomModel = gson.fromJson(strRoom, RoomModel.class);
            boolean isShowFollowDialog = returnIntent.getBooleanExtra("ISSHOWFOLLOWDIALOG", false);
            if (isShowFollowDialog) {
                mFollowDialog = new FollowDialog(mActivity, roomModel, () -> {
                    initData();
                });
                mFollowDialog.show();
            }
        }
        if (result.getResultCode() == ChatRoomActivityResultCode.ROOMCLOSE.resultCode) {
            AppUtil.showNormalDialog(mActivity, "Cierre de sala", "La persona que ha creado esta sala ha terminado el chat. ¡Gracias por unirte!");
        }
    });

    private final ValueEventListener observeRoomListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            initData();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public ChatRoomListFragment() {

    }

    public ChatRoomListFragment(MainActivity activity, int tabIndex) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_room_list, container, false);
        shimer = view.findViewById(R.id.shimer);
        nsContainer = view.findViewById(R.id.nsContainer);
        lltMain = view.findViewById(R.id.llt_main);
        initFire();
        initUIView(view);
        return view;
    }

    private void initFire() {
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        nsContainer.setVisibility(View.GONE);
        FireChatUtil.mFireDBRoomIDS.addValueEventListener(observeRoomListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::addRoomListener, 5000);
    }

    private void addRoomListener() {
        FireChatUtil.mFireDBRoomIDS.addValueEventListener(observeRoomListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        FireChatUtil.mFireDBRoomIDS.removeEventListener(observeRoomListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FireChatUtil.mFireDBRoomIDS.removeEventListener(observeRoomListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FireChatUtil.mFireDBRoomIDS.removeEventListener(observeRoomListener);
    }

    private void initData() {
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        filteredNormalRooms.clear();
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_ROOM_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                nsContainer.setVisibility(View.VISIBLE);
                siguiendoRooms.clear();
                normalRooms.clear();
                try {
                    JSONArray response = obj.getJSONArray("response");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        RoomModel model = new RoomModel();
                        model.initWithJSON(object);
                        if (model.isSiguiendo == 1) {
                            siguiendoRooms.add(model);
                        } else {
                            normalRooms.add(model);
                        }
                    }
                    if (normalRooms.isEmpty()) {
                        lltMain.setVisibility(View.GONE);
                    } else {
                        lltMain.setVisibility(View.VISIBLE);
                    }
                    filteredNormalRooms.addAll(normalRooms);
                    siguiendoAdapter.notifyDataSetChanged();
                    normalAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    nsContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                nsContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                nsContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initUIView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Chats en vivo");

        rclSiguiendo = view.findViewById(R.id.rcl_siguiendo);
        rclSiguiendo.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rclSiguiendo.setItemAnimator(new DefaultItemAnimator());
        siguiendoAdapter = new RoomSiguiendoAdapter(mActivity, siguiendoRooms, new RoomSiguiendoAdapter.RoomSiguiendoListenr() {
            @Override
            public void onClickAddRoom() {
                mActivity.addFragment(new AddRoomFragment(mActivity, tabIndex), tabIndex);
            }

            @Override
            public void onClickSiguiendoRoom(RoomModel room) {
                onCallJoinRoom(room);
            }
        });
        rclSiguiendo.setAdapter(siguiendoAdapter);

        rclRooms = view.findViewById(R.id.rcl_rooms);
        rclRooms.setLayoutManager(new LinearLayoutManager(mActivity));
        rclRooms.setItemAnimator(new DefaultItemAnimator());
        normalAdapter = new RoomNormalAdapter(mActivity, filteredNormalRooms, room -> {
            onCallJoinRoom(room);
        });
        rclRooms.setAdapter(normalAdapter);

        edtSearch = view.findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredNormalRooms.clear();
                if (s.toString().isEmpty()) {
                    filteredNormalRooms.addAll(normalRooms);
                } else {
                    String filteredKey = s.toString().toLowerCase();
                    for(RoomModel model : normalRooms) {
                        if (model.title.toLowerCase().contains(filteredKey)) {
                            filteredNormalRooms.add(model);
                        }
                    }
                }
                normalAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void onCallJoinRoom(RoomModel room) {
        final ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("roomID", "" + room.roomID);
        param.put("loggedin_userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.JOIN_ROOM, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                AppUtil.onDismissProgressDialog(progressDialog);
                FireChatUtil.joinLeaveRoom(room, true);
                Gson gson = new Gson();
                String strRoom = gson.toJson(room);
                Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                intent.putExtra("ROOMMODEL", strRoom);
                chatRoomActivityLauch.launch(intent);
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
            }
        });
    }
}