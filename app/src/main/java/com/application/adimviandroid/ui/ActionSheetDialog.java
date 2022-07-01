package com.application.adimviandroid.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.application.adimviandroid.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ActionSheetDialog extends BottomSheetDialog {

    private Context mContext;
    private ActionSheetListener mListener;

    private LinearLayout lltFirst, lltSecond, lltCancel;
    private TextView txtFirst, txtSecond;
    private String strFirst, strSecond;

    public ActionSheetDialog(@NonNull Context context, int style, String strFirst, String strSecond , ActionSheetListener listener) {
        super(context, style);
        this.mContext = context;
        this.strFirst = strFirst;
        this.strSecond = strSecond;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_action_sheet);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lltFirst = findViewById(R.id.lltFirst);
        lltSecond = findViewById(R.id.lltSecond);
        lltCancel = findViewById(R.id.lltClose);
        txtFirst = findViewById(R.id.txtFirst);
        txtSecond = findViewById(R.id.txtSecond);

        txtFirst.setText(strFirst);
        txtSecond.setText(strSecond);

        lltFirst.setOnClickListener(v -> mListener.onClickFirstOption());
        lltSecond.setOnClickListener(v -> mListener.onClickSecondOption());
        lltCancel.setOnClickListener(v -> dismiss());
    }

    public interface ActionSheetListener {
        void onClickFirstOption();
        void onClickSecondOption();
    }
}
