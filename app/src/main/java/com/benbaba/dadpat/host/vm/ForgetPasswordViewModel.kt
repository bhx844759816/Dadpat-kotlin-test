package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.ForgetPasswordRepository
import com.bhx.common.mvvm.BaseViewModel

/**
 * 忘记密码的界面
 */
class ForgetPasswordViewModel(application: Application) : BaseViewModel<ForgetPasswordRepository>(application) {

    /**
     * 修改密码
     */
    fun modifyPassword(phone: String, code: String, newPassword: String) {
        mRepository.modifyPassword(phone, code, newPassword)
    }
}