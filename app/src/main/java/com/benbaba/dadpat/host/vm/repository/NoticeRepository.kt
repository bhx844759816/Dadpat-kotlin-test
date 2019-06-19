package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.action
import com.benbaba.dadpat.host.http.handleResult
import io.reactivex.functions.Consumer

/**
 * 公告的请求类
 */
class NoticeRepository : BaseEventRepository() {
    fun getNotices() {
        addDisposable(
            apiService.getNoticeList().compose(handleResult()).subscribe {
                sendData(Constants.EVENT_KEY_NOTICE, Constants.TAG_GET_NOTICES, it)
            }
        )
    }
}