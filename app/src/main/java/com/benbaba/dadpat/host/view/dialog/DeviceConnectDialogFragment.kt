package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.event.LiveBus
import kotlinx.android.synthetic.main.dialog_device_connect.*

/**
 * 设备连接的对话框
 */
class DeviceConnectDialogFragment : BaseDialogFragment() {
    override fun getLayoutId(): Int = R.layout.dialog_device_connect
    override fun initView(view: View?) {
        super.initView(view)
        //确认蓝牙连接
        bluetoothConnect.clickWithTrigger {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_SELECT_DEVICE_CONNECT, 0)
            dismissAllowingStateLoss()
        }
        //确认wifi连接
        wifiConnect.clickWithTrigger {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_SELECT_DEVICE_CONNECT, 1)
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private val TAG = DeviceConnectDialogFragment::class.simpleName

        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = DeviceConnectDialogFragment()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as DeviceConnectDialogFragment
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}