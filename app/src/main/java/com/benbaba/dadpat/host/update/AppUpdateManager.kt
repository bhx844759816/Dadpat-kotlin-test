package com.benbaba.dadpat.host.update

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Pair
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.ApiService
import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.http.handleResultForLoadingDialog
import com.benbaba.dadpat.host.model.PluginBean
import com.benbaba.dadpat.host.view.dialog.AppUpdateDialogFragment
import com.benbaba.dadpat.host.view.dialog.PersonInfoDialogFragment
import com.bhx.common.http.RetrofitManager
import com.bhx.common.http.downland.DownlandManager
import com.bhx.common.http.downland.FileInfo
import com.bhx.common.utils.FileUtils
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.bhx.common.utils.Utils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import zlc.season.rxdownload3.helper.contentLength
import java.io.File
import java.util.*

/**
 *
 */
class AppUpdateManager {
    private var curPercent: Int = 0
    private var apiService: ApiService? = null
    private var needDownloadPluginBean: PluginBean? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private lateinit var context: Context
    private var mManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private val appUpdateCallBack = object : AppUpdateCallBack {
        override fun confirmUpdate() {
            updateApp()
        }

    }


    /**
     * 检查是否需要更新
     */
    fun checkIsNeedUpdate(activity: FragmentActivity) {
        context = activity.applicationContext
        if (apiService == null) {
            apiService = RetrofitManager.getInstance().createApiService(ApiService::class.java)
        }
        addDisposable(
            apiService!!.getHostApp()
                .compose(handleResultForLoadingDialog())
                .subscribe {
                    val bean = it[0]
                    val localVersion = Utils.getLocalVersion(activity)
                    if (bean.version > localVersion) {
                        needDownloadPluginBean = bean
                        AppUpdateDialogFragment.show(
                            activity,
                            bean.versionName,
                            FileUtils.formatFileSize(bean.apkSize.toLong()),
                            appUpdateCallBack
                        )
                    } else {
                        ToastUtils.toastShort("当前是最新版本")
                    }
                })
    }

    /**
     * 下载更新APP
     */
    fun updateApp() {
        //创建通知
        createNotification()
        //
        val fileInfo = FileInfo()
        fileInfo.fileName = Constants.APP_DOWNLAND_NAME
        fileInfo.filePath = Constants.APP_SAVE_DIR
        fileInfo.url = needDownloadPluginBean?.url
        addDisposable(
            DownlandManager.downland(needDownloadPluginBean?.url, fileInfo)
                .compose(applySchedulers())
                .subscribe {
                    if (it is FileInfo) {
                        LogUtils.i("下载完成$it")
                        installApk(fileInfo)
                    } else {
                        val pair = it as Pair<Long, Long>
                        val downloadSize = pair.first
                        val totalSize = pair.second
                        LogUtils.i("下载过程${pair.first}")
                        updateProgress(downloadSize, totalSize)
                    }
                }
        )
    }

    private fun updateProgress(downloadSize: Long, totalSize: Long) {
        val percent = (((downloadSize * 1.0f) / totalSize) * 100).toInt()
        if ((percent - curPercent) > 5) {
            curPercent = percent
            mBuilder?.let {
                it.setProgress(100, curPercent, false)
                it.setContentText(FileUtils.formatFileSize(downloadSize) + "/" + FileUtils.formatFileSize(totalSize))
                mManager?.notify(NOTIFICATION_ID, it.build())
            }
        }
    }

    /**
     * 获取安装得Intent
     *
     * @return
     */
    private fun installApk(fileInfo: FileInfo) {
        val file = File(fileInfo.filePath, fileInfo.fileName)
        val intent = Intent(Intent.ACTION_VIEW)
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            val apkUri = FileProvider.getUriForFile(context, "com.benbaba.dadpat.host.fileprovider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)

    }

    /**
     * 创建通知
     */
    private fun createNotification() {
        if (mBuilder == null) {
            mBuilder = NotificationCompat.Builder(context, "app_update")
                .setContentTitle("App更新")
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.dadpat_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.dadpat_logo))
                .setAutoCancel(true)
        }
        mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mManager?.notify(NOTIFICATION_ID, mBuilder?.build())
    }

    private object SingletonHolder {
        val holder = AppUpdateManager()
    }

    companion object {
        val INSTANCE = SingletonHolder.holder
        val NOTIFICATION_ID = UUID.randomUUID().hashCode()
    }

    fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable)
    }

    fun unDisposable() {
        if (mCompositeDisposable != null && !mCompositeDisposable!!.isDisposed) {
            mCompositeDisposable!!.clear()
        }
    }
}