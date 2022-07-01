package com.application.adimviandroid.screens.profile.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.adapter.ChatAdapter;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageListFragment extends Fragment {

    private MainActivity mActivity;

    private RecyclerView rchChat;
    private ImageView imgBack;
    private TextView txtTitle;
    private ShimmerFrameLayout shimer;

    private List<ChatModel> mChats = new ArrayList<>();
    private ChatAdapter mAdapter;

    public MessageListFragment() {
        // Required empty public constructor
    }

    public MessageListFragment(MainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity)getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);

        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Mensajes");

        rchChat = view.findViewById(R.id.rclMessage);
        rchChat.setLayoutManager(new LinearLayoutManager(mActivity));
        rchChat.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ChatAdapter(mActivity, mChats, chat -> {
            mActivity.addFragment(new MessageFragment(mActivity, chat, ProfileFragment.TAB_POSITION), ProfileFragment.TAB_POSITION);
        });
        rchChat.setAdapter(mAdapter);
    }

    private void initData() {
        mChats.clear();
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        rchChat.setVisibility(View.GONE);

        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_CHAT_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                rchChat.setVisibility(View.VISIBLE);
                try {
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray messages = response.getJSONArray("message");
                    for (int i = 0; i < messages.length(); i++) {
                        ChatModel chat = new ChatModel();
                        chat.initWithJSON(messages.getJSONObject(i));
                        mChats.add(chat);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                rchChat.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                rchChat.setVisibility(View.VISIBLE);
            }
        });
    }
}