package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.event.LiveBus
import com.bhx.common.utils.ToastUtils
import kotlinx.android.synthetic.main.dialog_device_connect.*
import kotlinx.android.synthetic.main.dialog_device_connect2.*

/**
 * 设备连接的对话框
 */
class DeviceConnectDialogFragment2 : BaseDialogFragment() {
    var isConnect = false
    override fun getLayoutId(): Int = R.layout.dialog_device_connect2
    override fun initView(view: View?) {
        super.initView(view)
        isConnect = SmartBle.getInstance().isConnect
        if (isConnect) {
            bluetoothConnected.background = resources.getDrawable(R.drawable.drawable_bluetooth_connected_state)
        } else {
            bluetoothConnected.background = resources.getDrawable(R.drawable.drawable_bluetooth_not_connect_state)
        }
        bluetoothConnectedText.isChecked = isConnect
        bluetoothConnected.clickWithTrigger {
            //点击蓝牙连接
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_SELECT_DEVICE_CONNECT, 0)
            dismissAllowingStateLoss()
        }
        wifiConnected.clickWithTrigger {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_SELECT_DEVICE_CONNECT, 1)
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private val TAG = DeviceConnectDialogFragment2::class.simpleName

        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = DeviceConnectDialogFragment2()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as DeviceConnectDialogFragment2
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}