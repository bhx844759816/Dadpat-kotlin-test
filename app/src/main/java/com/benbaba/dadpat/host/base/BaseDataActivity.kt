package com.benbaba.dadpat.host.base

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.view.dialog.LoadingDialogFragment
import com.bhx.common.http.ApiException
import com.bhx.common.mvvm.BaseMVVMActivity
import com.bhx.common.mvvm.BaseViewModel
import com.bhx.common.utils.AppManager
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import me.jessyan.autosize.internal.CustomAdapt
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseDataActivity<T : BaseViewModel<*>?> : BaseMVVMActivity<T>(), CustomAdapt {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i("${this.javaClass.simpleName},onResume")
        //注册加载对话框监听
        registerObserver(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, Boolean::class.java).observe(
            this, Observer {
                if (it != null && it) {
                    LogUtils.i("接收到弹出加载对话框true")
                    LoadingDialogFragment.show(this)
                } else {
                    LogUtils.i("接收到弹出加载对话框false")
                    LoadingDialogFragment.dismiss(this)
                }
            })
        // 注册Http请求得监听
        registerObserver(
            Constants.EVENT_KEY_HTTP_REQUEST_ERROR,
            Constants.TAG_HTTP_REQUEST_ERROR,
            Throwable::class.java
        ).observe(
            this, Observer {
                if (it is ApiException) {
                    val code = it.code
                    ToastUtils.toastShort(it.message)
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        LogUtils.i("${this.javaClass.simpleName},onPause")
        //页面不显示的时候取消注册对话框监听
        unRegisterObserver(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG)
        unRegisterObserver(Constants.EVENT_KEY_HTTP_REQUEST_ERROR, Constants.TAG_HTTP_REQUEST_ERROR)
    }

    /**
     * 是否按照宽度进行等比例适配 (为了保证在高宽比不同的屏幕上也能正常适配, 所以只能在宽度和高度之中选择一个作为基准进行适配)
     *
     * @return {@code true} 为按照宽度进行适配, {@code false} 为按照高度进行适配
     */
    override fun isBaseOnWidth(): Boolean  = false
    /**
     * 这里使用 iPhone 的设计图, iPhone 的设计图尺寸为 750px * 1334px, 高换算成 dp 为 667 (1334px / 2 = 667dp)
     * <p>
     * 返回设计图上的设计尺寸, 单位 dp
     * {@link #getSizeInDp} 须配合 {@link #isBaseOnWidth()} 使用, 规则如下:
     * 如果 {@link #isBaseOnWidth()} 返回 {@code true}, {@link #getSizeInDp} 则应该返回设计图的总宽度
     * 如果 {@link #isBaseOnWidth()} 返回 {@code false}, {@link #getSizeInDp} 则应该返回设计图的总高度
     * 如果您不需要自定义设计图上的设计尺寸, 想继续使用在 AndroidManifest 中填写的设计图尺寸, {@link #getSizeInDp} 则返回 {@code 0}
     *
     * @return 设计图上的设计尺寸, 单位 dp
     */
    override fun getSizeInDp(): Float  = 0f


}