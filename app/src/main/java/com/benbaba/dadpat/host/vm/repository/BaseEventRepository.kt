package com.benbaba.dadpat.host.vm.repository

import com.benbaba.dadpat.host.http.ApiService
import com.bhx.common.event.LiveBus
import com.bhx.common.http.RetrofitManager
import com.bhx.common.mvvm.BaseRepository

/**
 * Created by Administrator on 2019/5/15.
 */

open class BaseEventRepository:BaseRepository(){
    var apiService: ApiService = RetrofitManager.getInstance().createApiService(ApiService::class.java)

    /**
     * 发送数据
     *
     * @param eventKey key
     * @param t        数据实体
     */
    fun sendData(eventKey: Any, t: Any) {
        sendData(eventKey, null, t)
    }

    fun sendData(eventKey: Any, tag: String?, t: Any) {
        LiveBus.getDefault().postEvent(eventKey, tag, t)
    }
}