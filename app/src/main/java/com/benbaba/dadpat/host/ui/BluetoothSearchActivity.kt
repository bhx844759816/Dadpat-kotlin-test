package com.benbaba.dadpat.host.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.Observer
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException
import com.benbaba.dadpat.common.bluetooth.exception.BluetoothExceptionCompat
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.BlueState
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.vm.BluetoothSearchViewModel
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bluetooth_search.*

/**
 * Created by Administrator on 2019/6/3.
 */
class BluetoothSearchActivity : BaseDataActivity<BluetoothSearchViewModel>() {
    private var isAllowGPSPermission = false
    private val mSearchFinishDeviceAddress = mutableListOf<String>()
    override fun getEventKey(): Any = Constants.EVENT_KEY_BLUETOOTH_SEARCH

    override fun getLayoutId(): Int = R.layout.activity_bluetooth_search

    override fun initView() {
        super.initView()
        bluetoothSearchView.callback = {
            mViewModel.connectDevice(it as BluetoothDevice)
        }
        back.clickWithTrigger {
            this@BluetoothSearchActivity.finish()
        }
        searchDevice()
    }

    @SuppressLint("CheckResult")
    private fun searchDevice() {
        RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { aBoolean ->
                if (aBoolean!!) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            LogUtils.i("获取定位权限")
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivityForResult(intent, GPS_SETTING_REQUEST_CODE) // 设置完成后返回到原来的界面
                        } else {
                            LogUtils.i("获取定位权限成功")
                            isAllowGPSPermission = true
                            mViewModel.startSearchDevice()
                        }
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (isAllowGPSPermission) {
            bluetoothSearchView.clearDevice()
            mViewModel.startSearchDevice()
        }
    }

    private fun addDevice(device: BluetoothDevice) {
        val bluetoothDevice = mViewModel.getConnectDevice()
        LogUtils.i("连接成功的地址${bluetoothDevice?.address}")
        LogUtils.i("连接成功的名字${bluetoothDevice?.name}")
        val isConnected = (bluetoothDevice?.address == device.address)
                && (bluetoothDevice?.name == device.name)
        bluetoothSearchView.addDevice(device, isConnected, device.address)
    }

    /**
     * 停止搜索
     */
    override fun onPause() {
        super.onPause()
        if (isAllowGPSPermission)
            mViewModel.stopSearchDevice()
    }

    /**
     * 注册事件接收者
     */
    override fun dataObserver() {
        // 注册搜索结果
        registerObserver(Constants.TAG_BLUETOOTH_SEARCH_RESULT, BluetoothDevice::class.java).observe(this, Observer {
            addDevice(it)
        })
        //清楚搜索结果
        registerObserver(Constants.TAG_BLUETOOTH_SEARCH_CLEAR, Boolean::class.java).observe(this, Observer {

        })
        //开始搜索
        registerObserver(Constants.TAG_BLUETOOTH_SEARCH_START, Boolean::class.java).observe(this, Observer {
        })
        //清除断线的
        registerObserver(Constants.TAG_BLUETOOTH_SEARCH_FINISH, List::class.java).observe(this, Observer {
            val list = it as List<BluetoothDevice>
            if (list.isEmpty()) {
                return@Observer
            }
            LogUtils.i("搜索完成$list")
            mSearchFinishDeviceAddress.clear()
            list.forEach { device ->
                mSearchFinishDeviceAddress.add(device.address)
            }
            bluetoothSearchView.clearDevice(mSearchFinishDeviceAddress)

        })
        //注册操作结果
        //蓝牙操作结果
        registerObserver(Constants.TAG_BLUETOOTH_OPERATION_RESULT, BlueState::class.java).observe(this, Observer {
            when (it.code) {
                BlueState.CONNECT_SUCCESS -> {
                    ToastUtils.toastShort("蓝牙连接成功")
                    this@BluetoothSearchActivity.finish()
                }
                BlueState.CONNECT_ERROR,
                BlueState.SEARCH_ERROR -> {
                    if (it.exception != null) {
                        ToastUtils.toastShort(it.exception!!.msg)
                        operateException(it.exception)
                    }
                }
            }
        })
    }

    /**
     * 处理蓝牙操作的异常
     */
    private fun operateException(exception: BaseBluetoothException?) {
        when (exception?.code) {
            BluetoothExceptionCompat.CODE_BLUETOOTH_NOT_OPEN -> {
                SmartBle.getInstance().openBlueSync(this@BluetoothSearchActivity, BLUETOOTH_SETTING_REQUEST_CODE)
            }
        }
    }

    /**
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BLUETOOTH_SETTING_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    mViewModel.startSearchDevice()
                }
            }
            GPS_SETTING_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    isAllowGPSPermission = true
                    mViewModel.startSearchDevice()
                }
            }
        }

    }

    companion object {
        const val GPS_SETTING_REQUEST_CODE = 0x01
        const val BLUETOOTH_SETTING_REQUEST_CODE = 0x02
    }
}