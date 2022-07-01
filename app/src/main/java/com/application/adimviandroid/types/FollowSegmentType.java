package com.application.adimviandroid.types;

import com.application.adimviandroid.R;
import com.application.adimviandroid.utils.ApiUtil;

public enum FollowSegmentType {

//    FOLLOWING_POST(0, ApiUtil.GET_FOLLOWING_POST),
    FOLLOWING_POST_TEST(0, ApiUtil.GET_FOLLOWING_POST_TEST),
    FOLLOWING_WALL(1, ApiUtil.GET_WALL_FOLLOW),
    FOLLOWING_POST_TAG(2, ApiUtil.GET_FOLLOWING_TAG_POST);

    public int index;
    public String url;

    FollowSegmentType(int index, String url) {
        this.index = index;
        this.url = url;
    }
}
