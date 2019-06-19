package com.benbaba.dadpat.common.bluetooth.callback;

import android.bluetooth.BluetoothDevice;
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException;

/**
 * 连接得回调
 */
public interface OnConnectCallBack {
    /**
     * 连接成功
     */
    void connectSuccess(BluetoothDevice device);

    /**
     * 连接失败
     *
     * @param exception
     */
    void connectError(BaseBluetoothException exception);

    /**
     * 绑定成功
     */
    void bondSuccess();


    void receiveMsg(byte[] bytes);


    void disConnect();
}
