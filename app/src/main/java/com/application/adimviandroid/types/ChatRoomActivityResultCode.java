package com.application.adimviandroid.types;

public enum ChatRoomActivityResultCode {
    ROOMLEAVE(1111),
    ROOMCLOSE(1112);

    public int resultCode;
    ChatRoomActivityResultCode(int value) {
        resultCode = value;
    }
}
