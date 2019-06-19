package com.benbaba.dadpat.host.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.DeviceUdpUtils
import com.benbaba.dadpat.host.drum.SocketManager
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.vm.ConnectDeviceViewModel
import com.bhx.common.mvvm.BaseMVVMFragment
import com.bhx.common.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_connect_device.*

/**
 * 连接设备玩具鼓的WIFI
 */
class ConnectDeviceFragment : BaseMVVMFragment<ConnectDeviceViewModel>() {
    private var sendSSID: String? = null
    override fun getLayoutId(): Int = R.layout.fragment_connect_device
    var isOpenPsd = false

    override fun getEventKey(): Any = Constants.EVENT_KEY_SEND_WIFI_INFO
    override fun lazyLoad() {
        super.lazyLoad()
        //控制密码显示隐藏
        wifiPsdControl.clickWithTrigger {
            isOpenPsd = !isOpenPsd
            if (isOpenPsd) {
                wifiPsdInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                wifiPsdControl.setBackgroundResource(R.drawable.login_eye_open)
            } else {
                wifiPsdInput.transformationMethod = PasswordTransformationMethod.getInstance()
                wifiPsdControl.setBackgroundResource(R.drawable.login_eye_close)
            }
        }
        //发送wifi信息
        wifiInfoSend.clickWithTrigger {
            val password = wifiPsdInput.text.toString().trim()
            if (TextUtils.isEmpty(password)) {
                ToastUtils.toastShort("请输入wifi密码")
                return@clickWithTrigger
            }
            val msg = DeviceUdpUtils.getWifiSettingJson(sendSSID, password)
            mViewModel.sendWifiInfoToDrum(msg)
        }
        //
    }

    override fun initView(bundle: Bundle?) {
        super.initView(bundle)

        registerObserver(Constants.TAG_WIFI_SEND_DEVICE_RESULT, Boolean::class.java).observe(this, Observer {
            if (it) {
                activity?.finish()
            } else {
                ToastUtils.toastShort("发送玩具鼓wifi信息失败")
            }
        })
    }


    /**
     *
     */
    fun setSendSSID(ssid: String) {
        sendSSID = ssid
    }
}