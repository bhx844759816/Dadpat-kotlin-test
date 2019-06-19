package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.drum.SocketManager
import com.benbaba.dadpat.host.vm.repository.ConnectDeviceRepository
import com.bhx.common.mvvm.BaseViewModel
import com.code4a.wificonnectlib.WiFiHelper

class ConnectDeviceViewModel(application: Application) : BaseViewModel<ConnectDeviceRepository>(application) {

    /**
     * 发送消息到玩具鼓
     */
    fun sendWifiInfoToDrum(msg: String) {
        mRepository.resetData()
        mRepository.sendWifiInfoToDrum(msg, getApplication())
    }
}