package com.shendun.architecture.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.shendun.architecture.net.RepositoryManager;
import com.shendun.architecture.net.schedulers.SchedulerProvider;
import com.shendun.architecture.utils.DeviceUtils;
import com.shendun.architecture.utils.SnackBarUtil;
import com.shendun.loading.LoadingDialog;
import com.shendun.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Author:daiminzhi
 * Time: 6:09 PM
 */
public abstract class BaseFragment<T extends ViewDataBinding> extends RxFragment {
    protected static final String TAG = BaseFragment.class.getSimpleName();

    protected Context mContext;
    private LoadingDialog loadingDialog;
    protected View mRootView;

    protected T mBinding;

    protected CompositeDisposable mCompositeDisposable;

    protected PopupWindow popupWindow;

    protected PopupWindow topPopupWindow;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initEvent();

    @Override
    public void onDetach() {
        super.onDetach();
        DeviceUtils.hideSoftKeyboard(mContext, mRootView);
        hideLoading();
        loadingDialog = null;
        unDispose();
        mCompositeDisposable = null;
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        mContext = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        T binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mBinding = binding;
        try{
            EventBus.getDefault().register(this);
        }catch (Exception e){
        }
        initEvent();
        return binding.getRoot();
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

    /**
     * 显示snack
     */
    public void showMessage(@NonNull String message) {
        SnackBarUtil.showShortSnack(getActivity(), message);
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
     */
    public void showMessageDelay(@NonNull String message, Snackbar.Callback callback) {
        SnackBarUtil.showShortSnackDelay(getActivity(), message, callback);
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
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) _mActivity).showCenterToast(content);
        }
    }

    protected <T> T getRepository(Class<T> service) {
        return RepositoryManager.getInstance().obtainRetrofitService(service);
    }

    protected <T> ObservableTransformer<T, T> dispatchSchedulers() {
        return dispatchSchedulers(false);
    }

    public  <T> ObservableTransformer<T, T> dispatchSchedulers(boolean showLoading) {
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

        if (topPopupWindow != null && topPopupWindow.isShowing()) {
            topPopupWindow.dismiss();
            topPopupWindow = null;
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        dismissPopWindow();
    }

    public Context getContext() {
        return mContext;
    }
}
