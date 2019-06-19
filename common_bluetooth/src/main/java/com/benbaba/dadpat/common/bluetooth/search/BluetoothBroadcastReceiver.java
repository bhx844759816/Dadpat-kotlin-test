package com.benbaba.dadpat.common.bluetooth.search;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 蓝牙广播接收者
 */
@SuppressLint("MissingPermission")
public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private BluetoothSearchListener mSearchListener;
    private BluetoothBondListener mBondListener;
    private List<BluetoothDevice> mDeviceList;

    public BluetoothBroadcastReceiver() {
        mDeviceList = new ArrayList<>();
    }

    /**
     * 设置蓝牙搜索得回调
     *
     * @param listener
     */
    public void setBluetoothSearchListener(BluetoothSearchListener listener) {
        Log.i("TAG","setBluetoothSearchListener");
        mSearchListener = listener;
    }

    /**
     * 设置配对监听
     *
     * @param bondListener
     */
    public void setBluetoothBondListener(BluetoothBondListener bondListener) {
        mBondListener = bondListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_FOUND: { // 发现蓝牙设备广播
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !TextUtils.isEmpty(device.getName())) {
                    mDeviceList.add(device);
                    // 回调搜索结果
                    if (mSearchListener != null) {
                        mSearchListener.searchDevice(device);
                    }
                }
                break;
            }
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {// 开始发现蓝牙广播
                Log.i("TAG","开始发现蓝牙广播");
                if (mSearchListener != null) {
                    mDeviceList.clear();
                    mSearchListener.startSearch();
                }
                break;
            }
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: { // 结束发现蓝牙广播
                if (mSearchListener != null) {
                    mSearchListener.searchFinish(mDeviceList);
                }
                break;
            }
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("TAG", "bondState=" + device.getBondState());
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        if (mBondListener != null) {
                            mBondListener.cancelBond();
                        }
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        if (mBondListener != null) {
                            mBondListener.bonding();
                        }
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        if (mBondListener != null) {
                            mBondListener.bondSuccess();
                        }
                        break;
                }
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED: {// 蓝牙状态的改变
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                Log.i("TAG", "state=" + state);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON: //
                        break;
                    case BluetoothAdapter.STATE_ON: // 成功打开蓝牙
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (mBondListener != null) {
                            mBondListener.bluetoothClose();
                        }
                        break;
                }

                break;
            }
        }

    }
}
