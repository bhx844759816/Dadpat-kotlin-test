package com.bhx.common.adapter.rv;

import android.content.Context;

import java.util.List;

/**
 * 单一布局得列表适配器
 *
 * @param <T> 数据源对象
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(Context context) {
        super(context);
        initItemViewType();
    }

    public CommonAdapter(Context context, List<T> datas) {
        super(context, datas);
        initItemViewType();
    }

    private void initItemViewType() {
        addItemViewType(new ItemViewType<T>() {
            @Override
            public int getItemViewLayoutId() {
                return CommonAdapter.this.itemLayoutId();
            }

            @Override
            public boolean isViewForType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract int itemLayoutId();

    protected abstract void convert(ViewHolder holder, T t, int position);
}
