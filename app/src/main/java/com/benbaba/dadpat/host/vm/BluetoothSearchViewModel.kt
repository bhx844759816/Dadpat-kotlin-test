package com.benbaba.dadpat.host.vm

import android.app.Application
import android.bluetooth.BluetoothDevice
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException
import com.benbaba.dadpat.common.bluetooth.callback.SearchResultCallBack
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.BlueState
import com.benbaba.dadpat.host.drum.BluetoothCompat
import com.benbaba.dadpat.host.vm.repository.BluetoothSearchRepository
import com.bhx.common.event.LiveBus
import com.bhx.common.mvvm.BaseViewModel
import com.bhx.common.utils.LogUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * 蓝牙设备搜索
 * Created by Administrator on 2019/6/4.
 */
class BluetoothSearchViewModel(application: Application) : BaseViewModel<BluetoothSearchRepository>(application) {
    private var isNeedSearchDevice = false
    private var mDelayDisposable: Disposable? = null

    //搜索蓝牙的回调
    private val bluetoothSearchCallBack = object : SearchResultCallBack {
        override fun searchTargetDevice(device: BluetoothDevice?) {
            if (isNeedSearchDevice) {
                device?.let {
                    LiveBus.getDefault()
                        .postEvent(Constants.EVENT_KEY_BLUETOOTH_SEARCH, Constants.TAG_BLUETOOTH_SEARCH_RESULT, it)
                }
            }
        }

        override fun startSearch() {
            if (isNeedSearchDevice) {
                LogUtils.i("开始搜索")
                LiveBus.getDefault()
                    .postEvent(
                        Constants.EVENT_KEY_BLUETOOTH_SEARCH,
                        Constants.TAG_BLUETOOTH_SEARCH_START,
                        true
                    )
            }
        }

        override fun searchFinish(list: List<BluetoothDevice>) {
            if (isNeedSearchDevice) {
                LogUtils.i("结束搜索")

                LiveBus.getDefault()
                    .postEvent(Constants.EVENT_KEY_BLUETOOTH_SEARCH, Constants.TAG_BLUETOOTH_SEARCH_FINISH, list)
                searchDelay()
            }
        }

        override fun searchError(exception: BaseBluetoothException) {
            if (isNeedSearchDevice) {
                LogUtils.i("蓝牙搜索失败:${exception.msg}")
                searchDelay()
                //发送蓝牙搜索失败的消息
                val blueState = BlueState(BlueState.SEARCH_ERROR, exception)
                LiveBus.getDefault()
                    .postEvent(
                        Constants.EVENT_KEY_BLUETOOTH_SEARCH,
                        Constants.TAG_BLUETOOTH_OPERATION_RESULT,
                        blueState
                    )
            }
        }
    }


    private fun searchDelay() {
        mDelayDisposable = Observable.timer(5, TimeUnit.SECONDS)
            .subscribe {
                if (isNeedSearchDevice && mDelayDisposable != null && !mDelayDisposable!!.isDisposed) {
                    startSearchDevice()
                }
            }
    }

    init {
        BluetoothCompat.instance.registerBluetoothSearchCallBack(bluetoothSearchCallBack)
    }

    /**
     * 开始搜索附近的蓝牙设备
     */
    fun startSearchDevice() {
        isNeedSearchDevice = true
        BluetoothCompat.instance.startSearchDevice()
    }

    fun getBondDevice(): MutableList<BluetoothDevice> {
        return SmartBle.getInstance().bondDevice
    }

    fun getConnectDevice(): BluetoothDevice? {
        return if (BluetoothCompat.instance.isConnect()) BluetoothCompat.instance.mConnectBluetoothDevice else null
    }

    /**
     * 停止搜索附近的蓝牙设备
     */
    fun stopSearchDevice() {
        isNeedSearchDevice = false
        BluetoothCompat.instance.stopSearchDevice()
    }

    /**
     * 连接设备
     */
    fun connectDevice(device: BluetoothDevice) {
        BluetoothCompat.instance.connectSuccessCallBack = {
            stopSearchDevice()
            LiveBus.getDefault()
                .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
            //发送连接成功的消息
            val blueState = BlueState(BlueState.CONNECT_SUCCESS, null)
            LiveBus.getDefault()
                .postEvent(
                    Constants.EVENT_KEY_BLUETOOTH_SEARCH,
                    Constants.TAG_BLUETOOTH_OPERATION_RESULT,
                    blueState
                )
        }
        BluetoothCompat.instance.connectErrorCallBack = {
            startSearchDevice()
            val blueState = BlueState(BlueState.CONNECT_ERROR, it)
            //取消对话框
            LiveBus.getDefault()
                .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
            //发送蓝牙连接失败
            LiveBus.getDefault().postEvent(
                Constants.EVENT_KEY_BLUETOOTH_SEARCH,
                Constants.TAG_BLUETOOTH_OPERATION_RESULT, blueState
            )
        }
        isNeedSearchDevice = false
        LiveBus.getDefault()
            .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
        BluetoothCompat.instance.connectBlueTooth(device)
    }

    override fun onCleared() {
        super.onCleared()
        mDelayDisposable?.dispose()
        mDelayDisposable = null
        isNeedSearchDevice = false
        BluetoothCompat.instance.unRegisterBluetoothSearchCallBack()
        BluetoothCompat.instance.stopSearchDevice()
    }
}