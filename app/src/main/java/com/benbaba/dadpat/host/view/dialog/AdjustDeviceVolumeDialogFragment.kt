package com.benbaba.dadpat.host.view.dialog

import android.content.Context
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.bhx.common.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_adjust_device_volume.*
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.view.MotionEvent
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.DeviceUdpUtils
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.qihoo360.replugin.RePlugin


/**
 * 调节鼓的音量
 */
class AdjustDeviceVolumeDialogFragment : BaseDialogFragment() {
    var isConnect = false
    override fun getLayoutId(): Int = R.layout.dialog_adjust_device_volume

    override fun initView(view: View?) {
        super.initView(view)
        isConnect = SmartBle.getInstance().isConnect
//        drumVolumeSeekBar.isEnabled = isConnect
        getDrumVolume()
        getSystemVolume()
        //处理蓝牙未连接触摸的事件
        drumVolumeSeekBar.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isConnect) {
                        ToastUtils.toastShort("蓝牙未连接")
                    }
                }
            }
            !isConnect
        }
        //调节鼓的音量
        drumVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drumVolumeTv.text = ((progress / 10) * 10).toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        //调节手机的音量
        phoneVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                phoneVolumeTv.text =((progress / 10) * 10).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        //点击确定
        confirm.clickWithTrigger {
            setSystemVolume(phoneVolumeSeekBar.progress)
            setDrumVolume((drumVolumeSeekBar.progress / 10) * 10)
            dismissAllowingStateLoss()
        }


    }

    private fun getDrumVolume() {
        val drumVolume = SPUtils.get(context, Constants.VOLUME_DRUM, 100) as Int

        drumVolumeSeekBar.progress = drumVolume
        drumVolumeTv.text = drumVolume.toString()
    }

    /**
     * 获取系统音量
     */
    private fun getSystemVolume() {
        val am = context?.getSystemService(AUDIO_SERVICE) as AudioManager?
        am?.let {
            val max = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val percent = (current * 100) / max
            phoneVolumeSeekBar.progress = percent
            phoneVolumeTv.text = percent.toString()
            LogUtils.i("系统最大音量$max,当前音量$current")
        }
    }

    private fun setSystemVolume(progress: Int) {
        val am = context?.getSystemService(AUDIO_SERVICE) as AudioManager?
        am?.let {
            val max = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = (progress * max) / 100
            it.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND)
        }
    }

    private fun setDrumVolume(progress: Int) {
        if (SmartBle.getInstance().isConnect) {
            drumVolumeSeekBar.isEnabled = true
            SmartBle.getInstance().sendMsg(DeviceUdpUtils.getSettingDeviceVolume(progress.toString()))
            SPUtils.put(context, Constants.VOLUME_DRUM, progress)
        } else {
            drumVolumeSeekBar.isEnabled = false
            ToastUtils.toastShort("蓝牙未连接")
        }
    }

    companion object {
        private val TAG = AdjustDeviceVolumeDialogFragment::class.simpleName

        fun show(activity: FragmentActivity) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = AdjustDeviceVolumeDialogFragment()
            }
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity?) {
            val fragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as AdjustDeviceVolumeDialogFragment
            if (fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}