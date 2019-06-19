package com.benbaba.dadpat.host.vm.repository

import android.text.TextUtils
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.*
import com.benbaba.dadpat.host.model.PluginBean
import com.benbaba.dadpat.host.model.User
import com.bhx.common.event.LiveBus
import com.bhx.common.http.ApiException
import com.bhx.common.utils.*
import com.qihoo360.replugin.RePlugin
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.*
import java.io.File


/**
 * 主页面的Repository(仓库)类
 */
class MainRepository : BaseEventRepository() {
    private val missionMap = hashMapOf<PluginBean, Mission>()
    private lateinit var downloadSizeDisposable: Disposable //获取下载进度
    /**
     * 获取插件列表
     */
    fun getPluginList() {
        addDisposable(
            apiService.getPluginList()
                .action {
                    //将数据发送到MainActivity
                    sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_GET_PLUGIN_LIST, it)
                }
        )
    }

    /**
     * 获取需要下载的大小
     */
    fun getNeedDownSize(bean: PluginBean) {
        LogUtils.i("需要下载的文件${bean.pluginAlias}")
        val mission = Mission(bean.url, bean.pluginName + ".apk", bean.savePath, false)
        addDisposable(
            RxDownload.isExists(mission)
                .compose(applySchedulersMaybe())
                .subscribe {
                    if (it) {
                        getNeedDownloadSize(mission)
                    } else {
                        sendData(
                            Constants.EVENT_KEY_MAIN,
                            Constants.TAG_NEED_DOWNLAND_SIZE,
                            FileUtils.formatFileSize(bean.apkSize.toLong())
                        )
                    }
                }
        )
    }

    /**
     * 获取需要下载的大小
     */
    private fun getNeedDownloadSize(mission: Mission) {
        downloadSizeDisposable = RxDownload.create(mission, false)
            .compose(applySchedulersFlowable())
            .subscribe {
                val needDownSize = FileUtils.formatFileSize(it.totalSize - it.downloadSize)
//              LogUtils.i("rxDownloadSize$needDownSize")
                sendData(
                    Constants.EVENT_KEY_MAIN,
                    Constants.TAG_NEED_DOWNLAND_SIZE,
                    needDownSize
                )
                downloadSizeDisposable.dispose()
            }
    }

    /**
     * 下载对应的插件 如果已经创建任务的话判断是否正在下载执行相反的逻辑
     */
    fun downlandPlugin(bean: PluginBean) {
        //判断是否已经下载过了
        if (missionMap.contains(bean)) {
            // 创建过下载任务
            if (bean.isDownLanding) {
                missionMap[bean]?.let {
                    stopDownland(it)
                    bean.isDownLanding = false
                    ToastUtils.toastShort("${bean.pluginAlias}暂停下载")
                }
            } else {
                missionMap[bean]?.let {
                    startDownland(it)
                    bean.isDownLanding = true
                    ToastUtils.toastShort("${bean.pluginAlias}开始下载")
                }
            }
            return
        }
        //判断是否url为空
        if(TextUtils.isEmpty(bean.url)){
            ToastUtils.toastShort("请稍后重试")
            return
        }
        ToastUtils.toastShort("${bean.pluginAlias}开始下载")
        LogUtils.i("需要下载的插件$bean")
        val mission = Mission(bean.url, bean.pluginName + ".apk", bean.savePath, true)
        if (!missionMap.contains(bean)) {
            missionMap[bean] = mission
        }
        addDisposable(RxDownload.create(mission, true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe {
                //处理下载状态
                handleStatus(it, bean)
            })


    }

    /**
     * 处理下载状态
     */
    private fun handleStatus(status: Status, bean: PluginBean) {
        when (status) {
            is Downloading -> {
                //发送下载进度
                val percent =
                    String.format("%.2f", status.downloadSize.toFloat() / status.totalSize.toFloat()).toFloat()
                LogUtils.i("percent:$percent")
                bean.waveDrawable.level = (percent * 10000).toInt()
                bean.downProgress = percent
            }
            is Suspend -> {
                LogUtils.i("暂停下载${bean.pluginAlias}")
                val percent =
                    String.format("%.2f", status.downloadSize.toFloat() / status.totalSize.toFloat()).toFloat()
                LogUtils.i("Suspend percent:$percent")
                bean.waveDrawable.level = (percent * 10000).toInt()
                bean.downProgress = percent
            }
            is Normal -> {
                bean.isDownLanding = true
                sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_DOWNLAND_START, bean)
            }
            is Failed -> {
                bean.isDownLanding = false
                //下载失败
                missionMap.remove(bean)
                sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_DOWNLAND_ERROR, bean)
            }
            is Succeed -> {
                LogUtils.i("下载成功")
                bean.isDownLanding = false
                //下载成功
                missionMap.remove(bean)
                installPlugin(bean)
            }
        }
    }

    /**
     * 安装插件
     */
    private fun installPlugin(bean: PluginBean) {
        addDisposable(
            Single.create<PluginBean> {
                val path = bean.savePath + "/" + bean.pluginName + ".apk"
                val apkMd5 = Md5Utils.getFileMD5(File(path))
                if (bean.apkMd5 != apkMd5) {
                    deleteDownLandRecord(bean)
                    it.onError(Throwable("下载文件和本地的MD5不相同"))
                } else {
                    val info = RePlugin.install(path)
                    if (info != null) {
                        it.onSuccess(bean)
                    }
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    deleteDownLandRecord(bean)
                    LogUtils.i("安装完成")
                    val isInstall = RePlugin.isPluginInstalled(it.pluginName)
                    bean.isInstall = isInstall
                    bean.isNeedUpdate = false
                    ToastUtils.toastShort("${bean.pluginAlias}安装完成")
                    sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_PLUGIN_INSTALL_SUCCESS, it)
                }, {
                    LogUtils.i("安装失败:${it.localizedMessage}")
                })
        )
    }

    /**
     * 删除下载记录
     */
    private fun deleteDownLandRecord(bean: PluginBean) {
        //删除下载记录
        val mission =
            Mission(bean.url, bean.pluginName + ".apk", bean.savePath, true)
        addDisposable(RxDownload.create(mission, false).subscribe {
            RxDownload.delete(mission, true).subscribe {
                RxDownload.clear(mission).subscribe()
            }
        })
    }

    /**
     * 开始指定的下载
     */
    private fun startDownland(mission: Mission) {
        addDisposable(RxDownload.start(mission).subscribe())
    }

    /**
     * 停止指定的下载
     */
    private fun stopDownland(mission: Mission) {
        addDisposable(RxDownload.stop(mission).subscribe())
    }

    /**
     * 停止全部的下载
     */
    fun stopAllDownland() {
        addDisposable(RxDownload.stopAll().subscribe())
    }

    /**
     *
     */
    fun startPluginActivity(bean: PluginBean) {
        //当插件对象不为空的时候
        if (!bean.isInstall || bean.isNeedUpdate || bean.isRelease == "2") {
            return
        }
        if (bean.pluginName == "Plugin_Rhythm" && !SmartBle.getInstance().isConnect) {
            ToastUtils.toastShort("请先连接爸爸拍拍蓝牙")
            return
        }
        addDisposable(Single.create<Boolean> {
            val info = RePlugin.getPluginInfo(bean.pluginName)
            if (info != null) {
                val result = RePlugin.preload(info)
                it.onSuccess(result)
            } else {
                it.onSuccess(false)
            }
        }.subscribeOn(Schedulers.io())
            .doOnSubscribe {
                val loadingGifName = when (bean.pluginName) {
                    "Plugin_Web_Calendar" -> "gif/loading_calendar.gif"
                    "Plugin_Web_Astronomy" -> "gif/loading_astronomy.gif"
                    "Plugin_Web_ChinaHistory" -> "gif/loading_china_history.gif"
                    "Plugin_Web_Earth" -> "gif/loading_earth.gif"
                    "Plugin_Web_Animal" -> "gif/loading_animal.gif"
                    "Plugin_Web_English" -> "gif/loading_abc.gif"
                    "Plugin_Web_Picture" -> "gif/loading_picture.gif"
                    "Plugin_Web_WorldHistory" -> "gif/loading_world_history.gif"
                    else -> "gif/loading_dialog.gif"
                }
                sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_START_PLUGIN_ACTIVITY_DIALOG, loadingGifName)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                if (it) {
                    sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_PRELOAD_PLUGIN_SUCCESS, bean)
                }
            })
        )


    }

    /**
     * 上传用户信息
     */
    fun uploadUserInfo(user: User) {
        val params = mapOf(
            "userId" to user.userId,
            "userName" to user.userName,
            "userSex" to user.userSex,
            "userBirthday" to user.userBirthday
        )
        // 用户信息上传
        val uploadUserInfo = apiService.updateUserInfo(params)
        // 头像上传
        val fileBody = user.photoFile?.let {
            LogUtils.i("文件头像是否存在${it.exists()}")
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), it)
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }
        // 合并请求
        val requestObservable = if (fileBody != null) {
            LogUtils.i("请求包含头像")
            val uploadHeadImg = apiService.updateUserPhoto(fileBody)
            Observable.concat(uploadUserInfo, uploadHeadImg)
        } else {
            LogUtils.i("请求不包含头像")
            uploadUserInfo
        }
        //发送请求
        addDisposable(
            requestObservable
                .compose(applySchedulers())
                .doOnSubscribe {
                    LogUtils.i("弹出加载对话框")
                    if (!it.isDisposed) {
                        LiveBus.getDefault()
                            .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
                    }
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    LiveBus.getDefault()
                        .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
                }
                .flatMap {
                    if (it.success) {
                        apiService.getUser().compose(applySchedulers())
                    } else {
                        Observable.error(ApiException(Integer.valueOf(it.code), it.msg))
                    }
                }
                .compose(handleResult())
                .subscribe(Consumer{
                    LogUtils.i("更新用户信息成功")
                    //更新用户信息成功
                    sendData(Constants.EVENT_KEY_MAIN, Constants.TAG_UPDATE_USER_INFO_SUCCESS, it)
                }, Consumer {

                }, Action {

                })
        )
    }

    /**
     * 退出登陆
     */
    fun loginOut() {
        addDisposable(apiService.doLogout().compose(applySchedulers()).subscribe())
    }


}