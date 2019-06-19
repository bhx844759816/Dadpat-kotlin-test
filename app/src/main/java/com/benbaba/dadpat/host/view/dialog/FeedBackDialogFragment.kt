package com.benbaba.dadpat.host.view.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.vm.FeedBackViewModel
import com.bhx.common.mvvm.BaseMVVMDialogFragment
import com.bhx.common.utils.ToastUtils
import kotlinx.android.synthetic.main.dialog_feedback.*

class FeedBackDialogFragment : BaseMVVMDialogFragment<FeedBackViewModel>() {
    private var mUser: User? = null //
    override fun getLayoutId(): Int = R.layout.dialog_feedback


    override fun getEventKey(): Any = FeedBackDialogFragment::class

    override fun initView(view: View?) {
        super.initView(view)
        mUser = arguments?.getSerializable("User") as User
        feedbackClose.clickWithTrigger {
            dismissAllowingStateLoss()
        }
        //发送反馈消息
        feedbackSend.clickWithTrigger {
            mUser?.let {
                val content = feedbackContent.text.toString().trim()
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.toastShort("反馈意见不能为空")
                    return@let
                }
                mViewModel.sendFeedBack(it.userName, it.userId, content)
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        private val TAG = FeedBackDialogFragment::class.simpleName


        fun show(activity: FragmentActivity) {
            show(activity, null)
        }

        fun show(activity: FragmentActivity, user: User?) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = FeedBackDialogFragment()
            }
            val bundle = Bundle()
            bundle.putSerializable("User", user)
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
            if (fragment != null && fragment is FeedBackDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }

}