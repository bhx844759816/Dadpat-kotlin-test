package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.SearchDeviceRepository
import com.bhx.common.mvvm.BaseViewModel
import com.code4a.wificonnectlib.WiFiHelper

/**
 * 搜索设备的ViewModel
 */
class SearchDeviceViewModel(application: Application) : BaseViewModel<SearchDeviceRepository>(application) {

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

    /**
     * 连接设备
     */
    fun connectDevice() {
        mRepository.connectDevice(getApplication())

    }

}