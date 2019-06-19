package com.bhx.common.adapter.rv;


import androidx.collection.SparseArrayCompat;

/**
 * 添加和管理ViewType的类
 */
public final class ItemViewTypeManager<T> {
    // 存储所有类型的ViewType
    private SparseArrayCompat<ItemViewType<T>> sparseArray = new SparseArrayCompat<>();

    /**
     * 添加ItemViewType得
     *
     * @param type
     */
    public ItemViewTypeManager<T> addItemViewType(ItemViewType<T> type) {
        if (type != null) {
            sparseArray.put(sparseArray.size(), type);
        }
        return this;
    }

    public ItemViewTypeManager<T> addItemViewType(ItemViewType<T> type, int viewType) {
        if (sparseArray.get(viewType) != null) {
            throw new IllegalArgumentException("An ItemViewType is already registered for the viewType ="
                    + viewType + ". Already registered ItemViewDelegate is " + sparseArray.get(viewType));

        }
        if (type != null)
            sparseArray.put(viewType, type);
        return this;
    }

    public ItemViewTypeManager<T> removeItemViewType(ItemViewType<T> itemViewType) {
        if (itemViewType != null) {
            int indexToRemove = sparseArray.indexOfValue(itemViewType);
            if (indexToRemove >= 0) {
                sparseArray.removeAt(indexToRemove);
            }
        }
        return this;
    }

    public ItemViewTypeManager<T> removeItemViewType(int viewType) {
        int indexToRemove = sparseArray.indexOfKey(viewType);
        if (indexToRemove >= 0) {
            sparseArray.removeAt(indexToRemove);
        }
        return this;
    }

    /**
     * 获取当前布局得ViewType
     *
     * @return
     */
    public int getViewType(T item, int position) {
        for (int i = 0; i < sparseArray.size(); i++) {
            ItemViewType<T> itemViewType = sparseArray.valueAt(i);
            if (itemViewType.isViewForType(item, position)) {
                return sparseArray.keyAt(i);
            }
        }
        throw new IllegalArgumentException("No ItemViewType added that matches position=" + position + " in data source");
    }

    /**
     * 通过布局viewType获取ItemViewType
     *
     * @param viewType
     * @return
     */
    public ItemViewType<T> getItemViewType(int viewType) {
        return sparseArray.get(viewType);
    }

    /**
     * 根据不同得ViewType填充布局
     *
     * @param holder
     * @param item
     * @param position
     */
    public void convert(ViewHolder holder, T item, int position) {
        for (int i = 0; i < sparseArray.size(); i++) {
            ItemViewType<T> itemViewType = sparseArray.valueAt(i);
            if (itemViewType.isViewForType(item, position)) {
                itemViewType.convert(holder, item, position);
                return;
            }
        }
        throw new IllegalArgumentException(
                "No ItemViewType added that matches position=" + position + " in data source");
    }

    /**
     * 通过ViewType获取布局ID
     *
     * @param viewType
     * @return
     */
    public int getItemViewLayoutId(int viewType) {
        return getItemViewType(viewType).getItemViewLayoutId();
    }

    /**
     * 通过itemViewType获取在sparseArray得位置
     *
     * @param itemViewType
     * @return
     */
    public int getItemViewType(ItemViewType<T> itemViewType) {
        return sparseArray.indexOfValue(itemViewType);
    }

    public int getItemViewTypeCount() {
        return sparseArray.size();
    }
}
