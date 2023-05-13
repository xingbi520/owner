package com.shendun.renter.widget.layoutmanager;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:daiminzhi
 * Time: 5:41 PM
 */
public class GridLayoutManagerWrap extends GridLayoutManager {
    public GridLayoutManagerWrap(Context context, AttributeSet attrs, int defStyleAttr,
        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridLayoutManagerWrap(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerWrap(Context context, int spanCount, int orientation,
        boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 在这里通过try来捕获这个异常即可
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
