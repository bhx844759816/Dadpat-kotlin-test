package com.benbaba.dadpat.host.drum

import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException
import java.lang.Exception

class BlueState(var code: Int,var exception: BaseBluetoothException?,var msg: String = "") {

    // code 标识状态   msg 错误信息

    companion object {
        const val CONNECT_SUCCESS = 0x01
        const val CONNECT_ERROR = 0x02
        const val SEARCH_ERROR = 0x03
        const val SEARCH_NO_DEVICE = 0x04
        const val BREAK_OFF_CONNECT = 0x05
    }


}