package com.application.adimviandroid.utils;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiUtil {
    public enum APIMethod {
        GET, POST
    }
    static final String BaseUrl = "https://www.adimvi.com/appAPI/index.php/api/";
    static public final String ImageUrl = "https://adimvi.com/?qa=image&qa_blobid=";

    //Authentication Part//
    public static String USER_LOGIN = BaseUrl + "userlogin";
    public static String USER_REGISTER = BaseUrl + "userRegister";
    public static String CONTACT = BaseUrl + "contact";
    //Other Part//
    public static String GET_PROIFLE = BaseUrl + "getProfile";
    public static String SALES_NOTIFY = BaseUrl + "salesNotify";
    public static String BUY_POST_LIST = BaseUrl + "buyPostList";
    public static String GET_CATEGORY = BaseUrl + "getCategories";
    public static String GET_CATEGORY_NEW = BaseUrl + "getCategoriesNew";
    public static String GET_POPULAR_TAG = BaseUrl + "popularTagList";
    public static String HOME_SEARCH = BaseUrl + "homeSearch";
    public static String HOME_SEARCH_NEW = BaseUrl + "homeSearchNew";
    public static String GET_NOTIFICATIONS = BaseUrl + "mainNotification";
    public static String SET_ALL_SEEN_NOTIFICATION = BaseUrl + "allSeenNotification";
    public static String SET_SEEN_NOTIFICATION = BaseUrl + "seenNotification";
    public static String GET_POST_DETAIL = BaseUrl + "getPostDetail";
    public static String GET_RELATED_POST = BaseUrl + "relatedPostByCategory";
    public static String GET_MOST_TRENDING = BaseUrl + "getMostTreandingPost";
    public static String GET_MOST_RECENT = BaseUrl + "getRecentPost";
    public static String GET_MOST_VOTED = BaseUrl + "getMostVotedPost";
    public static String GET_MOST_VIEW = BaseUrl + "getMostViewsPost";
    public static String GET_MOST_COMMENT = BaseUrl + "getMostCommentedPost";
    public static String GET_POSTS_BY_CATEGORY = BaseUrl + "getCategoryByPost";
    public static String GET_POSTS_BY_TAG = BaseUrl + "popularTagPostList";
    public static String GET_MUROS_BY_TAG = BaseUrl + "popularTagMuroList";
    public static String GET_FOLLOW_POSTS_BY_TAG = BaseUrl + "popularTagPostFollow";
    public static String GET_TAGS_BY_POST = BaseUrl + "postByTagList";
    public static String BUY_POST = BaseUrl + "buyPost";
    public static String SET_USER_FOLLOWING = BaseUrl + "setUserFollowing";
    public static String SET_POST_FOLLOW = BaseUrl + "setfavourite";
    public static String SET_POST_LIKE = BaseUrl + "setVote";
    public static String SET_POST_REPORT = BaseUrl + "setPostReport";
    public static String SET_POST_COMMENT= BaseUrl + "postComment";
    public static String SET_POST_RATING = BaseUrl + "addRating";
    public static String DELETE_POST = BaseUrl + "postDelete";
    public static String GET_COMMENT_LIST = BaseUrl + "postCommentList";
    public static String EDIT_COMMENT = BaseUrl + "editHideShowpostComment";
    public static String DELET_COMMENT = BaseUrl + "commentDelete";
    public static String GET_COMMENTCOMMENT_LIST = BaseUrl + "CommentCommentList";
    public static String SET_POST_VIEWED = BaseUrl + "postViewed";
    public static String GET_FOLLOWING_POST = BaseUrl + "getPostFollowing";
    public static String GET_FOLLOWING_POST_TEST = BaseUrl + "getPostFollowingTest";
    public static String GET_WALL_FOLLOW = BaseUrl + "wallFollowList";
    public static String GET_FOLLOWING_TAG_POST = BaseUrl + "followTagPostList";
    public static String GET_FOLLOWING_TAG_LIST = BaseUrl + "followTagList";
    public static String SET_WALL_FAVOURITE = BaseUrl + "setWallfavourite";
    public static String GET_WALL_COMMENT_LIST = BaseUrl + "wallCommentList";
    public static String DELETE_WALL_COMMENT = BaseUrl + "deleteWall";
    public static String EDIT_WALL_COMMENT = BaseUrl + "editWall";
    public static String REPLY_WALL_COMMENT = BaseUrl + "replyWallComments";
    public static String UPLOAD_POST_CONTENT_IMAGE = BaseUrl + "uploadPostContentImage";
    public static String GET_DRAFT_POST_LIST = BaseUrl + "draftPostList";
    public static String GET_DRAFT_POST_DETAIL = BaseUrl + "draftPostDetail";
    public static String ADD_NEW_POST = BaseUrl + "addNewPost";
    public static String UPDATE_POST = BaseUrl + "updatePost";
    public static String CHANGE_PASSWORD = BaseUrl + "changePassword";
    public static String EDIT_PROFILE = BaseUrl + "editProfile";
    public static String GET_CHAT_LIST = BaseUrl + "allPrivateMessagesList";
    public static String GET_MESSAGES = BaseUrl + "privateMessagesList";
    public static String SEND_MESSAGES = BaseUrl + "addPrivateMessagesNew";
    public static String GET_FAVORITE = BaseUrl + "getfavourite";
    public static String GET_NOTE_LIST = BaseUrl + "notesList";
    public static String EDIT_NOTE = BaseUrl + "updateNotes";
    public static String ADD_NOTE = BaseUrl + "addNotes";
    public static String DELETE_NOTE = BaseUrl + "notesDelete";
    public static String GET_USER_PUBLICATION = BaseUrl + "getUserPublications";
    public static String GET_USER_PUBLICATION_NEW = BaseUrl + "getUserPublicationsNew";
    public static String GET_FOLLOWERS = BaseUrl + "getFollowers";
    public static String GET_FOLLOWERS_NEW = BaseUrl + "getFollowersNew";
    public static String GET_FOLLOWING = BaseUrl + "getUserFollowing";
    public static String GET_FOLLOWING_NEW = BaseUrl + "getUserFollowingNew";
    public static String WALL_POST_LIST = BaseUrl + "wallPostList";
    public static String ADD_NEW_WALL = BaseUrl + "addWall";
    public static String GET_RECENT_ACTIVITY = BaseUrl + "userRecentActivity";
    public static String GET_USER_BALANCE = BaseUrl + "userBalance";
    public static String SET_USER_CREDIT = BaseUrl + "saveCreditTransaction";
    public static String UPDATE_FCM_DEVICE = BaseUrl + "updateFCMDevice";
    public static String GET_ALL_MEMBERS = BaseUrl + "fetchAllUser";
    public static String GET_RECENT_WALL_USER = BaseUrl + "recentWallUser";
    public static String UPDATE_USER_PROFILE_VISIT = BaseUrl + "updateUserProfileVisited";
    public static String UPDATE_USER_REWALL_COUNT = BaseUrl + "updateUserRewallCount";
    public static String UPDATE_WALL_COUNT = BaseUrl + "updateWallCount";
    public static String ADD_ROOM = BaseUrl + "addRoom";
    public static String GET_ROOM_LIST = BaseUrl + "getRoomList";
    public static String JOIN_ROOM = BaseUrl + "joinRoom";
    public static String LEAVE_ROOM = BaseUrl + "leaveRoom";
    public static String CLOSE_ROOM = BaseUrl + "closeRoom";
    public static String ADD_ROOM_MESSAGE = BaseUrl + "addRoomMessage";
    public static String ADD_VOICE_ROOM_MESSAGE = BaseUrl + "addVoiceRoomMessage";
    public static String GET_ROOM_MESSAGE = BaseUrl + "fetchRoomMessage";
    public static String SET_FEATURE_POST = BaseUrl + "setFeaturePost";
    public static String SET_FEATURE_WALL = BaseUrl + "setFavoriteWall";
    public static String SET_NOTIFICATION_BLOCK = BaseUrl + "setBlockUser";
    public static String GET_NOTIFICATION_BLOCK = BaseUrl + "getBlockState";
    // story part
    public static String PUBLISH_STORY = BaseUrl + "publishStory";

    public static void onAPIConnectionResponse (String url, Map<String, String> params, APIMethod method, APIManagerCallback apiResponse) {
        if (method == APIMethod.POST) {
            OkHttpUtils.post().url(url)
                .params(params)
                .build()
                .connTimeOut(50000)
                .readTimeOut(50000)
                .writeTimeOut(50000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        apiResponse.onEventInternetError(e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            apiResponse.onEventCallBack(obj);
                        } catch (JSONException e) {
                            apiResponse.onEventServerError(e);
                            e.printStackTrace();
                        }
                    }
                });
        } else {
            OkHttpUtils.get().url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        apiResponse.onEventInternetError(e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            apiResponse.onEventCallBack(obj);
                        } catch (JSONException e) {
                            apiResponse.onEventServerError(e);
                            e.printStackTrace();
                        }
                    }
                });
        }
    }

    public static void onAPIConnectionRawValue (String url, String params, APIManagerCallback apiResponse) {
        OkHttpUtils.postString().url(url)
            .content(params)
            .build()
            .connTimeOut(50000)
            .readTimeOut(50000)
            .writeTimeOut(50000)
            .execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int i) {
                    apiResponse.onEventInternetError(e);
                }

                @Override
                public void onResponse(String response, int i) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        apiResponse.onEventCallBack(obj);
                    } catch (JSONException e) {
                        apiResponse.onEventServerError(e);
                        e.printStackTrace();
                    }
                }
            });
    }

    public static void onAPIConnectionFileUploadResponse (String url, Map<String, String> params, String key, File file, APIManagerCallback apiResponse) {
        if (file != null) {
            OkHttpUtils.post().url(url)
                    .params(params)
                    .addFile(key, file.getName(), file)
                    .build()
                    .connTimeOut(50000)
                    .readTimeOut(50000)
                    .writeTimeOut(50000)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            apiResponse.onEventInternetError(e);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                apiResponse.onEventCallBack(obj);
                            } catch (JSONException e) {
                                apiResponse.onEventServerError(e);
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            OkHttpUtils.post().url(url)
                    .params(params)
                    .build()
                    .connTimeOut(50000)
                    .readTimeOut(50000)
                    .writeTimeOut(50000)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            apiResponse.onEventInternetError(e);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                apiResponse.onEventCallBack(obj);
                            } catch (JSONException e) {
                                apiResponse.onEventServerError(e);
                                e.printStackTrace();
                            }
                        }
                    });
        }

    }

    public static void uploadMultiImages(String url, Map<String, String> params, File[] files, APIManagerCallback apiResponse) {
        Map<String, File> fileParams = new HashMap<>();
        for (File file : files) {
            fileParams.put(file.getName(), file);
        }
        OkHttpUtils.post().url(url)
                .files("images[]", fileParams)
                .params(params)
                .build()
                .connTimeOut(50000)
                .readTimeOut(50000)
                .writeTimeOut(50000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        apiResponse.onEventInternetError(e);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            apiResponse.onEventCallBack(obj);
                        } catch (JSONException e) {
                            apiResponse.onEventServerError(e);
                            e.printStackTrace();
                        }
                    }
                });
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        for (File file : files) {
//            if (file.exists()) {
//                final MediaType MEDIA_TYPE = MediaType.parse(MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()));
//                builder.addFormDataPart("my_images[]", file.getName(), RequestBody.create(MEDIA_TYPE, file));
//            } else {
//                Log.d("UPLOADMULTIIMAGES", "file not exist");
//            }
//        }
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            builder.addFormDataPart(entry.getKey(), entry.getValue());
//        }
//        RequestBody requestBody = builder.build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//        OkHttpClient client = new OkHttpClient.Builder().build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
    }

    public interface APIManagerCallback {
        void onEventCallBack(JSONObject obj);
        void onEventInternetError(Exception e);
        void onEventServerError(Exception e);
    }
}
