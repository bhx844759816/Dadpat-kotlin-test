package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_device_setting_help.*

/**
 * 设置wifi界面的帮助对话框
 */
class DeviceSettingHelpDialogFragment : BaseDialogFragment() {
    override fun getLayoutId(): Int = R.layout.dialog_device_setting_help
    override fun initView(view: View?) {
        super.initView(view)
        confirm.clickWithTrigger {
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private val TAG = DeviceSettingHelpDialogFragment::class.simpleName
        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = DeviceSettingHelpDialogFragment()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity) {
            val fragment = activity.supportFragmentManager?.findFragmentByTag(TAG)
            if (fragment != null && fragment is DeviceSettingHelpDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }

}