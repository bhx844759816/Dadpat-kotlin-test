package com.benbaba.dadpat.host.vm.repository

import android.content.Context
import com.benbaba.dadpat.host.config.Constants
import com.code4a.wificonnectlib.WiFiHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * wifi列表的请求类
 */
class WifiListRepository : BaseEventRepository() {

    private var mSearchDisposable: Disposable? = null

    /**
     * 搜索wifi
     */
    fun startSearchWifi(context: Context) {
        mSearchDisposable = Observable.interval(0, 8, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .map {
                //开始搜索
                WiFiHelper.getInstance(context).startScan()
                //获取搜索结果
                val list = WiFiHelper.getInstance(context).getScanResultsOutTarget(Constants.DEVICE_WIFI_SSID)
                //发送搜索结果到主界面
                sendData(Constants.EVENT_KEY_WIFI_LIST, Constants.TAG_WIFI_LIST_RESULT, list)
            }
            .subscribe()
    }

    /**
     * 停止搜索wifi
     */
    fun stopSearchWifi() {
        mSearchDisposable?.let {
            it.dispose()
            mSearchDisposable = null
        }
    }


}