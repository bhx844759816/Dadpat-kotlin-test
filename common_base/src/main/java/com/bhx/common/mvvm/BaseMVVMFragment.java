package com.bhx.common.mvvm;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.bhx.common.base.BaseLazyFragment;
import com.bhx.common.event.LiveBus;
import com.bhx.common.utils.ReflexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MVVM得Fragment得基类
 *
 * @param <T>
 */
public abstract class BaseMVVMFragment<T extends BaseViewModel> extends BaseLazyFragment {
    public T mViewModel;
    private List<Object> eventKeys = new ArrayList<>();

    @Override
    protected void initView(Bundle bundle) {
        mViewModel = VMProviders(this, ReflexUtils.getInstance(this, 0));
    }

    /**
     * 通过ViewModelProviders生成ViewModel
     *
     * @param fragment   当前类对象
     * @param modelClass BaseViewModel得子类得class对象
     * @param <T>
     * @return
     */
    protected <T extends ViewModel> T VMProviders(Fragment fragment, @NonNull Class<T> modelClass) {
        return ViewModelProviders.of(fragment).get(modelClass);

    }

    /**
     * 获取EventKey用于事件传递的绑定
     *
     * @return
     */
    protected abstract Object getEventKey();

    /**
     * 通过tag创建事件监听
     *
     * @param tag    事件tag
     * @param tClass 事件传递得对象
     * @param <T>    事件传递得泛型
     * @return
     */
    protected <T> MutableLiveData<T> registerObserver(String tag, Class<T> tClass) {
        return registerObserver(getEventKey(), tag, tClass);
    }

    /**
     * 区分具体
     *
     * @param eventKey getEventKey()获取的key
     * @param tag      针对一个页面不同的请求分tag进行事件监听
     * @param tClass   预留参数
     * @param <T>      返回类型
     * @return
     */
    protected <T> MutableLiveData<T> registerObserver(Object eventKey, String tag, Class<T> tClass) {
        String event;
        if (TextUtils.isEmpty(tag)) {
            event = (String) eventKey;
        } else {
            event = eventKey + tag;
        }
        eventKeys.add(event);
        return LiveBus.getDefault().subscribe(eventKey, tag, tClass);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (eventKeys != null && eventKeys.size() > 0) {
            for (int i = 0; i < eventKeys.size(); i++) {
                LiveBus.getDefault().clear(eventKeys.get(i));
            }
        }
    }
}
