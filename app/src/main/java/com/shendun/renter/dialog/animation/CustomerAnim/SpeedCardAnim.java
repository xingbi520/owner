package com.shendun.renter.dialog.animation.CustomerAnim;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.shendun.renter.dialog.animation.BaseAnimatorSet;


/**
 * ************************************************************
 * <p/>
 * ************************************************************
 * Created by wenxb on 2017/11/6.
 */


public class SpeedCardAnim extends BaseAnimatorSet {

    public SpeedCardAnim() {
        duration = 700;
    }

    @Override
    public void setAnimation(View view) {
        view.setPivotX(getScreenSize(view.getContext())[0]/2);
        view.setPivotY(0);
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(view, "rotationX", -180,45,-45,0),//
//                ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 0.5f, 0.9f, 0.8f, 0.9f, 1),//
                ObjectAnimator.ofFloat(view, "alpha", 0, 0.5f,0.5f,1));
    }

    private int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }
}
