package com.benbaba.dadpat.host.drum

import android.bluetooth.BluetoothDevice
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.common.bluetooth.config.SearchConfig
import com.benbaba.dadpat.common.bluetooth.callback.OnConnectCallBack
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException
import com.benbaba.dadpat.common.bluetooth.callback.SearchResultCallBack
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils

/**
 * 蓝牙操作类
 * Created by Administrator on 2019/6/12.
 */
class BluetoothCompat {
    var mConnectBluetoothDevice: BluetoothDevice? = null
    var receiveCallBack: ((String) -> Unit)? = null //接收消息的回调
    var aidlConnectCallBack: ((Boolean) -> Unit)? = null //aidi连接结果的回调
    var aidlDisConnectCallBack: (() -> Unit)? = null //断开连接的回调
    var connectSuccessCallBack: (() -> Unit)? = null//
    var connectErrorCallBack: ((BaseBluetoothException?) -> Unit)? = null
    var isConnecting = false
    private val bluetoothConnectCallBack = object : OnConnectCallBack {
        override fun disConnect() {
            aidlDisConnectCallBack?.invoke()
        }

        override fun receiveMsg(bytes: ByteArray?) {
            bytes?.let {
                val msg = String(it)
                LogUtils.i("接收到鼓的消息1：$msg")
                receiveCallBack?.invoke(msg)
            }
        }

        override fun connectSuccess(device: BluetoothDevice?) {
            isConnecting = false
            mConnectBluetoothDevice = device
            //连接成功
            connectSuccessCallBack?.invoke()
            //连接成功回调给AIDL
            aidlConnectCallBack?.invoke(true)
        }

        override fun connectError(exception: BaseBluetoothException?) {
            //
            isConnecting = false
            //
            mConnectBluetoothDevice = null
            //连接失败
            connectErrorCallBack?.invoke(exception)
            //连接失败回调给Aidl
            aidlConnectCallBack?.invoke(false)

        }

        override fun bondSuccess() {
            ToastUtils.toastShort("绑定成功")
        }

    }

    init {
        val config = SearchConfig()
        config.deviceName = "爸爸拍拍"
        SmartBle.getInstance().config(config)
    }

    /**
     * 开始搜索附近的爸爸拍拍蓝牙设备
     */
    fun startSearchDevice() {
        SmartBle.getInstance().search()
    }

    /**
     * 注册搜索监听
     */
    fun registerBluetoothSearchCallBack(callBack: SearchResultCallBack) {
        SmartBle.getInstance().registerBluetoothSearchCallBack(callBack)
    }

    /**
     * 取消注册搜索监听
     */
    fun unRegisterBluetoothSearchCallBack() {
        SmartBle.getInstance().unRegisterBluetoothSearchCallBack()
    }

    /**
     * 停止搜索附近的蓝牙设备
     */
    fun stopSearchDevice() {
//        SmartBle.getInstance().stopSearch()
    }

    /**
     * 连接指定的设备
     */
    fun connectBlueTooth(device: BluetoothDevice) {
        if (!SmartBle.getInstance().isBlueOpen) {
            ToastUtils.toastShort("蓝牙已关闭，请先打开蓝牙")
            return
        }
        if (!isConnecting) {
            LogUtils.i("连接蓝牙的地址${device.address}")
            SmartBle.getInstance().disConnect()
            SmartBle.getInstance().connect(device, bluetoothConnectCallBack)
        }

    }

    /**
     * 连接蓝牙
     */
    fun connectBlueTooth() {
        mConnectBluetoothDevice?.let {
            SmartBle.getInstance().connect(it, bluetoothConnectCallBack)
        }
    }

    /**
     * 发送消息
     */
    fun sendMsg(msg: String?): Boolean {
        LogUtils.i("cocos蓝牙 sendMsg$msg")
        return SmartBle.getInstance().sendMsg(msg)
    }

    /**
     * 是否连接
     */
    fun isConnect(): Boolean {
        return SmartBle.getInstance().isConnect
    }

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = BluetoothCompat()
    }

}