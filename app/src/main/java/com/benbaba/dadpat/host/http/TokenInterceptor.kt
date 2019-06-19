package com.benbaba.dadpat.host.http

import android.content.Context
import android.text.TextUtils
import com.benbaba.dadpat.host.App
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.utils.SPUtils
import com.bhx.common.BaseApplication
import com.bhx.common.utils.LogUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 添加token请求头
 */
class TokenInterceptor : Interceptor {
    private val TOKEN_KEY = "Authorization"
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = App.instance().getToken()
        LogUtils.i("token:$token")
        if (!TextUtils.isEmpty(token)) {
            val request = originalRequest
                .newBuilder()
                .addHeader(TOKEN_KEY, token)
                .build()
            return chain.proceed(request)
        }
        return chain.proceed(originalRequest)
    }
}