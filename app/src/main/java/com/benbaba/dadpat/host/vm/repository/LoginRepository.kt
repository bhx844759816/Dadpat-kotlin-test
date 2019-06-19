package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.action
import com.benbaba.dadpat.host.http.applySchedulers
import com.bhx.common.utils.ToastUtils
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class LoginRepository : BaseEventRepository() {

    fun doLoginThird(type: String, params: String) {
        addDisposable(
            apiService.loginThird(type, "APP_USER", params)
                .action {
                    sendData(Constants.EVENT_KEY_LOGIN, Constants.TAG_LOGIN_RESULT, it)
                }
        )
    }
}