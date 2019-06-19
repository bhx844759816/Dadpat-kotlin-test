package com.benbaba.dadpat.host.ui

import androidx.fragment.app.Fragment
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.ui.fragment.ConnectDeviceFragment
import com.benbaba.dadpat.host.ui.fragment.SearchDeviceFragment
import com.benbaba.dadpat.host.ui.fragment.WifiListFragment
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.vm.DrumSettingViewModel
import com.bhx.common.mvvm.BaseMVVMActivity
import kotlinx.android.synthetic.main.activity_drum_setting.*

/**
 * 设置玩具鼓的界面
 */
class DrumSettingActivity : BaseMVVMActivity<DrumSettingViewModel>() {
    private lateinit var mSearchDeviceFragment: SearchDeviceFragment
    private lateinit var mWifiListFragment: WifiListFragment
    private lateinit var mConnDeviceFragment: ConnectDeviceFragment
    private lateinit var mCurrentFragment: Fragment
    private lateinit var mCurrentState: String
    override fun getEventKey(): Any = DrumSettingActivity::class.simpleName!!

    override fun getLayoutId(): Int = R.layout.activity_drum_setting

    override fun initView() {
        initFragment()
        back.clickWithTrigger {
            onBackPressed()
        }
    }

    /**
     * 初始化Fragment
     */
    private fun initFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        mSearchDeviceFragment = SearchDeviceFragment()
        mWifiListFragment = WifiListFragment()
        mConnDeviceFragment = ConnectDeviceFragment()
        transaction.add(R.id.fragment, mSearchDeviceFragment)
        transaction.add(R.id.fragment, mWifiListFragment)
        transaction.add(R.id.fragment, mConnDeviceFragment)
        transaction.hide(mWifiListFragment)
        transaction.hide(mConnDeviceFragment)
        transaction.commit()
        mCurrentFragment = mSearchDeviceFragment
        mCurrentState = SEARCH_DEVICE
    }

     fun showWifiList() {
        if (mCurrentFragment is WifiListFragment)
            return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(mCurrentFragment)
        transaction.show(mWifiListFragment)
        transaction.commit()
        mCurrentFragment = mWifiListFragment
        mCurrentState = WIFI_LIST
    }

     private fun showSearchDevice() {
        if (mCurrentFragment is SearchDeviceFragment)
            return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(mCurrentFragment)
        transaction.show(mSearchDeviceFragment)
        transaction.commit()
        mCurrentFragment = mSearchDeviceFragment
        mCurrentState = SEARCH_DEVICE
    }

    /**
     * 发送wifi信息给玩具鼓
     */
     fun showConnectDevice(ssid: String) {
        if (mCurrentFragment is ConnectDeviceFragment) {
            return
        }
        mConnDeviceFragment.setSendSSID(ssid)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(mCurrentFragment)
        transaction.show(mConnDeviceFragment)
        transaction.commit()
        mCurrentFragment = mConnDeviceFragment
        mCurrentState = CONNECT_DEVICE
    }

    override fun onBackPressed() {
        when (mCurrentState) {
            WIFI_LIST -> showSearchDevice()
            SEARCH_DEVICE -> this@DrumSettingActivity.finish()
            CONNECT_DEVICE -> showWifiList()
        }
    }

    companion object {
        const val SEARCH_DEVICE = "search_device"
        const val WIFI_LIST = "wifi_list"
        const val CONNECT_DEVICE = "connect_device"
    }
}
