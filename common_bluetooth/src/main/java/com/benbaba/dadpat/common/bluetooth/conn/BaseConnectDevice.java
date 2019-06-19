package com.benbaba.dadpat.common.bluetooth.conn;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import com.benbaba.dadpat.common.bluetooth.callback.OnConnectCallBack;
import com.benbaba.dadpat.common.bluetooth.exception.BluetoothExceptionCompat;
import com.benbaba.dadpat.common.bluetooth.search.BluetoothBondListener;

import java.lang.reflect.Method;

/**
 * 抽取得蓝牙连接得基类
 */
@SuppressLint("MissingPermission")
public abstract class BaseConnectDevice implements BluetoothBondListener {
    BluetoothDevice mDevice;
    OnConnectCallBack mCallBack;
    boolean isCancelBond;

    BaseConnectDevice(BluetoothDevice device) {
        mDevice = device;
    }

    /**
     * 设置连接状态得回调
     *
     * @param callBack
     */
    public void setConnectCallBack(OnConnectCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }

    /**
     * 连接设备
     */
    public void connect() {
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            mDevice.createBond();
        } else {
            realConnect();
        }
    }

    @Override
    public void cancelBond() {
        //取消绑定了
        isCancelBond = true;
        if (mCallBack != null) {
            mCallBack.connectError(BluetoothExceptionCompat.getCancelBondException());
        }
    }

    /**
     * 释放资源
     */
    public void release() {

    }

    /**
     * 绑定设备
     */
    private void bondDevice() {
        try {
            Method createBondMethod = mDevice.getClass().getMethod("createBond");
            createBondMethod.invoke(mDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bondSuccess() {
        if (mCallBack != null) {
            isCancelBond = false;
            mCallBack.bondSuccess();
        }
        realConnect();
    }

    @Override
    public void bonding() {
        //正在绑定
    }

    public void disConnect(){

    }

    /**
     * 绑定成功了去连接
     */
    abstract void realConnect();

}
