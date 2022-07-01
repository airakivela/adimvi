package com.application.adimviandroid.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.adimviandroid.models.RoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FireChatUtil {

    public static final DatabaseReference mFireDB = FirebaseDatabase.getInstance().getReference();

    /// Start Of Private Message Part ///
    public static final DatabaseReference mFireDBPrivateMessage = mFireDB.child("PrivateMessage");

    public static DatabaseReference privateMsgRef (int userID) {
        return mFireDBPrivateMessage.child(AppUtil.getPrivateMessageChanelByCombinationUserIDS(SharedUtil.getSharedUserID(), userID));
    }

    public static void addPrivateMessage(int targetUserID) {
        String key = mFireDBPrivateMessage.push().getKey();
        privateMsgRef(targetUserID).child("privateMessageID").setValue(key);
    }

    public static void setPrivateMessageTypingIndicator(int targetUserID, boolean isTyping) {
        privateMsgRef(targetUserID).child("typingIndicator").child(String.valueOf(SharedUtil.getSharedUserID())).setValue(isTyping);
    }

    public static void removePrivateMessageTypingIndicator(int targetUserID) {
        privateMsgRef(targetUserID).child("typingIndicator").child("" + SharedUtil.getSharedUserID()).removeValue();
    }

    public static void removeOthersPrivateMessageTypingIndicator(int targetUserID) {
        privateMsgRef(targetUserID).child("typingIndicator").child("" + targetUserID).removeValue();
    }

    public static void setPrivateMessaageChannelUserIsON(int targetUserID) {
        privateMsgRef(targetUserID).child("isChanelOn").child("" + SharedUtil.getSharedUserID()).setValue(true);
    }

    public static void removePrivateMessageChanelIsON(int targetUserID) {
        privateMsgRef(targetUserID).child("isChanelOn").child("" + SharedUtil.getSharedUserID()).removeValue();
    }

    /// End Of Private Message Part ///

    /// Start Of Room Message Part///
    ///// chat room list part /////
    public static final DatabaseReference mFireDBRoomIDS = mFireDB.child("RoomIDS");

    public static void createRoom(RoomModel room, RoomMessageListener listener) {
        String pushKey = mFireDBRoomIDS.child(String.valueOf(room.roomID)).push().getKey();
        mFireDBRoomIDS.child(String.valueOf(room.roomID)).child("pushKey").setValue(pushKey).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.createdRoomSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.createdRoomSuccess();
            }
        });
    }

    public static void closeRoom(RoomModel room) {
        mFireDBRoomIDS.child(String.valueOf(room.roomID)).removeValue();
        mFireDBRoomMessages.child(String.valueOf(room.roomID)).removeValue();
    }

    public static void joinLeaveRoom(RoomModel room, boolean isJoin) {
        int updateMemberCnt = room.memberCnt;
        if (isJoin) {
            updateMemberCnt ++;
        } else {
            updateMemberCnt --;
        }
        mFireDBRoomIDS.child(String.valueOf(room.roomID)).child("members").setValue(updateMemberCnt);
        setRoomMessage(room);
    }

    ///// room message part /////
    public static final DatabaseReference mFireDBRoomMessages = mFireDB.child("RoomMessages");

    public static void setRoomMessage(RoomModel room) {
        String pushKey = mFireDBRoomMessages.child(String.valueOf(room.roomID)).push().getKey();
        mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("roomMessages").setValue(pushKey);
    }

    public static void setAdminTyping(RoomModel room) {
        mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("isTyping").setValue(true);
    }

    public static void removeAdminTyping(RoomModel room) {
        mFireDBRoomMessages.child(String.valueOf(room.roomID)).child("isTyping").removeValue();
    }

    public interface RoomMessageListener {
        void createdRoomSuccess();
    }
}
