package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.utils.LogUtils
import kotlinx.android.synthetic.main.dialog_confirm_delete.*
import kotlinx.android.synthetic.main.dialog_loading.*
import pl.droidsonroids.gif.GifDrawable

/**
 * 加载对话框
 */
class LoadingDialogFragment : BaseDialogFragment() {
    private var gifPath = "gif/loading_dialog.gif"
    override fun getLayoutId(): Int {
        return R.layout.dialog_loading
    }

    override fun initView(view: View) {
      dialog?.setCanceledOnTouchOutside(false)
    }

    override fun initData() {
        val gifPath_ = arguments?.getString("gifPath").toString()
        if (!TextUtils.isEmpty(gifPath_)) {
            gifPath = gifPath_
        }
        val gifDrawable = activity?.assets?.let { GifDrawable(it, gifPath) }
        gifDrawable.let {
            loadingView.setImageDrawable(it)
        }

    }

    companion object {
        private val TAG = LoadingDialogFragment::class.simpleName

        fun show(activity: FragmentActivity) {
            show(activity, "gif/loading_dialog.gif")
        }

        fun show(activity: FragmentActivity, gifPath: String) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = LoadingDialogFragment()
            }
            val bundle = Bundle()
            bundle.putString("gifPath", gifPath)
            fragment.arguments = bundle
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG)
            if (fragment != null && fragment is LoadingDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }

}