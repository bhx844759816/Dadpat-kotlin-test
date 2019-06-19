package com.benbaba.dadpat.host.view.dialog

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.bhx.common.base.BaseDialogFragment
import android.os.Bundle
import android.view.View
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.event.LiveBus
import kotlinx.android.synthetic.main.dialog_confirm_delete.*

/**
 * 确认删除Dialog
 *
 */
class ConfirmDeleteDialogFragment : BaseDialogFragment() {
    var isConfirm = false
    override fun getLayoutId(): Int {
        return R.layout.dialog_confirm_delete
    }

    override fun initView(view: View?) {
        super.initView(view)
        //取消删除
        promptCancel.clickWithTrigger {

            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_CONFIRM_DELETE_PLUGIN, false)
            dismissAllowingStateLoss()
        }
        //确认删除
        promptConfirm.clickWithTrigger {
            isConfirm = true
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_CONFIRM_DELETE_PLUGIN, true)
            dismissAllowingStateLoss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (!isConfirm) {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_CONFIRM_DELETE_PLUGIN, false)
        }
    }

    override fun initData() {
        super.initData()
        val pluginName = arguments?.getString("pluginName")
        promptMessage.text = StringBuffer().append("是否删除").append(pluginName).toString()
    }

    companion object {
        private val TAG = ConfirmDeleteDialogFragment::class.simpleName

        fun show(activity: FragmentActivity, pluginName: String) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = ConfirmDeleteDialogFragment()
            }
            val bundle = Bundle()
            bundle.putString("pluginName", pluginName)
            fragment.arguments = bundle
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as ConfirmDeleteDialogFragment
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}