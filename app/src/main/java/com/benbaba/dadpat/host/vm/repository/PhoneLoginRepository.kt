package com.benbaba.dadpat.host.vm.repository

import android.annotation.SuppressLint
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.action
import com.bhx.common.utils.LogUtils

/**
 * 手机号登陆的请求类
 */
class PhoneLoginRepository : BaseEventRepository() {

    @SuppressLint("CheckResult")
    fun login(phone: String, psd: String) {
        val params = hashMapOf("userMobile" to phone, "userPwd" to psd)
        addDisposable(apiService.doLogin(params).action {
            LogUtils.i("login success:$it")
            sendData(Constants.EVENT_KEY_PHONE_LOGIN, Constants.TAG_PHONE_LOGIN_RESULT, it)
        })
    }

}