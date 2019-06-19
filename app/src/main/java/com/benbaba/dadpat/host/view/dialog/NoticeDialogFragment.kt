package com.benbaba.dadpat.host.view.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.adapter.NoticeAdapter
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.NoticeBean
import com.benbaba.dadpat.host.vm.NoticeViewModel
import com.bhx.common.mvvm.BaseMVVMDialogFragment
import kotlinx.android.synthetic.main.dialog_notice.*

/**
 * 公告
 */
class NoticeDialogFragment : BaseMVVMDialogFragment<NoticeViewModel>() {
    private var mNoticeList: MutableList<NoticeBean> = ArrayList() //插件集合对象
    private lateinit var mAdapter: NoticeAdapter
    override fun getLayoutId(): Int = R.layout.dialog_notice

    override fun getEventKey(): Any = Constants.EVENT_KEY_NOTICE

    @SuppressLint("WrongConstant")
    override fun initView(view: View?) {
        super.initView(view)
        mAdapter = NoticeAdapter(context!!, mNoticeList)
        messageRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        messageRecyclerView.adapter = mAdapter
        registerObserver(Constants.TAG_GET_NOTICES, List::class.java).observe(this, Observer {
            val list = it as List<NoticeBean>
            mNoticeList.clear()
            mNoticeList.addAll(list)
            mAdapter.notifyDataSetChanged()
        })
        mViewModel.getNotices()

    }

    companion object {
        private val TAG = NoticeDialogFragment::class.simpleName

        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = NoticeDialogFragment()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as NoticeDialogFragment
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}