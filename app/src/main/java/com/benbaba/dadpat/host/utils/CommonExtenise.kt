package com.benbaba.dadpat.host.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 封装扩展函数
 */

/**
 * 扩展StartActivity
 */
inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

//fun View.onClickReply(action: suspend () -> Unit) {
//  val eventActor = actor<Unit>
//}

///**
// * 扩展Observable的设置观察责的线程切换
// */
//fun <T : Any> Observable<T>.action(
//    onNext: (T) -> Unit,
//    onError: (Throwable) -> Unit,
//    onComplete: () -> Unit = {}
//): Disposable {
//    return this.subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(onNext, onError, onComplete)
//}


