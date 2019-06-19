package com.bhx.common.mvvm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.bhx.common.utils.ReflexUtils;

/**
 * BaseViewModel
 */
public class BaseViewModel<T extends BaseRepository> extends AndroidViewModel {
    public T mRepository;

    public BaseViewModel(Application application) {
        super(application);
        mRepository = ReflexUtils.getNewInstance(this, 0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mRepository != null) {
            mRepository.unDisposable();
        }
    }
}
