package com.bhx.common.http;

import android.net.ParseException;

import android.os.NetworkOnMainThreadException;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class CustomException {


    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;

    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;

    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = 1002;


    /**
     * 协议错误
     */
    public static final int HTTP_ERROR = 1003;

    /**
     * 请求在主线程
     */
    public static final int NETWORK_ON_MAIN_ERROR = 1004;


    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //解析错误
            ex = new ApiException(PARSE_ERROR, "解析数据出错");
            return ex;
        } else if (e instanceof ConnectException) {
            //网络错误
            ex = new ApiException(NETWORK_ERROR,"网络错误");
            return ex;
        } else if (e instanceof NetworkOnMainThreadException) {
            ex = new ApiException(NETWORK_ON_MAIN_ERROR, "网络错误");
            return ex;
        } else if (e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
            //协议错误
            ex = new ApiException(HTTP_ERROR, "网络错误,请检查网络");
            return ex;
        } else {
            //未知错误
            ex = new ApiException(UNKNOWN, "服务器异常");
            return ex;
        }
    }

}
