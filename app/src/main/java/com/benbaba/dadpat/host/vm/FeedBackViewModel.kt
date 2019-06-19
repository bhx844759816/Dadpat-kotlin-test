package com.benbaba.dadpat.host.vm

import android.app.Application
import com.benbaba.dadpat.host.vm.repository.FeedBackRepository
import com.bhx.common.mvvm.BaseViewModel

/**
 * 反馈意见
 */
class FeedBackViewModel(application: Application) : BaseViewModel<FeedBackRepository>(application) {
    //    params.put("userName", mUser.getUserName());
//    params.put("userId", mUser.getUserId());
//    params.put("content", content);
    fun sendFeedBack(userName: String, userId: String, content: String) {
        mRepository.sendFeedBack(userName, userId, content)
    }
}