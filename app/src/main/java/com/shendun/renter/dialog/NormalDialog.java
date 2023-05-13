package com.shendun.renter.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.shendun.renter.R;

@SuppressWarnings("deprecation")
public class NormalDialog extends BaseAlertDialog<NormalDialog> {
    /** title underline */
    private View mVLineTitle;
    /** vertical line between btns */
    private View mVLineVertical;
    /** vertical line between btns */
    private View mVLineVertical2;
    /** horizontal line above btns */
    private View mVLineHorizontal;
    /** title underline color(标题下划线颜色) */
    private int mTitleLineColor = Color.parseColor("#61AEDC");
    /** title underline height(标题下划线高度) */
    private float mTitleLineHeight = 1f;
    /** btn divider line color(对话框之间的分割线颜色(水平+垂直)) */
    private int mDividerColor = Color.parseColor("#DCDCDC");

    public static final int STYLE_ONE = 0;
    public static final int STYLE_TWO = 1;
    private int mStyle = STYLE_ONE;

    public NormalDialog(Context context) {
        super(context);

        /** default value*/
        mTitleTextColor = context.getResources().getColor(R.color.dialog_nomal);
        mTitleTextSize = context.getResources().getDimension(R.dimen.x28);
        mContentTextColor =context.getResources().getColor(R.color.dialog_nomal);
        mContentTextSize = context.getResources().getDimension(R.dimen.x28);
        mLeftBtnTextColor = Color.parseColor("#8a000000");
        mRightBtnTextColor = Color.parseColor("#8a000000");
        mMiddleBtnTextColor = Color.parseColor("#8a000000");
        mLeftBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        mRightBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        mMiddleBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        /** default value*/
    }

    public NormalDialog(Context context, boolean softVisible) {
        super(context,true);

        /** default value*/
        mTitleTextColor = context.getResources().getColor(R.color.dialog_nomal);
        mTitleTextSize = context.getResources().getDimension(R.dimen.x28);
        mContentTextColor =context.getResources().getColor(R.color.dialog_nomal);
        mContentTextSize = context.getResources().getDimension(R.dimen.x28);
        mLeftBtnTextColor = Color.parseColor("#8a000000");
        mRightBtnTextColor = Color.parseColor("#8a000000");
        mMiddleBtnTextColor = Color.parseColor("#8a000000");
        mLeftBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        mRightBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        mMiddleBtnTextSize = context.getResources().getDimension(R.dimen.x24);
        /** default value*/
    }

