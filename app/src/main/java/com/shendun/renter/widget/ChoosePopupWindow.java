package com.shendun.renter.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;

import com.shendun.renter.R;

import timber.log.Timber;

/**
 * Author:daiminzhi
 * Time: 2:05 PM
 */
public class ChoosePopupWindow extends PopupWindow {

    private static final float MAX_ALPHA_VALUE = 1f;
    private static final float MIN_ALPHA_VALUE = 0.3f;

    private Window window = null;

    private boolean customDismissAnimator = false;

    private boolean touchOutsideDismiss = true;

    private boolean alphaAnimEnable = true;

    public ChoosePopupWindow(Context context, @LayoutRes int layout, LogicCallback callback) {
        super(context);
        init(context, layout, callback);
    }

    private void init(Context context, @LayoutRes int layout, LogicCallback callback) {
        View rootView = LayoutInflater.from(context).inflate(layout, null);
        setContentView(rootView);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.cloud_popup_choicepic_anim_style);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchOutsideDismiss) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        dismiss();
                        return true;
                    }
                    return false;
                } else {
                    Timber.e("当前点击的view：%s",v.toString());
                    Timber.e("当前点击的event：%s",event.toString());
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        v.dispatchTouchEvent(event);
                        return true;
                    }
                    return false;
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!customDismissAnimator) {
                    changeWindowAlphaAnim(MIN_ALPHA_VALUE, MAX_ALPHA_VALUE, window);
                }
            }
        });
        callback.logic(rootView);
    }

    public ChoosePopupWindow setWindowSize(int width, int height) {
        setWidth(width);
        setHeight(height);
        this.update();
        return this;
    }

    public ChoosePopupWindow setWindowAnimationStyle(int animationStyle) {
        setAnimationStyle(animationStyle);
        this.customDismissAnimator = true;
        this.update();
        return this;
    }

    public ChoosePopupWindow show(View authView, Window window) {
        this.window = window;
        showAtLocation(authView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        changeWindowAlphaAnim(MAX_ALPHA_VALUE, MIN_ALPHA_VALUE, window);
        return this;
    }

    public ChoosePopupWindow showTop(View authView, Window window) {
        this.window = window;
        showAtLocation(authView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        changeWindowAlphaAnim(MAX_ALPHA_VALUE, MIN_ALPHA_VALUE, window);
        return this;
    }

    public ChoosePopupWindow showAtLocation(LocationSetCallback location) {
        location.location(this);
        return this;
    }

    public ChoosePopupWindow setAlphaAnimEnable(boolean alphaAnimEnable) {
        this.alphaAnimEnable = alphaAnimEnable;
        if (!alphaAnimEnable) {
            setBackgroundDrawable(null);
        } else {
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return this;
    }

    public ChoosePopupWindow setBackgroundTransparent(boolean isTransparent) {
        if (isTransparent){
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return this;
    }

    public ChoosePopupWindow setTouchOutsideDismiss(boolean touchOutsideDismiss) {
        this.touchOutsideDismiss = touchOutsideDismiss;
        if (touchOutsideDismiss) {
            setOutsideTouchable(true);
            setFocusable(true);
        } else {
            setOutsideTouchable(false);
            setFocusable(false);
        }
        return this;
    }

    public void setOnDismissListener(DismissCallback dismissCallback){
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!customDismissAnimator) {
                    changeWindowAlphaAnim(MIN_ALPHA_VALUE, MAX_ALPHA_VALUE, window);
                }

                dismissCallback.onDismiss();
            }
        });
    }

    /**
     * 在popwindow显示和隐藏时动画改变背景的透明度
     *
     * @param startValue 透明度起始值
     * @param endValue   透明度结束值
     */
    private void changeWindowAlphaAnim(float startValue, float endValue, Window window) {
        if (!alphaAnimEnable) {
            return;
        }
        Timber.tag("dmz").d("开始执行背景变更动画");
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, endValue);
        valueAnimator.setDuration(500).start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Timber.tag("dmz").d("执行背景变更动画参数：%f",value);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.alpha = value;
                window.setAttributes(layoutParams);
            }
        });
    }

    public interface LogicCallback {
        void logic(View view);
    }

    public interface LocationSetCallback {
        void location(PopupWindow popupWindow);
    }

    public interface DismissCallback {
        void onDismiss();
    }
}

