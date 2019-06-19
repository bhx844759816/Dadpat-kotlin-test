package com.bhx.common.http;

/**
 * 接口返回数据的包装类
 *
 * @param <T> 实体数据
 */
public class BaseResponse<T> {
    private T data;
    private int code;
    private String msg;

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
}
