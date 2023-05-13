package com.shendun.architecture.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.gyf.immersionbar.ImmersionBar;
import com.shendun.architecture.net.RepositoryManager;
import com.shendun.architecture.net.schedulers.SchedulerProvider;
import com.shendun.architecture.utils.SnackBarUtil;
import com.shendun.architecture.utils.SystemStatusBarUtil;
import com.shendun.renter.R;
import com.shendun.loading.LoadingDialog;
import com.shendun.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Author:daiminzhi
 * Time: 5:40 PM
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends RxActivity {

    protected T mBinding;

    protected Context mContext;

    private LoadingDialog loadingDialog;

    protected CompositeDisposable mCompositeDisposable;

    protected PopupWindow popupWindow;

    protected PopupWindow topPopupWindow;

    protected PopupWindow bottomPopupWindow;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initEvent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDelegate.onCreate(savedInstanceState);
        mContext = this;
        //初始化沉浸式
//        initImmersionBar();
        doBeforeSetcontentView();
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        doAfterSetContentView();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
        initEvent();
    }

    /**
     * 设置layout前配置
     */
    protected void doBeforeSetcontentView() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, android.R.color.white));
        }
    }

    protected void doAfterSetContentView() {
        SystemStatusBarUtil.setStatusBarLightMode(this);
    }

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.theme_blue)
                .init();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    /**
     * 显示加载
     */
    public void showLoading() {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog.Builder(mContext).cancelable(true)
                .canceledOnTouchOutside(false)
                .build();
        }
        loadingDialog.show();
    }

    /**
     * 隐藏加载
     */
    public void hideLoading() {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
        loadingDialog = null;
        unDispose();
        mCompositeDisposable = null;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mContext = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissPopWindow();
    }

    /**
     * 显示snack
     *
     * @param message 字符串
     */
    public void showMessage(@NonNull String message) {
        SnackBarUtil.showShortSnack(this, message);
    }

    /**
     * 显示snack
     *
     * @param messageResId 字符串id
     * @param formatArgs format匹配项
     */
    public void showMessage(@NonNull int messageResId, Object... formatArgs) {
        showMessage(getString(messageResId, formatArgs));
    }

    /**
     * 显示snack
     *
     * @param message 字符串
     * @param callback 延迟结束的回调
     */
    public void showMessageDelay(@NonNull String message, Snackbar.Callback callback) {
        SnackBarUtil.showShortSnackDelay(this, message, callback);
    }

    /**
     * 显示snack
     *
     * @param messageResId 字符串id
     * @param callback 延迟结束的回调
     * @param formatArgs format匹配项
     */
    public void showMessageDelay(@NonNull int messageResId, Snackbar.Callback callback,
        Object... formatArgs) {
        showMessageDelay(getString(messageResId, formatArgs), callback);
    }

    /**
     * 显示居中toast
     *
     * @param content 内容
     */
    public void showCenterToast(String content) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast_custom_center, null);
        TextView text = layout.findViewById(R.id.tv);
        text.setText(content);
        final Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    protected <T> T getRepository(Class<T> service) {
        return RepositoryManager.getInstance().obtainRetrofitService(service);
    }

    protected <T> ObservableTransformer<T, T> dispatchSchedulers() {
        return dispatchSchedulers(false);
    }

    public <T> ObservableTransformer<T, T> dispatchSchedulers(boolean showLoading) {
        return observable -> observable.compose(SchedulerProvider.getInstance().applySchedulers())
            .doOnSubscribe(disposable -> {
                if (showLoading) {
                    showLoading();
                }
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .doFinally(this::hideLoading)
            .compose(bindToLifecycle());
    }

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入容器集中处理
    }

    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
    }

    /**
     * 隐藏弹窗
     */
    protected void dismissPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * 隐藏弹窗
     */
    protected void dismissTopAndBottomPopWindow() {
        if (topPopupWindow != null && topPopupWindow.isShowing()) {
            topPopupWindow.dismiss();
            topPopupWindow = null;
        }

        if (bottomPopupWindow != null && bottomPopupWindow.isShowing()) {
            bottomPopupWindow.dismiss();
            bottomPopupWindow = null;
        }
    }
}
