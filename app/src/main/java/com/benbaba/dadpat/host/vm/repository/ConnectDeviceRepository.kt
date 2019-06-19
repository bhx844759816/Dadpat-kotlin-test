package com.benbaba.dadpat.host.vm.repository

import android.content.Context
import android.util.Log
import android.util.Pair
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.SocketManager
import com.benbaba.dadpat.host.http.applySchedulers
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.code4a.wificonnectlib.WiFiHelper
import io.reactivex.Observable

class ConnectDeviceRepository : BaseEventRepository() {

    private val  maxConnectCounts = 3
    private var currentConnectCounts = 0
    /**
     * 连接设备
     */
    private fun connectDevice(msg: String, context: Context) {
        LogUtils.i("开始连接设备")
        if(currentConnectCounts > maxConnectCounts){
            ToastUtils.toastShort("连接设备失败")
            return
        }
        currentConnectCounts++
        WiFiHelper.getInstance(context).connectWiFi(
            context,
            Constants.DEVICE_WIFI_SSID,
            Constants.DEVICE_WIFI_PASSWORD, object : WiFiHelper.WiFiConnectListener {
                override fun connectStart() {
                    sendData(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
                }

                override fun connectResult(ssid: String?, isSuccess: Boolean) {
                    LogUtils.i("连接玩具鼓Dadpat的wifi成功")
                    sendWifiInfoToDrum(msg, context)
                }

                override fun connectEnd() {
                    sendData(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
                }

            }
        )
    }

    /**
     * 发送消息到玩具鼓
     */
    fun sendWifiInfoToDrum(msg: String, context: Context) {
        val wifiInfo = WiFiHelper.getInstance(context).currentWifiSSID
        Log.i("TAG", "wifiInfo:$wifiInfo")
        if (Constants.DEVICE_WIFI_SSID == wifiInfo) {
            sendWifiInfo(msg, context)
        } else {
            connectDevice(msg, context)
        }

    }

    /**
     * 发送wifi信息到玩具鼓最大重复5次
     */
    private fun sendWifiInfo(msg: String, context: Context) {
        addDisposable(
            Observable.create<Boolean> {
                val counts = 0
                var results: Pair<String, String>? = null
                while (counts < 5) {
                    results = SocketManager.getInstance()
                        .sendMsgForReceive(msg, "255.255.255.255", 10025, 10026)
                    if (results != null) {
                        break
                    }
                    Thread.sleep(500)
                }
                if (results != null) {
                    it.onNext(true)
                } else {
                    it.onNext(false)
                }
                it.onComplete()
            }.compose(applySchedulers())
                .subscribe {
                    WiFiHelper.getInstance(context).disconnectWifi()
                    sendData(Constants.EVENT_KEY_SEND_WIFI_INFO, Constants.TAG_WIFI_SEND_DEVICE_RESULT, it)
                }

        )
    }

    fun resetData() {
        currentConnectCounts = 0
    }
}