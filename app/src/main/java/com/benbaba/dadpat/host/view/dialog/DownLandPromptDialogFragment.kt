package com.benbaba.dadpat.host.view.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.event.LiveBus
import kotlinx.android.synthetic.main.dialog_downland_prompt.*

/**
 * 4G网络下载提示框
 */
class DownLandPromptDialogFragment : BaseDialogFragment() {
    override fun getLayoutId(): Int = R.layout.dialog_downland_prompt

    override fun initView(view: View?) {
        super.initView(view)
        val fileSize = arguments?.getString("fileSize")
        content.text = String.format("当前是移动网络需要下载$fileSize,是否继续下载")
        confirm.clickWithTrigger {
            dismissAllowingStateLoss()
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_ALLOW_NETWORK_DOWNLAND, true)
        }
        cancel.clickWithTrigger {
            dismissAllowingStateLoss()
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_ALLOW_NETWORK_DOWNLAND, false)
        }
    }

    companion object {
        private val TAG = DownLandPromptDialogFragment::class.simpleName

        fun show(activity: FragmentActivity, fileSize: String) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = DownLandPromptDialogFragment()
            }
            val bundle = Bundle()
            bundle.putString("fileSize", fileSize)
            fragment.arguments = bundle
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun show(activity: FragmentActivity) {
            show(activity, "")
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as DownLandPromptDialogFragment
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}