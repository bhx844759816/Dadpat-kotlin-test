package com.benbaba.dadpat.host.vm

import android.app.Application
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.sina.weibo.SinaWeibo
import cn.sharesdk.wechat.friends.Wechat
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.view.dialog.LoadingDialogFragment
import com.benbaba.dadpat.host.vm.repository.LoginRepository
import com.bhx.common.event.LiveBus
import com.bhx.common.mvvm.BaseViewModel
import com.bhx.common.utils.ToastUtils
import com.google.gson.Gson
import java.util.HashMap

class LoginViewModel(application: Application) : BaseViewModel<LoginRepository>(application) {
    private val gson = Gson()
    var loginType: Int = 0
    var isCancelLogin = false
    //监听第三方登陆
    private val mListener = object : PlatformActionListener {
        override fun onComplete(platform: Platform?, p1: Int, p2: HashMap<String, Any>?) {

            platform?.let {
                val params = HashMap<String, Any>()
                if (it.name == Wechat.NAME) {
                    params["token"] = it.db.token
                    params["refresh_token"] = it.db.get("refresh_token")
                    params["expiresIn"] = it.db.expiresIn
                    params["nickname"] = it.db.get("nickname")
                    params["openid"] = it.db.get("openid")
                    params["unionid"] = it.db.get("unionid")
                    doLoginThird("weixin", gson.toJson(params))
                } else if (platform.name == SinaWeibo.NAME) {
                    params["userID"] = it.db.userId
                    params["token"] = it.db.token
                    params["refresh_token"] = it.db.get("refresh_token")
                    params["expiresIn"] = it.db.expiresIn
                    params["expiresTime"] = it.db.expiresTime
                    doLoginThird("weibo", gson.toJson(params))
                }
            }

        }

        override fun onCancel(p0: Platform?, p1: Int) {
            ToastUtils.toastShort("取消登陆")
            isCancelLogin = true
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
        }

        override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
            isCancelLogin  = true
            ToastUtils.toastShort("第三方登陆失败")
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
        }
    }

    /**
     * 第三方登陆
     * type:0 微信登陆 type:1 微博登陆
     */
    fun loginThird(type: Int) {
        isCancelLogin = false
        LiveBus.getDefault().postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
        loginType = type
        val platform = when (loginType) {
            0 -> Wechat.NAME
            1 -> SinaWeibo.NAME
            else -> Wechat.NAME
        }
        val plt = ShareSDK.getPlatform(platform)
        plt.platformActionListener = mListener
        plt.SSOSetting(false) //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        if (plt.isAuthValid) {
            plt.removeAccount(true)
        }
        plt.showUser(null)
    }

    private fun doLoginThird(type: String, params: String) {
        mRepository.doLoginThird(type, params)
    }


}