package com.shendun.renter.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;

import com.shendun.renter.utils.PolicyPermissionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * Author:daiminzhi
 * Time: 6:47 下午
 */
public class CheckPermissionUtil {

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(Fragment fragment,
        String permission, ForwardToSettingsCallback forwardToSettingsCallback) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Boolean> emitter) throws Exception {
                PolicyPermissionUtil.checkPermission(fragment, permission, new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList,
                        List<String> deniedList) {
                        emitter.onNext(allGranted);
                        emitter.onComplete();
                    }
                }, forwardToSettingsCallback);
            }
        });
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(Fragment fragment,
        String permission) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                PolicyPermissionUtil.checkPermission(fragment, permission, new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList,
                        List<String> deniedList) {
                        emitter.onNext(allGranted);
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(FragmentActivity fragmentActivity,
        String permission, ForwardToSettingsCallback forwardToSettingsCallback) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                PolicyPermissionUtil.checkPermission(fragmentActivity, permission,
                    new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, List<String> grantedList,
                            List<String> deniedList) {
                            Timber.tag("welcomePresenter").d("checkPermissionObservable granted:%b",allGranted);
                            emitter.onNext(allGranted);
                            emitter.onComplete();
                        }
                    }, forwardToSettingsCallback);
            }
        });
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(FragmentActivity fragmentActivity,
        String permission) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                PolicyPermissionUtil.checkPermission(fragmentActivity, permission,
                    new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, List<String> grantedList,
                            List<String> deniedList) {
                            emitter.onNext(allGranted);
                            emitter.onComplete();
                        }
                    });
            }
        });
    }
}
