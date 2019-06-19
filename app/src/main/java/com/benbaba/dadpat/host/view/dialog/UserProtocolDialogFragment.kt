package com.benbaba.dadpat.host.view.dialog

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.utils.FileUtils
import kotlinx.android.synthetic.main.dialog_protocol.*
import java.io.IOException

/**
 * 用户协议的Dialog
 */
class UserProtocolDialogFragment : BaseDialogFragment() {
    override fun getLayoutId(): Int = R.layout.dialog_protocol
    override fun initView(view: View?) {
        try {
            val inputStream = activity?.assets?.open("protocol.txt")
            inputStream?.let {
                val msg = FileUtils.readText(it)
                content.text = msg
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        confirm.clickWithTrigger {
            dismissAllowingStateLoss()
        }


    }

    companion object {
        private val TAG = UserProtocolDialogFragment::class.simpleName


        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = UserProtocolDialogFragment()
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
            if (fragment != null && fragment is UserProtocolDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}