package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.WifiListRepository
import com.bhx.common.mvvm.BaseViewModel
import com.code4a.wificonnectlib.WiFiHelper

class WifiListViewModel(application: Application) : BaseViewModel<WifiListRepository>(application) {

    /**
     * 开始搜索附近的设备
     */
    fun startSearchWifi() {
        mRepository.startSearchWifi(getApplication())
    }

    /**
     * 停止搜索附近的设备
     */
    fun stopSearchWifi() {
        mRepository.stopSearchWifi()
    }
}