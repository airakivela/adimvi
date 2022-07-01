package com.application.adimviandroid.screens.profile.viewpager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.ChatModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.auth.AuthWebActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.screens.profile.RecentFragment;
import com.application.adimviandroid.screens.profile.chat.MessageFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    private MainActivity mActivity;
    private int selectedUserID;
    private int tabIndex;

    private TextView txtAboutMe, txtNumber, txtSocial, txtWeb, txtLocation, txtRegister;
    private LinearLayout lltActivation, lltMessage;

    private ChatModel chatModel;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public PerfilFragment(MainActivity mainActivity, int userID, int tabIndex) {
        this.mActivity = mainActivity;
        this.selectedUserID = userID;
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
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        lltActivation = view.findViewById(R.id.lltActivation);
        lltActivation.setOnClickListener(v -> mActivity.addFragment(new RecentFragment(mActivity, selectedUserID, tabIndex), tabIndex));
        lltMessage = view.findViewById(R.id.lltMessage);
        lltMessage.setVisibility(SharedUtil.getSharedUserID() == selectedUserID ? View.GONE : View.VISIBLE);
        lltMessage.setOnClickListener(v -> mActivity.addFragment(new MessageFragment(mActivity, chatModel, tabIndex), tabIndex));
        txtAboutMe = view.findViewById(R.id.txtAboutMe);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtSocial = view.findViewById(R.id.txtSocial);
        txtNumber = view.findViewById(R.id.txtNumber);
        txtWeb = view.findViewById(R.id.txtWeb);
        txtRegister = view.findViewById(R.id.txtRegister);

        txtWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AuthWebActivity.class);
                intent.putExtra("title", "Link");
                intent.putExtra("url", txtWeb.getText().toString());
                mActivity.startActivity(intent);
            }
        });
    }

    private void initData() {
        Map<String, String> param = new HashMap<>();
        param.put("login_userid", "" + SharedUtil.getSharedUserID());
        param.put("userid", "" + selectedUserID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_PROIFLE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    JSONObject response = obj.getJSONArray("response").getJSONObject(0);
                    txtAboutMe.setText(response.getString("about"));
                    txtLocation.setText(response.getString("location"));
                    txtSocial.setText(response.getString("social-networks"));
                    txtNumber.setText(response.getString("name"));
                    txtWeb.setText(response.getString("website"));
                    txtRegister.setText(response.getString("created"));
                    try {
                        int flags = response.getInt("flags");
                        if (SharedUtil.getSharedUserID() == selectedUserID) {
                            lltMessage.setVisibility(View.GONE);
                        } else {
                            if (flags == 4 || flags == 260 || flags == 0 || flags == 256 || flags == 5 || flags == 261 || flags ==64 || flags ==320) {
                                lltMessage.setVisibility(View.VISIBLE);
                            } else {
                                lltMessage.setVisibility(View.GONE);
                            }
                        }
                        if (SharedUtil.getSharedUserID() == selectedUserID) {
                            mActivity.setPublication(true);
                        } else {
                            if (flags == 4 || flags == 20 || flags == 0 || flags ==16 || flags == 5 || flags == 21 || flags ==64 || flags == 80) {
                                mActivity.setPublication(true);
                            } else {
                                mActivity.setPublication(false);
                            }
                        }
                        chatModel = new ChatModel();
                        chatModel.imgAvatar = response.getString("avatarblobid").isEmpty() ? "" : ApiUtil.ImageUrl + response.getString("avatarblobid");
                        chatModel.userName = response.getString("username");
                        chatModel.userID = response.getInt("userid");
                        chatModel.verified = response.getInt("isVerify");
                    } catch (JSONException e) {
                        lltMessage.setVisibility(View.GONE);
                        mActivity.setPublication(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mActivity.setPublication(false);
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                e.printStackTrace();
                mActivity.setPublication(false);
            }

            @Override
            public void onEventServerError(Exception e) {
                e.printStackTrace();
                mActivity.setPublication(false);
            }
        });
    }
}