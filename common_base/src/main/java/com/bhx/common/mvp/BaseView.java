package com.bhx.common.mvp;

import com.trello.rxlifecycle2.LifecycleTransformer;

public interface BaseView {
    /**
     * 展示loading对话框
     */
    void showLoadingDialog();

    /**
     * 取消展示Loading对话框
     */
    void dismissLoadingDialog();

    /**
     * 展示无网络的View
     */
    void showNotNetWork();

    /**
     * 展示失败重试
     */
    void showFaild();

    /**
     * 绑定生命周期
     *
     * @param <T>
     * @return
     */
    <T> LifecycleTransformer<T> bindToLife();
}
