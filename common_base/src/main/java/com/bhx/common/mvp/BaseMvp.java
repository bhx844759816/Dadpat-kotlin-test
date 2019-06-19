package com.bhx.common.mvp;

public interface BaseMvp< V extends BaseView, P extends Presenter> {
    V createView();
    P createPresenter();
}
