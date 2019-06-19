package com.benbaba.dadpat.host.vm.repository

import android.content.Context
import com.benbaba.dadpat.host.config.Constants
import com.bhx.common.utils.LogUtils
import com.code4a.wificonnectlib.WiFiHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 搜索设备的Repository
 */
class SearchDeviceRepository : BaseEventRepository() {
    private var mSearchDisposable: Disposable? = null

    /**
     * 每隔5s搜索一次附近的wifi
     */
    fun startSearchWifi(context: Context) {
        mSearchDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                if (mSearchDisposable != null && !mSearchDisposable!!.isDisposed) {
                    //开始搜索
                    WiFiHelper.getInstance(context).startScan()
                    //获取搜索结果
                    val list = WiFiHelper.getInstance(context).getTargetScanResults(Constants.DEVICE_WIFI_SSID)
                    LogUtils.i("搜索结果：$list")
                    //发送搜索结果到主界面
                    sendData(Constants.EVENT_KEY_SEARCH_DEVICE, Constants.TAG_SEARCH_DEVICE_RESULT, list)
                }
            }
    }

    /**
     * 停止搜索
     */
    fun stopSearchWifi() {
        mSearchDisposable?.let {
            it.dispose()
            mSearchDisposable = null
        }
    }

    /**
     * 连接设备
     */
    fun connectDevice(context: Context) {
        LogUtils.i("开始连接设备")
        WiFiHelper.getInstance(context).connectWiFi(
            context,
            Constants.DEVICE_WIFI_SSID,
            Constants.DEVICE_WIFI_PASSWORD, object : WiFiHelper.WiFiConnectListener {
                override fun connectStart() {
                    sendData(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
                }

                override fun connectResult(ssid: String?, isSuccess: Boolean) {
                    sendData(Constants.EVENT_KEY_SEARCH_DEVICE, Constants.TAG_CONNECT_DEVICE_RESULT, isSuccess)
                }

                override fun connectEnd() {
                    sendData(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
                }

            }
        )
    }

}