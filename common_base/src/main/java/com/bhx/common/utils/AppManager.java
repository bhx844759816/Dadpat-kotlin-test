package com.bhx.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.Stack;

/**
 * 管理AppManager
 */
public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;


    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    public boolean isLastActivity() {
        return activityStack.size() == 1;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public void restartApp(Context context) {
        finishAllActivity();
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            System.exit(0);
        }

    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
//            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束除了当前的Activity的所有Activity
     *
     * @param activity
     */
    public void finishOthersActivity(Activity activity) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity_ = iterator.next();
            if (!activity_.equals(activity)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Activity activity) {
        LogUtils.i("栈内信息："+activityStack.toString());
        finishOthersActivity(activity);
        LogUtils.i("栈内信息："+activityStack.toString());
        ActivityManager activityMgr = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityMgr != null) {
            activityMgr.killBackgroundProcesses(activity.getPackageName());
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
