package com.benbaba.dadpat.host.vm

import android.annotation.SuppressLint
import android.app.Application
import com.benbaba.dadpat.host.App
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.PluginBean
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.view.WaveDrawable
import com.benbaba.dadpat.host.vm.repository.MainRepository
import com.bhx.common.mvvm.BaseViewModel
import com.bhx.common.utils.FileUtils
import com.bhx.common.utils.LogUtils
import com.qihoo360.replugin.RePlugin
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import java.io.File
import java.util.*

class MainViewModel(application: Application) : BaseViewModel<MainRepository>(application) {

    /**
     * 获取需要下载的大小
     */
    fun getNeedDownSize(bean: PluginBean) {
        mRepository.getNeedDownSize(bean)
    }

    /**
     * 获取插件列表
     */
    fun getPluginList() {
        //判断本地是否有如果有取本地的
        mRepository.getPluginList()
    }

    /**
     * 停止全部的下载
     */
    fun stopAllDownload() {
        mRepository.stopAllDownland()
    }

    /**
     * 下载插件
     */
    fun downLandPlugin(bean: PluginBean) {
        mRepository.downlandPlugin(bean)
    }

     fun handlePluginList(list: List<PluginBean>) {
        var index = 0
        list.forEach {
            it.savePath = Constants.PLUGIN_SAVE_DIR
            if (it.isRelease == "1") {
                it.imgRes = Constants.RES_IMG_MAP[it.pluginName]!!
                val isInstall = RePlugin.isPluginInstalled(it.pluginName)
                it.isInstall = isInstall
                if (isInstall) {
                    it.downProgress = 0f
                    val version = RePlugin.getPluginVersion(it.pluginName)
                    it.isNeedUpdate = it.version > version
                    if (it.isNeedUpdate) {
                        it.waveDrawable = getWaveDrawable(it.imgRes)
                    } else {
                        it.waveDrawable = null
                    }
                } else {
                    it.waveDrawable = getWaveDrawable(it.imgRes)
                }
            } else {
                when (index) {
                    0 -> it.imgRes = R.drawable.main_item_bitmap_01
                    1 -> it.imgRes = R.drawable.main_item_bitmap_02
                    2 -> it.imgRes = R.drawable.main_item_bitmap_03
                    3 -> it.imgRes = R.drawable.main_item_bitmap_04
                }
                it.waveDrawable = getWaveDrawable(it.imgRes)
                index++
            }
        }
    }

    /**
     * 实例化下载得Drawable
     *
     * @return
     */
    private fun getWaveDrawable(res: Int): WaveDrawable {
        val waveDrawable = WaveDrawable(getApplication(), res)
        waveDrawable.isIndeterminate = false
        waveDrawable.setWaveSpeed(4)
        waveDrawable.setWaveAmplitude(10)
        return waveDrawable
    }

    public fun sortedPluginList(list: List<PluginBean>) {
        //按照是否发布排序
        Collections.sort(list) { o1, o2 ->
            val isReleaseO1 = Integer.valueOf(o1.isRelease)
            val isReleaseO2 = Integer.valueOf(o2.isRelease)
            isReleaseO1 - isReleaseO2
        }
        // 按照安装顺序排序
        Collections.sort(list) { o1, o2 ->
            val isInstallO1 = o1.isInstall
            val isInstallO2 = o2.isInstall
            var sorts = 0
            if (isInstallO1 && !isInstallO2) {
                sorts = -1
            } else if (!isInstallO1 && isInstallO2) {
                sorts = 1
            }
            sorts
        }
    }

    /**
     * 删除插件
     */
    @SuppressLint("CheckResult")
    fun deletePlugin(mDragPluginBean: PluginBean?) {
        if (mDragPluginBean != null) {
            //删除缓存
            val file = getApplication<App>().applicationContext.getExternalFilesDir(
                "benbaba" + File.separator + mDragPluginBean.pluginName
            )
            FileUtils.deleteDirFiles(file)
            //删除下载记录
            val mission =
                Mission(mDragPluginBean.url, mDragPluginBean.pluginName + ".apk", mDragPluginBean.savePath, true)
            RxDownload.create(mission, false).subscribe {
                RxDownload.delete(mission, true).subscribe {
                    LogUtils.i("删除文件$it")
                    RxDownload.clear(mission).subscribe()
                }
            }
            //删除插件
            val result = RePlugin.uninstall(mDragPluginBean.pluginName)
            LogUtils.i("删除插件结果：$result")
            mDragPluginBean.isInstall = false
            mDragPluginBean.downProgress = 0f
        }
    }

    /**
     * 跳转到对应的插件界面
     */
    fun startPluginActivity(bean: PluginBean?) {
        bean?.let {
            mRepository.startPluginActivity(it)
        }
    }


    fun uploadUserInfo(it: User?) {
        it?.let {
            mRepository.uploadUserInfo(it)
        }
    }


    /**
     * 注销登陆
     */
    fun loginOut() {
        mRepository.loginOut()
    }
}