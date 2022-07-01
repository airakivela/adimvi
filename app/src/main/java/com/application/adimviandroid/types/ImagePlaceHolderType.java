package com.application.adimviandroid.types;

import com.application.adimviandroid.R;

public enum ImagePlaceHolderType {
    USERIMAGE(R.drawable.ic_user_placehoder),
    POSTIMAGE(R.drawable.img_post_placeholder),
    BACKGROUNDIMAGE(R.drawable.img_profile_bg);

    public int resID;

    ImagePlaceHolderType(int id) {
        this.resID = id;
    }
}
