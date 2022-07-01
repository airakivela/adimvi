package com.application.adimviandroid.screens.profile.note;

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
import com.application.adimviandroid.adapter.NoteAdapter;
import com.application.adimviandroid.models.NoteModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.profile.ProfileFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.SharedUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteListFragment extends Fragment {

    private MainActivity mActivity;

    private TextView txtTitle, txtNoData;
    private ImageView imgBack, imgAdd;
    private RecyclerView rclNote;
    private ShimmerFrameLayout shimer;

    private List<NoteModel> mNotes = new ArrayList<>();
    private NoteAdapter mAdapter;

    public NoteListFragment() {
        // Required empty public constructor
    }

    public NoteListFragment(MainActivity mainActivity) {
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
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        initUIView(view);
        initData();
        return view;
    }

    private void initUIView(View view) {
        shimer = view.findViewById(R.id.shimer);
        rclNote = view.findViewById(R.id.rclNotes);
        rclNote.setLayoutManager(new LinearLayoutManager(mActivity));
        rclNote.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new NoteAdapter(mActivity, mNotes, new NoteAdapter.NoteCellListener() {
            @Override
            public void onClickNoteCell(NoteModel note) {
                mActivity.addFragment(new AddNoteFragment(mActivity, note), ProfileFragment.TAB_POSITION);
            }

            @Override
            public void onClickDeleteNote(int notID, int position) {
                mNotes.remove(position);
                mAdapter.notifyDataSetChanged();
                onCallDeleteNote(notID);
            }
        });
        rclNote.setAdapter(mAdapter);

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Notas");
        txtNoData = view.findViewById(R.id.txtNoData);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        imgAdd = view.findViewById(R.id.imgAdd);
        imgAdd.setOnClickListener(v -> mActivity.addFragment(new AddNoteFragment(mActivity, null), ProfileFragment.TAB_POSITION));
    }

    private void initData() {
        shimer.setVisibility(View.VISIBLE);
        shimer.startShimmer();
        rclNote.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);

        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());

        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_NOTE_LIST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    mNotes.clear();
                    shimer.setVisibility(View.GONE);
                    shimer.stopShimmer();
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray noteArr = response.getJSONArray("notesList");
                    if (noteArr.length() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        rclNote.setVisibility(View.VISIBLE);
                        for (int i = 0; i < noteArr.length(); i++) {
                            NoteModel note = new NoteModel();
                            note.initWithJSON(noteArr.getJSONObject(i));
                            mNotes.add(note);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                txtNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEventServerError(Exception e) {
                shimer.setVisibility(View.GONE);
                shimer.stopShimmer();
                txtNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onCallDeleteNote(int noteID) {

        Map<String, String> param = new HashMap<>();
        param.put("noteId", "" + noteID);
        ApiUtil.onAPIConnectionResponse(ApiUtil.DELETE_NOTE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
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