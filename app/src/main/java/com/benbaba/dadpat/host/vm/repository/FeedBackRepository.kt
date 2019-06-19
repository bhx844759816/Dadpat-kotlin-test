package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.http.handleResult
import com.bhx.common.mvvm.BaseRepository

class FeedBackRepository : BaseEventRepository() {
    fun sendFeedBack(userName: String, userId: String, content: String) {
        val params = mapOf("userName" to userName, "userId" to userId, "content" to content)
        addDisposable(
            apiService.postFeedBack(params)
                .compose(applySchedulers())
                .subscribe()
        )
    }


}