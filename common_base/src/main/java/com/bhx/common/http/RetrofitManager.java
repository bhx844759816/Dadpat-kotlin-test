package com.bhx.common.http;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bhx.common.utils.HttpsUtils;
import com.bhx.common.utils.SSLSocketFactoryUtils;
import com.bhx.common.utils.SSLSocketFactoryUtils2;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLSocketFactory;

/**
 * Retrofit单例获取ApiService类
 */
public class RetrofitManager {

    private static volatile RetrofitManager INSTANCE;

    private Retrofit retrofit;//Retrofit对象

    public static RetrofitManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化Retrofit
     */
    public void init(Builder builder) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.connectTimeout(builder.timeOut, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(builder.timeOut, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(builder.timeOut, TimeUnit.SECONDS);
        okHttpBuilder.addInterceptor(new LoggingInterceptor());
        if (builder.interceptorList != null) {
            for (Interceptor interceptor : builder.interceptorList) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }
        if (builder.httpsInputStreams != null) {
            okHttpBuilder.sslSocketFactory(SSLSocketFactoryUtils2.getSSLSocketFactory(builder.httpsInputStreams),
                    SSLSocketFactoryUtils.createTrustAllManager());
            okHttpBuilder.hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier());
        }

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactoryUnsafe();
        okHttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        retrofit = new Retrofit.Builder()
                .baseUrl(builder.url)
                .client(okHttpBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 创建请求的代理接口
     *
     * @param clazz Api接口
     */
    public <T> T createApiService(Class<T> clazz) {
        if (retrofit == null) {
            throw new RuntimeException("retrofit is null ,RetrofitManager is not init in Application");
        }
        return retrofit.create(clazz);
    }

    public static class Builder {
        String url;
        List<Interceptor> interceptorList;
        int timeOut = 5;
        InputStream[] httpsInputStreams;

        public Builder setBaseUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setInterceptorList(List<Interceptor> interceptorList) {
            this.interceptorList = interceptorList;
            return this;
        }

        public Builder setTimeOut(int timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder setHttpsInputStream(InputStream... inputStream) {
            this.httpsInputStreams = inputStream;
            return this;
        }

    }
}
