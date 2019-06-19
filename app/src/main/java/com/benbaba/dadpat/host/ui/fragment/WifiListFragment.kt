package com.benbaba.dadpat.host.ui.fragment

import android.net.wifi.ScanResult
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.adapter.WifiListAdapter
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.ui.DrumSettingActivity
import com.benbaba.dadpat.host.vm.WifiListViewModel
import com.bhx.common.mvvm.BaseMVVMFragment
import com.bhx.common.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_search_device.*
import kotlinx.android.synthetic.main.fragment_wifi_list.*

/**
 * 显示wifi列表
 */
class WifiListFragment : BaseMVVMFragment<WifiListViewModel>() {
    private lateinit var mAdapter: WifiListAdapter
    private var mScanResults = mutableListOf<ScanResult>()
    override fun getLayoutId(): Int = R.layout.fragment_wifi_list

    override fun getEventKey(): Any = Constants.EVENT_KEY_WIFI_LIST

    override fun lazyLoad() {
        mAdapter = WifiListAdapter(context, mScanResults)
        wifiListRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        wifiListRecyclerView.adapter = mAdapter
        //获取wifi列表
        registerObserver(Constants.TAG_WIFI_LIST_RESULT, List::class.java).observe(this,
            Observer {
                val list = it as List<ScanResult>
                mScanResults.clear()
                mScanResults.addAll(list)
                mAdapter.notifyDataSetChanged()
            })
        //获取点击的wifi的列表Item
        registerObserver(Constants.TAG_WIFI_LIST_SELECT, Int::class.java).observe(this,
            Observer {
                //跳转到发送wifi的界面
                //跳转到选择wifi列表的Fragment
                if (activity is DrumSettingActivity) {
                    (activity as DrumSettingActivity).showConnectDevice(mScanResults[it].SSID)
                }
            })
    }

    override fun onVisible() {
        super.onVisible()
        LogUtils.i("WifiListFragment onVisible")
        mViewModel.startSearchWifi()
    }

    override fun onInVisible() {
        super.onInVisible()
        LogUtils.i("WifiListFragment onInVisible")
        mViewModel.stopSearchWifi()
    }

}