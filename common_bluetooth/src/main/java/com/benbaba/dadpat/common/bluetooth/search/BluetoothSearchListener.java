package com.benbaba.dadpat.common.bluetooth.search;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * 抽象类 蓝牙扫描得回调
 */
public abstract class BluetoothSearchListener {

    /**
     * 搜索到目标设备
     *
     * @param device 搜索到目标设备
     */
    protected abstract void searchDevice(BluetoothDevice device);

    /**
     * 开始搜索
     */
    protected abstract void startSearch();

    /**
     * 搜索结束
     *
     * @param devices 搜索到全部得设备列表
     */
    protected abstract void searchFinish(List<BluetoothDevice> devices);
}
