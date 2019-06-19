package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.utils.AppManager
import kotlinx.android.synthetic.main.dialog_restart_app.*

class ReStartAPPDialogFragment : BaseDialogFragment() {
    override fun getLayoutId(): Int = R.layout.dialog_restart_app

    override fun initView(view: View?) {
        super.initView(view)
        //取消重新启动
        restartAppCancel.clickWithTrigger {
            dismissAllowingStateLoss()
        }
        //确认重新启动
        restartAppConfirm.clickWithTrigger {
            AppManager.getAppManager().restartApp(context)
        }
    }

    companion object {
        private val TAG = ReStartAPPDialogFragment::class.simpleName


        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = ReStartAPPDialogFragment()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG)
            if (fragment != null && fragment is ReStartAPPDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}