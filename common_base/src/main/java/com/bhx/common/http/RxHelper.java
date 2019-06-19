package com.bhx.common.http;


import com.bhx.common.utils.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * 将返回数据去掉包装类并封装异常的回调处理
 * Created by Administrator on 2018/2/10.
 */
public class RxHelper {
    public static <T> ObservableTransformer<T, T> io_main() {




        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<BaseResponse<T>, T> handleResult() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .onErrorResumeNext(new ErrorResumeFunction<>())
                .flatMap(new ResponseFunction<T>());

    }

    /**
     * 错误的处理
     *
     * @param <T>
     */
    private static class ErrorResumeFunction<T> implements Function<Throwable, ObservableSource<? extends BaseResponse<T>>> {

        @Override
        public ObservableSource<? extends BaseResponse<T>> apply(Throwable throwable) throws Exception {
            return Observable.error(CustomException.handleException(throwable));
        }
    }

    /**
     * 获取正确数据的处理
     *
     * @param <T>
     */
    private static class ResponseFunction<T> implements Function<BaseResponse<T>, ObservableSource<T>> {

        @Override
        public ObservableSource<T> apply(BaseResponse<T> tResponse) throws Exception {
            int code = tResponse.getCode();
            if (code == 200) {
                return Observable.just(tResponse.getData());
            }
            return Observable.error(new ApiException(code, tResponse.getMessage()));
        }
    }

}
