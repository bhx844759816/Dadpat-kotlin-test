package com.benbaba.dadpat.host.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.bhx.common.utils.DensityUtil;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int topSpace;

    public GridSpaceItemDecoration(Context context) {
        this.topSpace = DensityUtil.dip2px(context, 30);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = topSpace;
    }


}
