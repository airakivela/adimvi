package com.application.adimviandroid.screens.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.screens.CreditActivity;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.auth.AuthWebActivity;
import com.application.adimviandroid.screens.auth.ContactActivity;
import com.application.adimviandroid.utils.AppUtil;

public class MenuFragment extends Fragment {

    private MainActivity mActivity;
    private int tabIndex;

    private LinearLayout lltTerms, lltHelp, lltContacto, lltLock, lltCredit;
    private ImageView imgBack;
    private TextView txtTitle;

    public MenuFragment() {
        // Required empty public constructor
    }

    public MenuFragment(MainActivity mainActivity, int tabIndex) {
        this.mActivity = mainActivity;
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        lltTerms = view.findViewById(R.id.lltTerms);
        lltHelp = view.findViewById(R.id.lltHelp);
        lltContacto = view.findViewById(R.id.lltContact);
        lltLock = view.findViewById(R.id.lltLock);
        lltCredit = view.findViewById(R.id.lltCredit);

        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Información");

        lltTerms.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AuthWebActivity.class);
            intent.putExtra("title", "Términos y política de privacidad");
            intent.putExtra("url", "https://www.adimvi.com/?qa=T%C3%A9rminos-y-privacidad");
            mActivity.startActivity(intent);
        });

        lltCredit.setOnClickListener(v -> {
            AppUtil.showOtherActivity(mActivity, CreditActivity.class, -1);
        });

        lltHelp.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AuthWebActivity.class);
            intent.putExtra("title", "Preguntas frecuentes");
            intent.putExtra("url", "https://www.adimvi.com/?qa=Informaci%C3%B3n");
            mActivity.startActivity(intent);
        });

        lltContacto.setOnClickListener(v -> AppUtil.showOtherActivity(mActivity, ContactActivity.class, -1));

        lltLock.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AuthWebActivity.class);
            intent.putExtra("title", "Recuperar contraseña");
            intent.putExtra("url", "https://www.adimvi.com/?qa=forgot");
            mActivity.startActivity(intent);
        });
    }
}