    @Override
    public View onCreateView() {
        /** title */
        mTvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlContainer.addView(mTvTitle);

        /** title underline */
        mVLineTitle = new View(mContext);
        mLlContainer.addView(mVLineTitle);

        /** content */
        mTvContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlContainer.addView(mTvContent);

        mVLineHorizontal = new View(mContext);
        mVLineHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        mLlContainer.addView(mVLineHorizontal);

        /** contentView */
        mContentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlContainer.addView(mContentView);

        mVLineHorizontal = new View(mContext);
        mVLineHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        mLlContainer.addView(mVLineHorizontal);

        /** btns */
        mTvBtnLeft.setLayoutParams(new LinearLayout.LayoutParams(0, (int) mContext.getResources().getDimension(R.dimen.y120), 1));
        mLlBtns.addView(mTvBtnLeft);

        mVLineVertical = new View(mContext);
        mVLineVertical.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        mLlBtns.addView(mVLineVertical);

        mTvBtnMiddle.setLayoutParams(new LinearLayout.LayoutParams(0, (int) mContext.getResources().getDimension(R.dimen.y120), 1));
        mLlBtns.addView(mTvBtnMiddle);

        mVLineVertical2 = new View(mContext);
        mVLineVertical2.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        mLlBtns.addView(mVLineVertical2);

        mTvBtnRight.setLayoutParams(new LinearLayout.LayoutParams(0,(int) mContext.getResources().getDimension(R.dimen.y120), 1));
        mLlBtns.addView(mTvBtnRight);

        mLlContainer.addView(mLlBtns);

        return mLlContainer;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();

        /** title */
        if (mStyle == STYLE_ONE) {
            mTvTitle.setMinHeight((int) mContext.getResources().getDimension(R.dimen.y80));
            mTvTitle.setGravity(Gravity.CENTER_VERTICAL);
            mTvTitle.setPadding(dp2px(15), dp2px(5), dp2px(0), dp2px(5));
            mTvTitle.setVisibility(mIsTitleShow ? View.VISIBLE : View.GONE);
        } else if (mStyle == STYLE_TWO) {
            mTvTitle.setGravity(Gravity.CENTER);
            mTvTitle.setPadding(dp2px(0), (int) mContext.getResources().getDimension(R.dimen.y30), dp2px(0), dp2px(0));
        }

        /** title underline */
        mVLineTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                dp2px(mTitleLineHeight)));
        mVLineTitle.setBackgroundColor(mTitleLineColor);
        mVLineTitle.setVisibility(mIsTitleShow && mStyle == STYLE_ONE ? View.VISIBLE : View.GONE);

        /** content */
        if (mStyle == STYLE_ONE) {
            mTvContent.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
            mTvContent.setMinHeight(dp2px(68));
            mTvContent.setGravity(mContentGravity);
        } else if (mStyle == STYLE_TWO) {
            mTvContent.setPadding((int) mContext.getResources().getDimension(R.dimen.x30), (int) mContext.getResources().getDimension(R.dimen.x45),
                    (int) mContext.getResources().getDimension(R.dimen.x30),(int) mContext.getResources().getDimension(R.dimen.x45));
            mTvContent.setMinHeight((int) mContext.getResources().getDimension(R.dimen.y150));
            mTvContent.setGravity(Gravity.CENTER);
        }

        /** contentView */
        if (mStyle == STYLE_ONE) {
            mContentView.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
            mContentView.setMinimumHeight(dp2px(68));
        } else if (mStyle == STYLE_TWO) {
            mContentView.setPadding((int) mContext.getResources().getDimension(R.dimen.x30), (int) mContext.getResources().getDimension(R.dimen.x45),
                    (int) mContext.getResources().getDimension(R.dimen.x30),(int) mContext.getResources().getDimension(R.dimen.x45));
            mContentView.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen.y150));
        }

        /** btns */
        mVLineHorizontal.setBackgroundColor(mDividerColor);
        mVLineVertical.setBackgroundColor(mDividerColor);
        mVLineVertical2.setBackgroundColor(mDividerColor);

        if (mBtnNum == 1) {
            mTvBtnLeft.setVisibility(View.GONE);
            mTvBtnRight.setVisibility(View.GONE);
            mVLineVertical.setVisibility(View.GONE);
            mVLineVertical2.setVisibility(View.GONE);
        } else if (mBtnNum == 2) {
            mTvBtnMiddle.setVisibility(View.GONE);
            mVLineVertical.setVisibility(View.GONE);
        } else if (mBtnNum == 0) {
            mVLineHorizontal.setVisibility(View.GONE);
            mLlBtns.setVisibility(View.GONE);
        }

        /**set background color and corner radius */
        float radius = dp2px(mCornerRadius);
        mLlContainer.setBackgroundDrawable(CornerUtils.cornerDrawable(mBgColor, radius));
        mTvBtnLeft.setBackgroundDrawable(CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 0));
        mTvBtnRight.setBackgroundDrawable(CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 1));
        mTvBtnMiddle.setBackgroundDrawable(CornerUtils.btnSelector(mBtnNum == 1 ? radius : 0, mBgColor, mBtnPressColor, -1));
    }

    // --->属性设置

    /** set style(设置style) */
    public NormalDialog style(int style) {
        this.mStyle = style;
        return this;
    }

    /** set title underline color(设置标题下划线颜色) */
    public NormalDialog titleLineColor(int titleLineColor) {
        this.mTitleLineColor = titleLineColor;
        return this;
    }

    /** set title underline height(设置标题下划线高度) */
    public NormalDialog titleLineHeight(float titleLineHeight_DP) {
        this.mTitleLineHeight = titleLineHeight_DP;
        return this;
    }

    /** set divider color between btns(设置btn分割线的颜色) */
    public NormalDialog dividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        return this;
    }

    public NormalDialog noBtn() {

        return this;
    }

}
