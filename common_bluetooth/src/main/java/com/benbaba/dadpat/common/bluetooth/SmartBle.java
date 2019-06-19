package com.benbaba.dadpat.common.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import com.benbaba.dadpat.common.bluetooth.config.SearchConfig;
import com.benbaba.dadpat.common.bluetooth.conn.BaseConnectDevice;
import com.benbaba.dadpat.common.bluetooth.conn.BluetoothConnect;
import com.benbaba.dadpat.common.bluetooth.callback.OnConnectCallBack;
import com.benbaba.dadpat.common.bluetooth.exception.BluetoothExceptionCompat;
import com.benbaba.dadpat.common.bluetooth.search.BluetoothBroadcastReceiver;
import com.benbaba.dadpat.common.bluetooth.search.BluetoothSearchListener;
import com.benbaba.dadpat.common.bluetooth.callback.SearchResultCallBack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 蓝牙操作类
 */
@SuppressLint("MissingPermission")
public class SmartBle {

    private static SmartBle mInstance;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothBroadcastReceiver mReceiver;// 蓝牙广播监听
    private Application mApplication; //
    private SearchConfig mSearchConfig;// 搜索得配置文件
    private BaseConnectDevice mConnectDevice;
    private SearchResultCallBack mSearchCallBack;

    public static SmartBle getInstance() {
        if (mInstance == null) {
            synchronized (SmartBle.class) {
                if (mInstance == null) {
                    mInstance = new SmartBle();
                }
            }
        }
        return mInstance;
    }

    /**
     * 需要在Application进行初始化
     *
     * @param application
     * @throws RuntimeException
     */
    public void init(Application application) throws RuntimeException {
        Log.i("TAG", "SmartBle init");
        mApplication = application;
        if (mApplication.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBluetoothManager = (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            //
            registerReceiver();
        }

    }

    /**
     * 搜索蓝牙设备
     */
    public void search() {
        if (mReceiver == null) {
            return;
        }
        if (mBluetoothAdapter == null) {
            return;
        }
        // 蓝牙未打开
        if (!isBlueOpen()) {
            openBlueAsyn();
            if (mSearchCallBack != null)
                mSearchCallBack.searchError(BluetoothExceptionCompat.getBluetoothNotOpenException());
            return;
        }
        //关闭正在搜索得
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索(耗时12s),搜索结果会通过广播进行回调 如果返回false说明蓝牙正在打开或者未打开
        mBluetoothAdapter.startDiscovery();
    }

    public void registerBluetoothSearchCallBack(SearchResultCallBack callBack) {
        if (mReceiver != null) {
            mSearchCallBack = callBack;
            mReceiver.setBluetoothSearchListener(new BluetoothSearchListenerImpl(callBack));
        }
    }

    public void unRegisterBluetoothSearchCallBack() {
        if (mReceiver != null) {
            mSearchCallBack = null;
            mReceiver.setBluetoothSearchListener(null);
        }
    }

    /**
     * 停止搜索
     */
    public void stopSearch() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 获取当前连接得蓝牙设备
     *
     * @return
     */
    public synchronized List<BluetoothDevice> getConnectBluetoothDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i("TAG", "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.i("TAG", "connected:" + device.getName());
                        deviceList.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
    }

    /**
     * 搜索到得回调
     */
    private class BluetoothSearchListenerImpl extends BluetoothSearchListener {
        SearchResultCallBack callBack;

        BluetoothSearchListenerImpl(SearchResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected void searchDevice(BluetoothDevice device) {
            if (mSearchConfig != null) {
                // 过滤目标蓝牙设备
                String deviceName = mSearchConfig.getDeviceName();
                String deviceMacAddress = mSearchConfig.getMacAddress();
                Log.i("TAG2", "搜索到设备:" + device.getName());
                if (device.getName().equals(deviceName) || device.getAddress().equals(deviceMacAddress)) {
                    // 如果设备名称或者设备地址有一个满足条件就符合
                    Log.i("TAG2", "爸爸拍拍 MAC:" + device.getAddress());
                    if (callBack != null)
                        callBack.searchTargetDevice(device);
                }
            }
        }

        @Override
        protected void startSearch() {
            if (callBack != null)
                callBack.startSearch();
        }

        @Override
        protected void searchFinish(List<BluetoothDevice> devices) {
            if (callBack != null) {
                Iterator<BluetoothDevice> it = devices.iterator();
                while (it.hasNext()) {
                    if (!it.next().getName().equals(mSearchConfig.getDeviceName())) {
                        it.remove();
                    }
                }
                callBack.searchFinish(devices);
            }
            stopSearch();
        }
    }

    /**
     * 注册蓝牙广播监听
     */
    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new BluetoothBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mApplication.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 取消注册蓝牙广播监听
     */
    public void unRegisterReceiver() {
        if (mReceiver != null) {
            mApplication.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }


    public List<BluetoothDevice> getBondDevice() {
        List<BluetoothDevice> list = new ArrayList<>();
        Set<BluetoothDevice> set = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : set) {
            Log.i("TAG2", "绑定的设备：" + device.getAddress() + ",名字：" + device.getName());
            String deviceName = mSearchConfig.getDeviceName();
            String deviceMacAddress = mSearchConfig.getMacAddress();
            if (device.getName().equals(deviceName) || device.getAddress().equals(deviceMacAddress)) {
                list.add(device);
            }
        }

        return list;
    }


    /**
     * 蓝牙是否连接
     *
     * @return
     */
    public boolean isConnect() {
        if (mConnectDevice instanceof BluetoothConnect) {
            return ((BluetoothConnect) mConnectDevice).isConnect();
        }
        return false;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public boolean sendMsg(String msg) {
        if (mConnectDevice instanceof BluetoothConnect) {
            return ((BluetoothConnect) mConnectDevice).sendMsg(msg);
        }
        return false;
    }


    /**
     * 连接A2dp
     *
     * @param device
     * @param callBack
     */
    public void connect(BluetoothDevice device, OnConnectCallBack callBack) {


        if (mReceiver == null) {
            throw new RuntimeException("mReceiver can not null,please call method registerReceiver()");
        }
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("mBluetoothAdapter can not null");
        }
        // 当前mConnectDevice是否为空 且是否是BluetoothConnect得实例
        if (!(mConnectDevice instanceof BluetoothConnect)) {
            mConnectDevice = new BluetoothConnect(device);
            mConnectDevice.setConnectCallBack(callBack);
            mReceiver.setBluetoothBondListener(mConnectDevice);
        } else {
            //设置需要连接的设备
            mConnectDevice.setDevice(device);
        }
        //连接
        mConnectDevice.connect();

    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (mConnectDevice != null) {
            mConnectDevice.disConnect();
        }
    }

    /**
     * 配置搜索条件
     *
     * @param config
     * @return
     */
    public SmartBle config(SearchConfig config) {
        this.mSearchConfig = config;
        return this;
    }

    /**
     * 是否支持蓝牙
     *
     * @return
     */
    private boolean isSupportBlue() {
        return mBluetoothAdapter == null;
    }

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    public boolean isBlueOpen() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 异步打开蓝牙
     */
    public void openBlueAsyn() {
        if (isSupportBlue()) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * 同步打开蓝牙
     */
    public void openBlueSync(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取蓝牙适配器
     *
     * @return
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * 获取全局Application
     *
     * @return
     */
    public Application getApplication() {
        return mApplication;
    }
}
