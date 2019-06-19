package com.benbaba.dadpat.common.bluetooth.search;

public interface BluetoothBondListener {
    /**
     * 正在配对
     */
    void bonding();

    /**
     * 取消配对
     */
    void cancelBond();

    /**
     * 配对成功
     */
    void bondSuccess();

    /**
     * 蓝牙断开
     */
    void bluetoothClose();
}
