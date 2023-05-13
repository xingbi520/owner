package com.shendun.renter.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;

import com.shendun.renter.R;

/**
 * Author:daiminzhi
 * Time: 9:19 AM
 */
public class CloudMaxHeightRecyclerView extends RecyclerView {
    private int mMaxHeight;

    public CloudMaxHeightRecyclerView(Context context) {
        super(context);
    }

    public CloudMaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public CloudMaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr =
            context.obtainStyledAttributes(attrs, R.styleable.CloudMaxHeightRecyclerView);
        mMaxHeight = arr.getLayoutDimension(R.styleable.CloudMaxHeightRecyclerView_cloud_maxHeight,
            mMaxHeight);
        arr.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
