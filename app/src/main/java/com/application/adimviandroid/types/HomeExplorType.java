package com.application.adimviandroid.types;

import com.application.adimviandroid.utils.ApiUtil;

public enum HomeExplorType {
    MOSTTRENDING(ApiUtil.GET_MOST_TRENDING, "Tendencias"),
    MOSTRECENT(ApiUtil.GET_MOST_RECENT, "Recientes"),
    MOSTVOTED(ApiUtil.GET_MOST_VOTED, "Más votados"),
    MOSTVIEW(ApiUtil.GET_MOST_VIEW, "Más visitados"),
    MOSTCOMMENT(ApiUtil.GET_MOST_COMMENT, "Más comentados");

    public String url;
    public String title;

    HomeExplorType(String apiURL, String strTitle) {
        url = apiURL;
        title = strTitle;
    }
}
