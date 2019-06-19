package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.action
import com.benbaba.dadpat.host.http.handleResult

class ForgetPasswordRepository : BaseEventRepository() {
    /**
     * 修改密码
     */
    fun modifyPassword(phone: String, code: String, newPassword: String) {
        val params = mapOf("userMobile" to phone, "userPwd" to newPassword, "smsCode" to code)
        addDisposable(
            apiService.modifyPsd(params)
                .action {
                    sendData(Constants.EVENT_KEY_FORGET_PASSWORD, Constants.TAG_MODIFY_PASSWORD_RESULT, true)
                }
        )
    }
}