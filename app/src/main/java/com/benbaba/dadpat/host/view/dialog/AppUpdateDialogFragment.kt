package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.update.AppUpdateCallBack
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.event.LiveBus
import kotlinx.android.synthetic.main.dialog_app_update.*

class AppUpdateDialogFragment : BaseDialogFragment() {
    private var appUpdateCallBack: AppUpdateCallBack? = null
    override fun getLayoutId(): Int = R.layout.dialog_app_update

    override fun initView(view: View?) {
        super.initView(view)
        isCancelable = false
        val versionName = arguments?.getString("versionName")
        val apkSize = arguments?.getString("apkSize")
        updateVersion.text = String.format("有新版%s可以下载", versionName)
        updateApkSize.text = String.format("需要下载得大小:%s", apkSize)
        updateAppCancel.clickWithTrigger {
            dismissAllowingStateLoss()
        }
        updateAppConfirm.clickWithTrigger {
            // 下载更新
            appUpdateCallBack?.confirmUpdate()
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private val TAG = AppUpdateDialogFragment::class.simpleName
        fun show(
            activity: FragmentActivity,
            versionName: String,
            apkSize: String,
            appUpdateCallBack: AppUpdateCallBack
        ) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = AppUpdateDialogFragment()
                fragment.appUpdateCallBack = appUpdateCallBack
            }
            val bundle = Bundle()
            bundle.putString("versionName", versionName)
            bundle.putString("apkSize", apkSize)
            fragment.arguments = bundle
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity) {
            val fragment = activity.supportFragmentManager?.findFragmentByTag(TAG)
            if (fragment != null && fragment is PersonInfoDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}