package com.benbaba.dadpat.host.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.ui.DrumSettingActivity
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.view.dialog.DeviceSettingHelpDialogFragment
import com.benbaba.dadpat.host.vm.SearchDeviceViewModel
import com.bhx.common.mvvm.BaseMVVMFragment
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bluetooth_search.*
import kotlinx.android.synthetic.main.fragment_search_device.*

/**
 * 搜索设备界面
 */
class SearchDeviceFragment : BaseMVVMFragment<SearchDeviceViewModel>() {
    private val scanResults: MutableList<ScanResult> = mutableListOf()
    override fun getLayoutId(): Int = R.layout.fragment_search_device
    override fun getEventKey(): Any = Constants.EVENT_KEY_SEARCH_DEVICE
    private var isAllowGPSPermission = false
    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        checkPermission()
    }

    override fun lazyLoad() {
        //wifi帮助按钮
        wifHelp.clickWithTrigger {
            activity?.let {
                DeviceSettingHelpDialogFragment.show(it)
            }
        }
        searchView.callback= {
            LogUtils.i("点击玩具鼓")
            mViewModel.connectDevice()
        }
        // 注册搜索附近设备列表
        registerObserver(Constants.TAG_SEARCH_DEVICE_RESULT, List::class.java).observe(this, Observer {
            val list = it as List<ScanResult>
            scanResults.clear()
            scanResults.addAll(list)
            searchView.addDeviceList(scanResults)
        })
        // 连接设备
        registerObserver(Constants.TAG_CONNECT_DEVICE_RESULT, Boolean::class.java).observe(this, Observer {
            if (it) {
                //跳转到选择wifi列表的Fragment
                if (activity is DrumSettingActivity) {
                    (activity as DrumSettingActivity).showWifiList()
                }
            } else {
                ToastUtils.toastShort("连接Dadpat失败")
            }
        })
    }

    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    fun checkPermission() {
        RxPermissions(activity!!).request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { aBoolean ->
                if (aBoolean!!) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val locManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            LogUtils.i("获取定位权限")
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivityForResult(intent, GPS_SETTING_REQUEST_CODE) // 设置完成后返回到原来的界面
                        } else {
                            LogUtils.i("获取定位权限成功")
                            isAllowGPSPermission = true
                            mViewModel.startSearchWifi()
                        }
                    }
                } else {

                }
            }
    }

    override fun onVisible() {
        super.onVisible()
        LogUtils.i("SearchDeviceFragment onVisible")
        if (isAllowGPSPermission && mViewModel != null) {
            searchView.clearDevice()
            mViewModel.startSearchWifi()
        }
    }

    override fun onInVisible() {
        super.onInVisible()
        LogUtils.i("SearchDeviceFragment onInVisible")
        mViewModel.stopSearchWifi()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.stopSearchWifi()
    }


    companion object {
        const val GPS_SETTING_REQUEST_CODE = 0x01
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GPS_SETTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isAllowGPSPermission = true
                mViewModel.startSearchWifi()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}