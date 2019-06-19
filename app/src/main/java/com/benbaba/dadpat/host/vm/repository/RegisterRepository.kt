package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.action
import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.http.handleResult
import com.bhx.common.event.LiveBus
import com.bhx.common.http.ApiException
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class RegisterRepository : BaseEventRepository() {
    /**
     * 注册
     */
    fun register(phone: String, code: String, pwd: String) {
        val params = mapOf("userMobile" to phone, "smsCode" to code)
        addDisposable(
            apiService.verifySms(params)
                .compose(handleResult())
                .flatMap {
                    val params2 = mapOf(
                        "authCode" to phone, "authType" to "mobile",
                        "authCredential" to pwd, "userType" to "DEFAULT_USER"
                    )
                    return@flatMap apiService.register(params2).compose(applySchedulers())
                }
                .compose(handleResult())
                .flatMap {
                    val params_ = mapOf(
                        "userMobile" to phone,
                        "userPwd" to pwd
                    )
                    return@flatMap apiService.doLogin(params_).compose(applySchedulers())
                }
                .action {
                    //注册成功
                    sendData(Constants.EVENT_KEY_REGISTER, Constants.TAG_REGISTER_RESULT, it)
                }


        )
    }
}