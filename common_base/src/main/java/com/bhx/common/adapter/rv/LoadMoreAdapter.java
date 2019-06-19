package com.bhx.common.adapter.rv;

import android.content.Context;

public class LoadMoreAdapter<T> extends MultiItemTypeAdapter<T> {

    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    public LoadMoreAdapter(Context context) {

        super(context);
        //添加底部加载更多得布局
        addItemViewType(new ItemViewType<T>() {
            @Override
            public int getItemViewLayoutId() {
                return 0;
            }

            @Override
            public boolean isViewForType(T item, int position) {
                return false;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
