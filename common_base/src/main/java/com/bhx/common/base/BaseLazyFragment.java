package com.bhx.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment的基类
 */
public abstract class BaseLazyFragment extends Fragment {
    public View rootView;
    protected boolean mIsFirstVisible = true;
    public Context mContext;
    public ViewGroup mViewGroup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        onCreateView(inflater, container);
        mViewGroup = container;
        initView(bundle);
        return rootView;
    }

    /**
     * 对外暴露得OnCreateView得方法
     */
    public void onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        boolean isVis = isHidden() || getUserVisibleHint();
        if (isVis && mIsFirstVisible) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        if (mIsFirstVisible && isResumed()) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {

    }

    /**
     * 数据懒加载
     */
    protected void lazyLoad() {


    }

    /**
     * 设置布局layout
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 实例化View
     *
     * @param bundle
     */
    protected void initView(Bundle bundle) {

    }


    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewById(int id) {
        return (T) rootView.findViewById(id);
    }
}
