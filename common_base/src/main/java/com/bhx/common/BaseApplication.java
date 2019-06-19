package com.bhx.common;

import android.app.Application;
import android.content.Context;

import com.bhx.common.http.RetrofitManager;
import com.bhx.common.utils.LogUtils;
import com.bhx.common.utils.ToastUtils;

/**
 * 所有Module都要继承BaseApplication来获取全局上下文
 */
public class BaseApplication extends Application {
    private Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取全局上下文
        applicationContext = getApplicationContext();
        //初始化Log日志
        LogUtils.init();
        //
        ToastUtils.init(applicationContext);
    }
}
