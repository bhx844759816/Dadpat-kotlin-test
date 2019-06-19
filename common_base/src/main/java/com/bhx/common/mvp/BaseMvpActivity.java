package com.bhx.common.mvp;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Mvp模式下的Activity 通过rxLifecycle控制RxJava的生命周期
 *
 * @param <P>
 * @param <V>
 */
public abstract class BaseMvpActivity<V extends BaseView, P extends Presenter> extends RxAppCompatActivity
        implements BaseMvp<V, P>, BaseView {
    P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mPresenter = createPresenter();
        if (mPresenter != null) mPresenter.registerView(createView());
    }

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    /**
     * 绑定到RxLifecycle
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindToLifecycle();
    }
}
