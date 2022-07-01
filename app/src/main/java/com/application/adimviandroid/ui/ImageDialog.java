package com.application.adimviandroid.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.application.adimviandroid.R;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.AppUtil;

public class ImageDialog extends Dialog {

    private Context context;
    private Bitmap bitmap;

    private ImageView imageView;

    public ImageDialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_image);
        imageView = findViewById(R.id.imgaeView);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = (int) (((float) bitmap.getHeight()) * (((float) width) / ((float) bitmap.getWidth())));
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
        imageView.setOnClickListener(v -> dismiss());
    }
}
