package com.benbaba.dadpat.host.view;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * 取消滚动得Manager
 */
public class NoScrollLayoutManager extends GridLayoutManager {
    private Context context;

    public NoScrollLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        this.context = context;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }


}
