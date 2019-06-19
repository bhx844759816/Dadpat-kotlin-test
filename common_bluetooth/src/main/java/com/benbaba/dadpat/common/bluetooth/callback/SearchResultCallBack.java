package com.benbaba.dadpat.common.bluetooth.callback;

import android.bluetooth.BluetoothDevice;
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException;

import java.util.List;

/**
 * 搜索结果得回调
 */
public interface SearchResultCallBack {
    /**
     * 搜索到目标设备
     */
    void searchTargetDevice(BluetoothDevice device);

    /**
     * 开始搜索
     */
    void startSearch();

    /**
     * 停止搜索
     */
    void searchFinish(List<BluetoothDevice> list);

    /**
     * 搜索失败
     */
    void searchError(BaseBluetoothException exception);

}
