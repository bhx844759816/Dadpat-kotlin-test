package com.benbaba.dadpat.host.ui

import android.Manifest
import android.content.Intent
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.utils.AudioPlayerManager
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.startActivity
import com.benbaba.dadpat.host.vm.LoadingViewModel
import com.bhx.common.mvvm.BaseMVVMActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_loading.*
import pl.droidsonroids.gif.GifDrawable

/**
 * loading页面
 */
class LoadingActivity : BaseMVVMActivity<LoadingViewModel>() {


    override fun getLayoutId(): Int {
        return R.layout.activity_loading
    }

    override fun getEventKey(): Any {
        return Constants.EVENT_KEY_LOADING
    }

    override fun initData() {
        val gifDrawable = GifDrawable(assets, "gif/main_loading.gif")
        gifDrawable.addAnimationListener {
            requestSdPermission()
        }
        loadingGif.setImageDrawable(gifDrawable)
        AudioPlayerManager.instance.setVolume(.5f, .5f)
        AudioPlayerManager.instance.prepare(this, "main_loading_bgm.mp3")
    }

    override fun onResume() {
        super.onResume()
        AudioPlayerManager.instance.start()
    }

    override fun onPause() {
        super.onPause()
        AudioPlayerManager.instance.stop()
    }

    override fun dataObserver() {
        registerObserver(Constants.TAG_LOADING_RESULT, Any::class.java).observe(this, Observer {
            when (it) {
                is User -> {
                    val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                    intent.putExtra("User", it)
                    startActivity(intent)
                    this@LoadingActivity.finish()
                }
                is Throwable -> {
//                    startActivity<LoginActivity>()
//                    this@LoadingActivity.finish()
                    startActivity<LoginActivity>()
                    this@LoadingActivity.finish()
                }
            }
        })
    }

    /**
     * 请求SD卡存储权限
     */
    private fun requestSdPermission() {
        addDisposable(RxPermissions(this)
            .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe {
                jumpActivity()
            })
    }

    /**
     * 判断跳转到对应的Activity
     */
    private fun jumpActivity() {
        val isLogin = SPUtils.get(applicationContext, Constants.SP_LOGIN, false) as Boolean
        if (isLogin) {
            mViewModel.getUser()
        } else {
            startActivity<LoginActivity>()
            this@LoadingActivity.finish()
        }
    }

}
