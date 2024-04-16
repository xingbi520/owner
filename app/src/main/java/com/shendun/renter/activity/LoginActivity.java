package com.shendun.renter.activity;

import static com.shendun.renter.config.SpConfig.SP_AUTO_LOGIN;
import static com.shendun.renter.config.SpConfig.SP_PASSWORD;
import static com.shendun.renter.config.SpConfig.SP_PHONE;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.renter.R;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityNewLoginBinding;
import com.shendun.renter.dialog.NormalDialog;
import com.shendun.renter.dialog.animation.FadeEnter.FadeEnter;
import com.shendun.renter.dialog.animation.ZoomExit.ZoomOutExit;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.GetPwdRequest;
import com.shendun.renter.repository.bean.GetPwdResponse;
import com.shendun.renter.repository.bean.LoginRequest;
import com.shendun.renter.repository.bean.LoginResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

/*
 * 登录页
 */
@RuntimePermissions
public class LoginActivity extends BaseActivity<ActivityNewLoginBinding>
    implements View.OnClickListener {
    private final static String TAG = LoginActivity.class.getSimpleName();

    /**
     * 验证码倒计时初始时间为60
     */
    private static final int SMS_CODE_INIT_SECOND = 60;

    private boolean mIsAutoLoginChecked = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_login;
    }

    @Override
    protected void initEvent() {
        QMUIStatusBarHelper.translucent(this);

        mBinding.ivLoginDel.setOnClickListener(this);
        mBinding.btGetVerifyCode.setOnClickListener(this);
        mBinding.autoLogin.setOnClickListener(this);
        mBinding.btLoginSubmit.setOnClickListener(this);

        LoginActivityPermissionsDispatcher.readPhoneStateWithPermissionCheck(
                LoginActivity.this);

        autoLogin();
    }

    private void autoLogin(){
        mIsAutoLoginChecked = DataHelper.getBooleanSF(mContext, SP_AUTO_LOGIN, false);
        if(mIsAutoLoginChecked){
            mBinding.checkboxAutoLogin.setBackgroundResource(R.mipmap.ic_check_box_on);
            String phone = DataHelper.getStringSF(mContext, SP_PHONE);
            String pwd = DataHelper.getStringSF(mContext, SP_PASSWORD);
            mBinding.etLoginPhone.setText(phone);
            mBinding.etLoginCode.setText(pwd);
            login(phone,pwd);
        } else {
            mBinding.checkboxAutoLogin.setBackgroundResource(R.mipmap.ic_check_box_off);
        }
    }

    /**
     * 获取输入框内容
     */
    private String getEditTextContent(EditText editText) {
        return null == editText || null == editText.getText() ? "" : editText.getText().toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final NormalDialog dialog = new NormalDialog(mContext);
            dialog.setCanceledOnTouchOutside(false);
            dialog.isContentShow(true)
                    .titleTextSize(50f)
                    .content("确定退出APP吗")
                    .contentTextSize(50f)
                    .style(NormalDialog.STYLE_TWO)//
                    .showAnim(new FadeEnter())
                    .btnNum(2)
                    .btnTextSize(45f,45f)
                    .btnTextColor(Color.parseColor("#333333"),
                            Color.parseColor("#80dad1"))
                    .dismissAnim(new ZoomOutExit())//
                    .show();

            dialog.setOnBtnClickL(() -> dialog.dismiss(), () -> {
                dialog.dismiss();
                finish();
            });
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.ivLoginDel) {
            mBinding.etLoginPhone.getText().clear();
        } else if (v == mBinding.btGetVerifyCode) {
            final String phone = mBinding.etLoginPhone.getText().toString();
            final String unicode = DeviceUtils.getUniqueDeviceId();
            LogUtils.dTag(TAG, "unicode:" + unicode);
            if(RegexUtils.isMobileSimple(phone)){
                GetPwdRequest getPwdRequest = new GetPwdRequest();
                getPwdRequest.setTel(phone);
                getPwdRequest.setImei(unicode);
                getPwdRequest.setZclx("2");
                getRepository(NetService.class).getPwd(UrlConfig.FD_REG, getPwdRequest.getRequestBody())
                        .compose(dispatchSchedulers(false))
                        .subscribe(new RepositorySubscriber<GetPwdResponse>() {
                            @Override
                            protected void onResponse(GetPwdResponse result) {
                                if ("0".equals(result.getCode())) {
                                    String account = result.getData().getUsercode();
                                    String pwd = result.getData().getPassword();
                                    if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)){
                                        showResult(account, pwd);
                                    }
                                } else {
                                    showCenterToast(result.getMessage());
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable t) {
                                super.onError(t);
                                showCenterToast(getString(R.string.network_failed));
                            }
                        });
            } else {
                showCenterToast(getString(R.string.invalid_phone_number));
            }

        } else if (v == mBinding.btLoginSubmit) {
            final String phone = mBinding.etLoginPhone.getText().toString();
            final String pwd = mBinding.etLoginCode.getText().toString();
            if(!RegexUtils.isMobileSimple(phone)){
                showCenterToast(getString(R.string.invalid_phone_number));
                return;
            }

//            if(AppUtils.isAppDebug()){
//                DataHelper.setStringSF(mContext, SpConfig.KEY_PHONE_NUMBER, phone);
//                launchMain();
//                return;
//            }

            if(TextUtils.isEmpty(pwd)){
                showCenterToast(getString(R.string.invalid_pwd_code));
                return;
            }

            login(phone, pwd);
        } else if(v == mBinding.autoLogin){
            if(mIsAutoLoginChecked){
                mIsAutoLoginChecked = false;
                mBinding.checkboxAutoLogin.setBackgroundResource(R.mipmap.ic_check_box_off);
            }else{
                mIsAutoLoginChecked = true;
                mBinding.checkboxAutoLogin.setBackgroundResource(R.mipmap.ic_check_box_on);
            }
            DataHelper.setBooleanSF(mContext, SP_AUTO_LOGIN, mIsAutoLoginChecked);
        }
    }

    /**
     * 登录
     */
    private void login(String phone, String pwd) {
        if(null == phone || TextUtils.isEmpty(phone)
        || null == pwd || TextUtils.isEmpty(pwd)){
            return;
        }

//        String unicode = AppUtils.isAppDebug() ? "HUAWEI_HWOCE-ML_e1274a1f-ea8f-43ca-8e1f-a2a59df08958-194bbc13" : DeviceUtils.getUniqueDeviceId();
        String unicode = AppUtils.isAppDebug() ? "29be82a6786e53afb81049eac473766ad" : DeviceUtils.getUniqueDeviceId();
        LogUtils.dTag(TAG, "unicode:" + unicode);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setTel(phone);
        loginRequest.setPassword(pwd);
        loginRequest.setUnicode(unicode);
        getRepository(NetService.class).appLogin(UrlConfig.FD_LOGIN, loginRequest.getRequestBody())
                .compose(dispatchSchedulers(true))
                .subscribe(new RepositorySubscriber<LoginResponse>() {
                    @Override
                    protected void onResponse(LoginResponse result) {
                        try {
                            if (result.getCode().equals("0")) {
                                DataHelper.setStringSF(mContext, SP_PHONE, phone);
                                DataHelper.setStringSF(mContext, SP_PASSWORD, pwd);

                                UserInfo user = result.getData();
                                loginSucceed(user);
                            } else {
                                String message = result.getMessage();
                                showCenterToast(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        showCenterToast(getString(R.string.network_failed));
                    }
                });
    }

    /**
     * 登录成功
     */
    private void loginSucceed(UserInfo userInfo) {
        CacheManager.saveToJson(getApplicationContext(), ConstantConfig.CACHE_NAME_USER_INFO + ".json", userInfo);

        launchMain();
    }

    /**
     * 进入主界面
     */
    private void launchMain() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    private void showResult(final String account, final String pwd){
        String result = "账户:" + account + " 密码:" + pwd;
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.isContentShow(true)
                .title("返回密码提示")
                .titleTextSize(50f)
                .content(result)
                .contentTextSize(50f)
                .style(NormalDialog.STYLE_TWO)//
                .showAnim(new FadeEnter())
                .btnNum(1)
                .btnTextSize(45f,45f)
                .btnTextColor(Color.parseColor("#333333"),
                        Color.parseColor("#008aff"))
                .dismissAnim(new ZoomOutExit())//
                .show();

        dialog.setOnBtnClickL(() -> dialog.dismiss());
    }

    /**
     * 开启验证码倒计时
     */
    public void startSmsTimer() {
        int time = SMS_CODE_INIT_SECOND;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Timber.e("发送验证码初始时间为%d秒", time);
                        mBinding.btGetVerifyCode.setText(
                                String.format("%ds", time));
                        mBinding.btGetVerifyCode.setEnabled(false);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Timber.e("恢复发送验证码按钮状态");
                        mBinding.btGetVerifyCode.setText(R.string.get_ver_code);
                        mBinding.btGetVerifyCode.setEnabled(true);
                    }
                })
                .take(time + 1)
                .compose(bindToLifecycle())
                .subscribe(new RepositorySubscriber<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        addDispose(d);
                    }

                    @Override
                    protected void onResponse(Long result) {
                        int lastSecond = time - result.intValue();
                        Timber.e("验证码倒计时剩余%d秒", lastSecond);
                        if (lastSecond == 0) {
                            unDispose();
                        } else {
                            mBinding.btGetVerifyCode.setText(
                                    String.format("%ds",
                                            lastSecond));
                            mBinding.btGetVerifyCode.setEnabled(false);
                        }
                    }
                });
    }

    @NeedsPermission({ Manifest.permission.READ_PHONE_STATE})
    void readPhoneState() {}

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE})
    void showRationaleStorage(final PermissionRequest request) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.isContentShow(true)
                .content(getString(R.string.cloud_rationale_message))
                .contentTextColor(getResources().getColor(R.color.cloud_normal_dark))
                .style(NormalDialog.STYLE_TWO)//
                .showAnim(new FadeEnter())
                .btnNum(2)
                .btnText(getString(R.string.cloud_cancel),
                        getString(R.string.cloud_ok))
                .btnTextColor(getResources().getColor(R.color.cloud_light_dark),
                        getResources().getColor(R.color.cloud_theme))
                .dismissAnim(new ZoomOutExit())//
                .show();
        dialog.setOnBtnClickL(() -> {
            request.cancel();
            dialog.dismiss();
        }, () -> {
            request.proceed();
            dialog.dismiss();
        });
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE})
    void permissionsDeniedStorage() {
        showCenterToast(getString(R.string.cloud_phone_state_denied_msg));
        Timber.tag(TAG).d("permissionsDeniedPhoneState denied");
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE})
    void nerverAskAgainCameraAndStorage() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.isContentShow(true)
                .content(getString(R.string.cloud_never_sak_message))
                .contentTextColor(getResources().getColor(R.color.cloud_normal_dark))
                .style(NormalDialog.STYLE_TWO)//
                .showAnim(new FadeEnter())
                .btnNum(2)
                .btnText(getString(R.string.cloud_cancel),
                        getString(R.string.cloud_ok))
                .btnTextColor(getResources().getColor(R.color.cloud_light_dark),
                        getResources().getColor(R.color.cloud_theme))
                .dismissAnim(new ZoomOutExit())//
                .show();
        dialog.setOnBtnClickL(() -> {
            dialog.dismiss();
        }, () -> {
            //引导用户至设置页手动授权
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            dialog.dismiss();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }
}