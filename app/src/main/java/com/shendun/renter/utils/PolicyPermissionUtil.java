package com.shendun.renter.utils;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.PermissionBuilder;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.renter.R;

import com.shendun.renter.widget.PermissionsNotifyDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * Author:daiminzhi
 * Time: 11:56 上午
 */
public class PolicyPermissionUtil {

    private static final String TAG = "PolicyPermissionUtil";

    private static final String SMARTONT_PERMISSION_FIRST_STORAGE =
        "smartont_permission_first_storage";
    private static final String SMARTONT_PERMISSION_FIRST_CONTACTS =
        "smartont_permission_first_contacts";
    private static final String SMARTONT_PERMISSION_FIRST_CAMERA =
        "smartont_permission_first_camera";
    private static final String SMARTONT_PERMISSION_FIRST_RECORD_AUDIO =
        "smartont_permission_first_record_audio";
    private static final String SMARTONT_PERMISSION_FIRST_LOCATION =
        "smartont_permission_first_location";
    private static final String SMARTONT_PERMISSION_FIRST_SYSTEM_ALERT_WINDOW =
        "smartont_permission_first_system_alert_window";

    public static void checkPermission(Fragment fragment, String permission,
        RequestCallback requestCallback) {
        checkPermission(fragment, permission, requestCallback, null);
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static void checkPermission(Fragment fragment, String permission,
        RequestCallback requestCallback, ForwardToSettingsCallback forwardToSettingsCallback) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragment).permissions(permission);
        checkAndShowPolicyNotifyDialog(fragment.getContext(), permissionBuilder, permission, () -> {
            requestCallback.onResult(false, Collections.emptyList(), Arrays.asList(permission));
        });
        if (forwardToSettingsCallback != null) {
            permissionBuilder.onForwardToSettings(forwardToSettingsCallback);
        }
        permissionBuilder.request(requestCallback);
    }

    public static Observable<Boolean> checkPermissionObservable(Fragment fragment,
        String permission) {
        return checkPermissionObservable(fragment, permission, null);
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(Fragment fragment,
        String permission, ForwardToSettingsCallback forwardToSettingsCallback) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                PermissionBuilder permissionBuilder =
                    PermissionX.init(fragment).permissions(permission);
                checkAndShowPolicyNotifyDialog(fragment.getContext(), permissionBuilder, permission,
                    () -> {
                        emitter.onNext(false);
                        emitter.onComplete();
                    });
                if (forwardToSettingsCallback != null) {
                    permissionBuilder.onForwardToSettings(forwardToSettingsCallback);
                }
                permissionBuilder.request(new RequestCallback() {
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

    public static void checkPermission(FragmentActivity fragmentActivity, String permission,
        RequestCallback requestCallback) {
        checkPermission(fragmentActivity, permission, requestCallback, null);
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static void checkPermission(FragmentActivity fragmentActivity, String permission,
        RequestCallback requestCallback, ForwardToSettingsCallback forwardToSettingsCallback) {
        PermissionBuilder permissionBuilder =
            PermissionX.init(fragmentActivity).permissions(permission);
        checkAndShowPolicyNotifyDialog(fragmentActivity, permissionBuilder, permission, () -> {
            requestCallback.onResult(false, Collections.emptyList(), Arrays.asList(permission));
        });
        if (forwardToSettingsCallback != null) {
            permissionBuilder.onForwardToSettings(forwardToSettingsCallback);
        }
        permissionBuilder.request(requestCallback);
    }

    public static Observable<Boolean> checkPermissionObservable(FragmentActivity fragmentActivity,
        String permission) {
        return checkPermissionObservable(fragmentActivity, permission, null);
    }

    /**
     * 请求用户权限，首次需要提示隐私用途弹窗
     */
    public static Observable<Boolean> checkPermissionObservable(FragmentActivity fragmentActivity,
        String permission, ForwardToSettingsCallback forwardToSettingsCallback) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Boolean> emitter) throws Exception {
                PermissionBuilder permissionBuilder =
                    PermissionX.init(fragmentActivity).permissions(permission);
                checkAndShowPolicyNotifyDialog(fragmentActivity, permissionBuilder, permission,
                    () -> {
                        emitter.onNext(false);
                        emitter.onComplete();
                    });
                if (forwardToSettingsCallback != null) {
                    permissionBuilder.onForwardToSettings(forwardToSettingsCallback);
                }
                permissionBuilder.request(new RequestCallback() {
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

    private static void checkAndShowPolicyNotifyDialog(Context context,
        PermissionBuilder permissionBuilder, String permission,
        FirstDeniedPermissionCallback firstDeniedPermissionCallback) {
        Timber.tag(TAG).d("checkAndShowPolicyNotifyDialog permission:%s", permission);
        String spKey = null;
        int iconResId = -1;
        String permissionTitle = "";
        List<String> permissionContentList = new ArrayList<>();
        Resources resources = context.getResources();
        //存储权限
        if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)
            || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
            spKey = SMARTONT_PERMISSION_FIRST_STORAGE;
            iconResId = R.drawable.ic_permission_storage;
            permissionTitle = resources.getString(R.string.policy_permission_storage);
            permissionContentList = Arrays.asList(
                resources.getStringArray(R.array.policy_permission_storage_content_list));
        }
        //通讯录权限
        else if (Manifest.permission.READ_CONTACTS.equals(permission)
            || Manifest.permission.WRITE_CONTACTS.equals(permission)) {
            spKey = SMARTONT_PERMISSION_FIRST_CONTACTS;
            iconResId = R.drawable.ic_permission_contact;
            permissionTitle = resources.getString(R.string.policy_permission_contacts);
            permissionContentList = Arrays.asList(
                resources.getStringArray(R.array.policy_permission_contacts_content_list));
        }
        //定位权限
        else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)
            || Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
            spKey = SMARTONT_PERMISSION_FIRST_LOCATION;
            iconResId = R.drawable.ic_permission_loc;
            permissionTitle = resources.getString(R.string.policy_permission_location);
            permissionContentList = Arrays.asList(
                resources.getStringArray(R.array.policy_permission_location_content_list));
        }
        //录音权限
        else if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            spKey = SMARTONT_PERMISSION_FIRST_RECORD_AUDIO;
            iconResId = R.drawable.ic_permission_record_audio;
            permissionTitle = resources.getString(R.string.policy_permission_record_audio);
            permissionContentList = Arrays.asList(
                resources.getStringArray(R.array.policy_permission_record_audio_content_list));
        }
        //相机权限
        else if (Manifest.permission.CAMERA.equals(permission)) {
            spKey = SMARTONT_PERMISSION_FIRST_CAMERA;
            iconResId = R.drawable.ic_permission_camera;
            permissionTitle = resources.getString(R.string.policy_permission_camera);
            permissionContentList = Arrays.asList(
                resources.getStringArray(R.array.policy_permission_camera_content_list));
        }
        Timber.tag(TAG).d("checkAndShowPolicyNotifyDialog spKey:%s", spKey);
        if (spKey == null) {
            return;
        }
        boolean firstPermission = DataHelper.getBooleanSF(context, spKey, false);
        if (!firstPermission) {
            Timber.tag(TAG).d("checkAndShowPolicyNotifyDialog spKey:%s value is false", spKey);
            DataHelper.setBooleanSF(context, spKey, true);
            int finalIconResId = iconResId;
            String finalPermissionTitle = permissionTitle;
            List<String> finalPermissionContentList = permissionContentList;
            permissionBuilder.explainReasonBeforeRequest()
                .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList,
                        boolean beforeRequest) {
                        Timber.tag(TAG)
                            .d("checkAndShowPolicyNotifyDialog onExplainReason  deniedList:%s;beforeRequest:%b",
                                deniedList.toString(), beforeRequest);
                        if (beforeRequest) {
                            PermissionsNotifyDialog permissionsNotifyDialog =
                                new PermissionsNotifyDialog(context, permission, finalIconResId,
                                    finalPermissionTitle, finalPermissionContentList);
                            scope.showRequestReasonDialog(permissionsNotifyDialog);
                        } else {
                            firstDeniedPermissionCallback.denied();
                        }
                    }
                });
        }
    }

    /**
     * 只用于悬浮窗权限检查首次提醒弹窗
     *
     * @param context 上下文
     * @param callback 回调
     */
    public static void checkShowSystemAlertWindowPolicyNotifyDialog(Context context,
        SystemAlertWindowFirstDeniedPermissionCallback callback) {
        Timber.tag(TAG).d("checkShowSystemAlertWindowPolicyNotifyDialog");
        String spKey = SMARTONT_PERMISSION_FIRST_SYSTEM_ALERT_WINDOW;
        int iconResId = R.drawable.ic_permission_system_alert_window;
        Resources resources = context.getResources();
        String permissionTitle =
            resources.getString(R.string.policy_permission_system_alert_window);
        List<String> permissionContentList = Arrays.asList(
            resources.getStringArray(R.array.policy_permission_system_alert_window_content_list));
        boolean firstPermission = DataHelper.getBooleanSF(context, spKey, false);
        if (!firstPermission) {
            Timber.tag(TAG).d("checkAndShowPolicyNotifyDialog spKey:%s value is false", spKey);
            DataHelper.setBooleanSF(context, spKey, true);
            PermissionsNotifyDialog permissionsNotifyDialog =
                new PermissionsNotifyDialog(context, Manifest.permission.SYSTEM_ALERT_WINDOW,
                    iconResId, permissionTitle, permissionContentList);
            permissionsNotifyDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.callback();
                    }
                }
            });
            permissionsNotifyDialog.show();
        } else {
            callback.callback();
        }
    }

    /**
     * 只用于摄像头权限检查首次提醒弹窗
     *
     * @param context 上下文
     * @param callback 回调
     */
    public static void checkShowCameraPolicyNotifyDialog(Context context,
        SystemAlertWindowFirstDeniedPermissionCallback callback) {
        Timber.tag(TAG).d("checkShowCameraPolicyNotifyDialog");
        String spKey = SMARTONT_PERMISSION_FIRST_CAMERA;
        int iconResId = R.drawable.ic_permission_camera;
        Resources resources = context.getResources();
        String permissionTitle =
            resources.getString(R.string.policy_permission_camera);
        List<String> permissionContentList = Arrays.asList(
            resources.getStringArray(R.array.policy_permission_camera_content_list));
        boolean firstPermission = DataHelper.getBooleanSF(context, spKey, false);
        if (!firstPermission) {
            Timber.tag(TAG).d("checkAndShowPolicyNotifyDialog spKey:%s value is false", spKey);
            DataHelper.setBooleanSF(context, spKey, true);
            PermissionsNotifyDialog permissionsNotifyDialog =
                new PermissionsNotifyDialog(context, Manifest.permission.CAMERA,
                    iconResId, permissionTitle, permissionContentList);
            permissionsNotifyDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.callback();
                    }
                }
            });
            permissionsNotifyDialog.show();
        } else {
            callback.callback();
        }
    }

    interface FirstDeniedPermissionCallback {
        void denied();
    }

    public interface SystemAlertWindowFirstDeniedPermissionCallback {
        void callback();
    }
}
