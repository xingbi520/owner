package com.shendun.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * 弹窗浮动加载进度条
 */
public class LoadingDialog {

    /**
     * 是否可取消
     */
    private boolean cancelable;
    /**
     * 点击dialog以外的区域是否关闭
     */
    private boolean canceledOnTouchOutside;

    private Context context;

    /**
     * 加载数据对话框
     */
    private Dialog mLoadingDialog;

    private ImageView loadImage;

    private LoadingDialog(Builder builder) {
        cancelable = builder.cancelable;
        canceledOnTouchOutside = builder.canceledOnTouchOutside;
        context = builder.mContext;
        init();
    }

    private void init() {
        mLoadingDialog = new Dialog(context, R.style.LoadingDialogStyle);
        View v = LayoutInflater.from(context)
            .inflate(R.layout.layout_dialog_loading, null);             // 得到加载view
        RelativeLayout layout = v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        loadImage = v.findViewById(R.id.img);
        //把动画文件设置为imageView的背景
        Glide.with(context)
            .asGif()
            .load(R.drawable.smarthome_loading_animation)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .listener(new RequestListener<GifDrawable>() {

                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                    Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Object model,
                    Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                    resource.setLoopCount(GifDrawable.LOOP_FOREVER);
                    return false;
                }
            })
            .into(loadImage);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        mLoadingDialog.setContentView(layout,
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        Window window = mLoadingDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(params);
        }
    }

    public void show() {
        if (!isShowing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    public void dismiss() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }

    public boolean isShowing() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public static final class Builder {

        //设置默认值
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder() {
        }

        public Builder cancelable(boolean val) {
            cancelable = val;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean val) {
            canceledOnTouchOutside = val;
            return this;
        }

        public LoadingDialog build() {
            return new LoadingDialog(this);
        }
    }
}