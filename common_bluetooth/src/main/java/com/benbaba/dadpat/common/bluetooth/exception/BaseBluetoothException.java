package com.benbaba.dadpat.common.bluetooth.exception;

public class BaseBluetoothException {
    private int code;
    private String msg;

    public BaseBluetoothException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "code=" + code + "msg=" + msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
