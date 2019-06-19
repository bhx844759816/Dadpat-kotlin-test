package com.bhx.common.http.upload;

import com.bhx.common.utils.LogUtils;

import io.reactivex.observers.DefaultObserver;

/**
 * 上传进度监听得观察者
 *
 * @param <T>
 */
public abstract class UploadProgressObserver<T> extends DefaultObserver<T> {



    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("UploadProgressObserver onStart");
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {
        LogUtils.i("UploadProgressObserver onComplete");
    }

    /**
     * 进度回调
     *
     * @param bytesWritten  当前写入得长度
     * @param contentLength 当前上传文件得长度
     * @param params        当前上传得文件对象
     */
    public void onProgressChange(long bytesWritten, long contentLength, UploadParam params) {
        onProgress(params, (int) (bytesWritten * 100 / contentLength));
    }

    /**
     * 上传成功
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 上传失败
     * @param throwable
     */
    public abstract void onFail(Throwable throwable);

    /**
     * 上传进度
     * @param params
     * @param percent
     */
    public abstract void onProgress(UploadParam params, int percent);
}
