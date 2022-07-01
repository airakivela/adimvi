package com.application.adimviandroid.types;

import com.application.adimviandroid.R;

public enum NotificationType {
    PRIVATEMESSAGE("private_message", R.drawable.ic_noti_message),
    POSTCOMMENTREPLY("post_comment_reply", R.drawable.ic_noti_reply),
    WALLCOMMENTREPLY("wall_post_comment_reply", R.drawable.ic_noti_reply),
    FOLLOWING("following", R.drawable.ic_noti_follow),
    FOLLOW("follow", R.drawable.ic_noti_follow),
    POSTCOMMENTDISLIKE("post_comment_dislike", R.drawable.ic_noti_dislike),
    POSTDISLIKE("post_dislike", R.drawable.ic_noti_dislike),
    POSTCOMMENTLIKE("post_comment_like", R.drawable.ic_noti_like),
    POSTLIKE("post_like", R.drawable.ic_noti_like),
    POSTCOMMENT("post_comment", R.drawable.ic_noti_comment),
    POSTLATING("post_rating", R.drawable.ic_noti_rate),
    WALLPOST("wall_post", R.drawable.ic_noti_wall),
    WALLPOSTHEART("wall_post_heart", R.drawable.ic_noti_post),
    POSTCOMMENTMENTION("post_comment_mention", R.drawable.ic_noti_mention),
    REWALLPOST("rewall_post", R.drawable.ic_noti_remuro);

    private String key;
    private int val;

    NotificationType(String key, int val) {
        this.key = key;
        this.val = val;
    }

    static public int getValFromKey(String strKey) {
        for (NotificationType type: NotificationType.values()) {
            if (type.key.equals(strKey)) {
                return type.val;
            }
        }
        return 0;
    }
}
