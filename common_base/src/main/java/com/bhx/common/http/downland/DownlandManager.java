package com.bhx.common.http.downland;

import com.bhx.common.http.RetrofitManager;
import com.bhx.common.http.upload.UploadManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import java.io.File;
import java.io.IOException;

/**
 * 下载得管理类
 */
public class DownlandManager {
    private static volatile ApiService apiService;

    public static Observable<Object> downland(String url, FileInfo fileInfo) {
        createApiService();
        return apiService.downland(url).flatMap(responseBody -> Observable.create(new DownlandFileObservable(responseBody, fileInfo)));
    }

    private static void createApiService() {
        if (apiService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            Retrofit manager = new Retrofit.Builder()
                    .baseUrl("https://www.goofypapa.com")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
            apiService = manager.create(ApiService.class);
        }

    }


    interface ApiService {
        @GET
        @Streaming
        Observable<ResponseBody> downland(@Url String url);
    }
}
