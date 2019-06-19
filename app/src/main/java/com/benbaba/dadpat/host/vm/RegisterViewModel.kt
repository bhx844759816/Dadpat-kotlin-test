package com.benbaba.dadpat.host.vm

import android.app.Application
import cn.smssdk.SMSSDK
import com.benbaba.dadpat.host.vm.repository.RegisterRepository
import com.bhx.common.mvvm.BaseViewModel

class RegisterViewModel(application: Application) : BaseViewModel<RegisterRepository>(application) {

    init {

    }

    /**
     * 获取验证码
     */
    fun getSms(phone: String) {

    }

    /**
     * 注册
     */
    fun register(phone: String, smsCode: String, pwd: String) {
        mRepository.register(phone, smsCode, pwd)
    }

}