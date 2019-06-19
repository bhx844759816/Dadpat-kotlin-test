package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.handleResult
import io.reactivex.functions.Consumer

class LoadingRepository : BaseEventRepository() {
    /**
     * 获取用户的
     */
    fun getUser() {
        addDisposable(apiService.getUser().compose(handleResult()).subscribe({
            sendData(Constants.EVENT_KEY_LOADING, Constants.TAG_LOADING_RESULT, it)
        }, {
            sendData(Constants.EVENT_KEY_LOADING, Constants.TAG_LOADING_RESULT, it)
        }))
    }
}