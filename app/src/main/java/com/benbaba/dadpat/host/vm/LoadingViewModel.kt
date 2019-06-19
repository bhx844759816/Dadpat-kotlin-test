package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.LoadingRepository
import com.bhx.common.mvvm.BaseViewModel

class LoadingViewModel(application: Application) : BaseViewModel<LoadingRepository>(application) {

    fun getUser() {
        mRepository.getUser()
    }
}