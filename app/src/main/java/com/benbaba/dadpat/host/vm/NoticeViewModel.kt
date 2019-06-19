package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.NoticeRepository
import com.bhx.common.mvvm.BaseViewModel

class NoticeViewModel(application: Application) : BaseViewModel<NoticeRepository>(application) {

    fun getNotices(){
        mRepository.getNotices()
    }
}