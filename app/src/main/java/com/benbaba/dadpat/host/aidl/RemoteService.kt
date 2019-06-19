package com.benbaba.dadpat.host.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import com.benbaba.dadpat.host.IBluetoothCallBack
import com.benbaba.dadpat.host.IMyAidlInterface
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.BluetoothCompat
import com.benbaba.dadpat.host.utils.SPUtils
import com.bhx.common.utils.LogUtils

/**
 * Created by Administrator on 2019/6/12.
 */
class RemoteService : Service() {
    private val mRemoteCallbackList = RemoteCallbackList<IBluetoothCallBack>()
    override fun onBind(intent: Intent?): IBinder? {
        return myAidlInterface
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.i("RemoteService onCreate")
        //接收消息的回调
        BluetoothCompat.instance.receiveCallBack = {
            LogUtils.i("接收到鼓的消息2：$it")
            try {
                val count = mRemoteCallbackList.beginBroadcast()
                if (count != 0) {
                    for (i in 0 until count) {
                        mRemoteCallbackList.getBroadcastItem(i).receiveMsg(it)
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } finally {
                try {
                    mRemoteCallbackList.finishBroadcast()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
        //连接蓝牙的回调
        BluetoothCompat.instance.aidlConnectCallBack = {
            try {
                val count = mRemoteCallbackList.beginBroadcast()
                if (count != 0) {
                    for (i in 0 until count) {
                        LogUtils.i("aidlConnectCallBack 连接$it")
                        mRemoteCallbackList.getBroadcastItem(i).connectCallBack(it)
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } finally {
                try {
                    mRemoteCallbackList.finishBroadcast()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
        //断开连接蓝牙的回调
        BluetoothCompat.instance.aidlDisConnectCallBack = {
            try {
                val count = mRemoteCallbackList.beginBroadcast()
                if (count != 0) {
                    for (i in 0 until count) {
                        LogUtils.i("aidlConnectCallBack 断开连接")
                        mRemoteCallbackList.getBroadcastItem(i).disConnectCallBack()
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } finally {
                try {
                    mRemoteCallbackList.finishBroadcast()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private val myAidlInterface = object : IMyAidlInterface.Stub() {
        override fun saveBluetoothVolume(volume: Int) {
            try {
                SPUtils.put(applicationContext,Constants.VOLUME_DRUM,volume)
            }catch (e:Exception){

            }
        }

        override fun isConnect(): Boolean {
            return BluetoothCompat.instance.isConnect()
        }

        override fun sendMsg(msg: String?): Boolean {
            return BluetoothCompat.instance.sendMsg(msg)
        }

        override fun unRegisterCallBack(callback: IBluetoothCallBack?) {
            mRemoteCallbackList.unregister(callback)
        }

        override fun connectBluetooth() {
            BluetoothCompat.instance.connectBlueTooth()
        }

        override fun registerCallBack(callback: IBluetoothCallBack?) {
            mRemoteCallbackList.register(callback)
        }

    }

}