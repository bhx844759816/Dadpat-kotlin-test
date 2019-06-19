package com.benbaba.dadpat.host

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.multidex.MultiDex
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.TokenInterceptor
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.Utils_CrashHandler
import com.bhx.common.BaseApplication
import com.bhx.common.http.RetrofitManager
import com.bhx.common.utils.AppManager
import com.bhx.common.utils.LogUtils
import com.mob.MobSDK
import com.qihoo360.replugin.RePlugin
import com.qihoo360.replugin.RePluginConfig
import com.zhihu.matisse.ui.ImageCropActivity
import com.zhihu.matisse.ui.MatisseActivity
import me.jessyan.autosize.AutoSizeConfig
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import zlc.season.rxdownload3.core.DownloadConfig
import zlc.season.rxdownload3.extension.ApkInstallExtension
import zlc.season.rxdownload3.extension.ApkOpenExtension

open class App : BaseApplication(), Application.ActivityLifecycleCallbacks {


    private var token: String? = null
    override fun onCreate() {
        super.onCreate()
        //Replugin的初始化
        RePlugin.App.onCreate()
        //注册监听回调
        registerActivityLifecycleCallbacks(this)
        instance = this
        //初始化MobSdk
        MobSDK.init(this)
        //RetrofitManager的初始化
        val builder = RetrofitManager.Builder()
            .setBaseUrl(Constants.BASE_URL2)
            .setHttpsInputStream(this.resources.openRawResource(R.raw.dadpat))
            .setInterceptorList(listOf(TokenInterceptor()))
        RetrofitManager.getInstance().init(builder)
        //RxDownland的配置
        val rxDownlandBuilder = DownloadConfig.Builder.create(this)
            .enableDb(true)
            .enableAutoStart(false)              //自动开始下载
            .addExtension(ApkInstallExtension::class.java)
            .addExtension(ApkOpenExtension::class.java)
            .setMaxRange(10)       // 每个任务并发的线程数量
            .setMaxMission(5)      // 同时下载的任务数量
        DownloadConfig.init(rxDownlandBuilder)
        //加载本地存储的Token
        token = SPUtils.get(this, Constants.SP_TOKEN, "") as String
        //创建通知对象
        createNotificationChannel()
        if (BuildConfig.DEBUG) {
            //将崩溃日志写到本地
            Utils_CrashHandler.getInstance().init(this)
        }
        //初始化蓝牙适配器
        SmartBle.getInstance().init(this)
        //
        initFonts()
        //
        AutoSizeConfig.getInstance().externalAdaptManager
            .addCancelAdaptOfActivity(ImageCropActivity::class.java)
            .addCancelAdaptOfActivity(MatisseActivity::class.java)
    }

    /**
     * 初始化字体
     */
    private fun initFonts() {
        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/fangyuanti.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    /**
     * 创建通知得渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "app_update"
            val channelName = "APP更新"
            val importance = NotificationManager.IMPORTANCE_MIN //静默
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        RePlugin.App.attachBaseContext(this, createConfig())
        MultiDex.install(this)
    }

    private fun createConfig(): RePluginConfig {
        val c = RePluginConfig()
        //当安装成功的时候删除源文件
        c.isMoveFileWhenInstalling = true
        // 允许“插件使用宿主类”。默认为“关闭”
        c.isUseHostClassIfNotFound = true
        // FIXME RePlugin默认会对安装的外置插件进行签名校验，这里先关掉，避免调试时出现签名错误
//        c.verifySign = !BuildConfig.DEBUG
        // FIXME 若宿主为Release，则此处应加上您认为"合法"的插件的签名，例如，可以写上"宿主"自己的。
        //        RePlugin.addCertSignature("9d86eaad935d24ebed33c6fb07446194");
        return c
    }

    override fun onLowMemory() {
        super.onLowMemory()
        RePlugin.App.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        RePlugin.App.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        RePlugin.App.onConfigurationChanged(newConfig)
    }

    /**
     * 设置Token
     */
    fun setToken(token: String) {
        this.token = token
        SPUtils.put(this, Constants.SP_TOKEN, token)
    }

    /**
     * 获取token
     */
    fun getToken() = token

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: App? = null

        fun instance() = instance!!
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        LogUtils.i("AppLifeCycle onActivityDestroyed${activity?.javaClass?.simpleName}")
        AppManager.getAppManager().removeActivity(activity)
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        LogUtils.i("AppLifeCycle onActivityCreated${activity?.javaClass?.simpleName}")
        AppManager.getAppManager().addActivity(activity)
    }
}