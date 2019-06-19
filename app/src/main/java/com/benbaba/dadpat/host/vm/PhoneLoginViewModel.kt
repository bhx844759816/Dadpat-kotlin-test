package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.PhoneLoginRepository
import com.bhx.common.mvvm.BaseViewModel

class PhoneLoginViewModel(application: Application) : BaseViewModel<PhoneLoginRepository>(application) {

    /**
     * 登陆
     */
    fun login(phone: String, psd: String) {
        mRepository.login(phone, psd)
    }

}