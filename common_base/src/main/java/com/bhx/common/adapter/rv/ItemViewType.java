package com.bhx.common.adapter.rv;


/**
 * RecvclerView多样式布局的列表Type区分
 */
public interface ItemViewType<T> {

    /**
     * 获取多类型布局的ID
     *
     * @return
     */
    int getItemViewLayoutId();

    /**
     * 判断item是否是当前指定的ViewType类型
     *
     * @return
     */
    boolean isViewForType(T item, int position);

    /**
     * 数据转换成布局对象
     *
     * @param holder   ViewHolder
     * @param t        数据源
     * @param position 位置
     */
    void convert(ViewHolder holder, T t, int position);

}
