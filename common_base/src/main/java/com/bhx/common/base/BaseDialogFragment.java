package com.bhx.common.base;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.bhx.common.R;


/**
 * Dialog得基类
 * Created by Administrator on 2018/3/2.
 */
public abstract class BaseDialogFragment extends DialogFragment {
    private static final int DEFAULT_STYLES = R.style.dialog;
    Context mContext;

    public BaseDialogFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(getLayoutId(), container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    /**
     * 获取布局得ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 界面启动
     */
    @Override
    public void onStart() {
        super.onStart();
//        initWindow();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, DEFAULT_STYLES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化window参数
     */
    public void initWindow() {
        if (getDialog() != null) {
            Window dialogWindow = getDialog().getWindow();
            if (dialogWindow != null) {
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(lp);
            }
        }

    }


    public void initView(View view) {

    }

    public void initData() {

    }
}

