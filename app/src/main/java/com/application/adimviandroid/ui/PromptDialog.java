package com.application.adimviandroid.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.application.adimviandroid.R;

public class PromptDialog extends Dialog {

    private Context context;
    private String msg;
    private PromptDialogListener mListener;

    private TextView txtMsg;
    private Button cancelUB, okUB;

    public PromptDialog(@NonNull Context context, String msg, PromptDialogListener mListener) {
        super(context);
        this.context = context;
        this.msg = msg;
        this.mListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_prompt);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtMsg = findViewById(R.id.txtContent);
        cancelUB = findViewById(R.id.btnCancel);
        okUB = findViewById(R.id.btnOK);

        txtMsg.setText(msg);
        cancelUB.setOnClickListener(v -> dismiss());
        okUB.setOnClickListener(v -> {
            dismiss();
            mListener.onClickOKUBListener();
        });
    }

    public interface PromptDialogListener {
        void onClickOKUBListener();
    }
}
