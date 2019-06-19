package com.bhx.common.mvp;

/**
 * Presenter的实现类
 */
public abstract class BasePresenterImpl<V extends BaseView, M extends BaseModel> implements Presenter<V> {

    private V view;
    private M model;

    BasePresenterImpl() {
        model = createModel();
    }

    @Override
    public void registerView(V v) {
        this.view = v;
    }

    @Override
    public void destroy() {
        if (view != null) {
            view = null;
        }
        onViewDestroy();
    }

    /**
     * 处理其他资源的回收
     */
    abstract void onViewDestroy();

    abstract M createModel();
}
