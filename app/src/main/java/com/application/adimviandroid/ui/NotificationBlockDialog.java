package com.application.adimviandroid.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.application.adimviandroid.R;

public class NotificationBlockDialog extends Dialog {

    private Context mContext;
    private int isPostBlock;
    private int isMuroBlock;
    private NotificationBlockListener mListener;

    public NotificationBlockDialog(@NonNull Context context, int isPostBlock, int isMuroBlock,  NotificationBlockListener listener) {
        super(context);
        this.mContext = context;
        this.isMuroBlock = isMuroBlock;
        this.isPostBlock = isPostBlock;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notification_block);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(v -> dismiss());
        SwitchCompat switchPost = findViewById(R.id.switchPost);
        switchPost.setChecked(isPostBlock == 1);
        SwitchCompat switchMuro = findViewById(R.id.switchMuro);
        LinearLayout okay = findViewById(R.id.lltOkay);
        switchMuro.setChecked(isMuroBlock == 1);
        okay.setOnClickListener(v -> {
            dismiss();
            mListener.onClickConfirmButton(switchPost.isChecked(), switchMuro.isChecked());
        });
    }

    public interface NotificationBlockListener {
        void onClickConfirmButton(boolean isPostBlocked, boolean isMuroBlocked);
    }
}
