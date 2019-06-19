package com.benbaba.dadpat.host.http

import android.text.TextUtils
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.HttpResult
import com.bhx.common.event.LiveBus
import com.bhx.common.http.ApiException
import com.bhx.common.http.CustomException
import com.bhx.common.utils.LogUtils
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.logging.Logger


/**
 * Created by Administrator on 2019/5/15.
 */
/**
 * 扩展加载显示对话框
 */


fun <T> Observable<HttpResult<T>>.action(
    onNext: (T) -> Unit
): Disposable {
    return this.compose(handleResultForLoadingDialog())
        .subscribe(Consumer(onNext), Consumer {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_HTTP_REQUEST_ERROR, Constants.TAG_HTTP_REQUEST_ERROR, it)
        }, Action {

        })
}

//fun <T> applySchedulers(): ObservableTransformer<HttpResult<T>, T> {
//    return ObservableTransformer { observable ->
//        observable.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .unsubscribeOn(Schedulers.io())
//            .onErrorResumeNext(ErrorResumeFunction())
//            .flatMap(ResponseFunction())
//    }
//}

/**
 * 扩展RxJava得线程切换
 */
fun <T> applySchedulers(): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> applySchedulersMaybe(): MaybeTransformer<T, T> {
    return MaybeTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> applySchedulersFlowable(): FlowableTransformer<T, T> {
    return FlowableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * 扩展RxJava得线程切换
 */
fun <T> applySchedulersForLoadingDialog(): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .doOnSubscribe {
                LogUtils.i("弹出加载对话框")
                if (!it.isDisposed) {
                    LiveBus.getDefault()
                        .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
                }
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .doFinally {
                LiveBus.getDefault().postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
            }
    }
}

/**
 * 转换
 */
fun <T> handleResult(): ObservableTransformer<HttpResult<T>, T> {
    return ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .onErrorResumeNext(ErrorResumeFunction())
            .flatMap(ResponseFunction())
    }
}

/**
 * 转换
 */
fun <T> handleResultForLoadingDialog(): ObservableTransformer<HttpResult<T>, T> {
    return ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .doOnSubscribe {
                if (!it.isDisposed) {
                    LiveBus.getDefault()
                        .postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, true)
                }
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .doFinally {
                LogUtils.i("取消对话框")
                LiveBus.getDefault().postEvent(Constants.EVENT_KEY_LOADING_DIALOG, Constants.TAG_LOADING_DIALOG, false)
            }
            .onErrorResumeNext(ErrorResumeFunction())
            .flatMap(ResponseFunction())
    }
}

class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<out HttpResult<T>>> {
    override fun apply(t: Throwable): ObservableSource<out HttpResult<T>> {
        if (t is ApiException) {
            return Observable.error<HttpResult<T>>(t)
        }
        return Observable.error<HttpResult<T>>(CustomException.handleException(t))
    }
}

class ResponseFunction<T> : Function<HttpResult<T>, ObservableSource<T>> {
    override fun apply(t: HttpResult<T>): ObservableSource<T> {
        return if (t.success) {
            if (t.data == null) {
                Observable.just(Any() as T)
            } else {
                Observable.just(t.data)
            }
        } else {
            LogUtils.i("http error:" + t.msg)
            var code = 0
            if (!TextUtils.isEmpty(t.code)) {
                code = Integer.parseInt(t.code)
            }
            Observable.error(ApiException(code, t.msg))
        }
    }

}

