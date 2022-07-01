package com.application.adimviandroid.utils;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class BannerUtil {
    public static void onShowWaringAlert(View view, String alert, int milliseconds) {
        Snackbar snackbar = Snackbar.make(view, alert,milliseconds)
                .setBackgroundTint(view.getContext().getColor(android.R.color.holo_orange_dark))
                .setActionTextColor(view.getContext().getColor(android.R.color.holo_orange_dark));
        TextView textView = ((TextView)snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text));
        textView.setMaxLines(5);
        snackbar.show();
    }

    public static void onShowSuccessAlertEventWithCallback(View view, String alert, int milliseconds, final SnackBarkCallback successCallback) {
        Snackbar.make(view, alert,milliseconds).addCallback(new Snackbar.Callback(){
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                successCallback.onSuccessCallback();
            }
        })
                .setBackgroundTint(view.getContext().getResources().getColor(android.R.color.holo_green_dark))
                .setActionTextColor(view.getContext().getResources().getColor(android.R.color.holo_green_dark)).show();
    }

    public interface SnackBarkCallback {
        void onSuccessCallback();
    }
}
