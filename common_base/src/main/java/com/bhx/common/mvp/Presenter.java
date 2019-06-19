package com.bhx.common.mvp;

public interface Presenter<V extends BaseView> {

    /**
     * 注册View
     *
     * @param v BaseView的字类
     */
    void registerView(V v);


    /**
     * 解绑View
     */
    void destroy();
}
