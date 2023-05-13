package com.shendun.renter.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.RegexUtils;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.renter.R;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.databinding.ActivityUserAuthBinding;
import com.shendun.renter.utils.PermissionDialogUtil;
import com.shendun.renter.utils.ScreenUtil;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/*
 * 用户实名认证页
 */
@RuntimePermissions
public class UserAuthActivity extends BaseActivity<ActivityUserAuthBinding>
    implements View.OnClickListener {

    private String mPhone;   //主房客手机号
    private String mSubPhone;//新增房客手机号
    private String mOrder;  //订单号
    private String mName;   //姓名
    private String mId;     //身份证号码
    private String mOperateStyle; //从哪里跳转该页面
    private String mStyle;  //租客类型，1表示主客，2表示同行客

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_auth;
    }

    @Override
    protected void initEvent() {
        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.user_auth);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 6));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));

        mBinding.btNext.setOnClickListener(this);

        mPhone = DataHelper.getStringSF(mContext, SpConfig.KEY_PHONE_NUMBER);
        mSubPhone = "";
        mOrder = getIntent().getStringExtra(SpConfig.KEY_ORDER_NUMBER);
        mOperateStyle = getIntent().getStringExtra(ParamConfig.PARAM_OPERATE_STYLE);
        if(!TextUtils.isEmpty(mOperateStyle) && ConstantConfig.ROOM_ADD_AUTH_IN.equals(mOperateStyle)){
            mBinding.llLoginPhone.setVisibility(View.VISIBLE);
            mStyle = "2";
        } else {
            mBinding.llLoginPhone.setVisibility(View.GONE);
            mStyle = "1";
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btNext) {
            final String name = mBinding.etName.getText().toString();
            final String id = mBinding.etId.getText().toString();
            final String phone = mBinding.etPhone.getText().toString();
            if(TextUtils.isEmpty(name)) {
                showCenterToast("请输入姓名");
                return;
            }
            if(TextUtils.isEmpty(id) || !RegexUtils.isIDCard18Exact(id)) {
                showCenterToast("请输入正确的身份证号码");
                return;
            }

            if(mBinding.llLoginPhone.getVisibility() == View.VISIBLE){
                if(TextUtils.isEmpty(phone) || !RegexUtils.isMobileSimple(phone)) {
                    showCenterToast("请输入正确的手机号码");
                    return;
                } else {
                    mSubPhone = phone;
                }
            }

            mName = name;
            mId = id;

            UserAuthActivityPermissionsDispatcher.startLivenessWithPermissionCheck(
                    UserAuthActivity.this);
        }
    }

    @NeedsPermission({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void startLiveness() {
        Bundle bundle = new Bundle();
        bundle.putString(SpConfig.KEY_PHONE_NUMBER, mPhone);
        bundle.putString(SpConfig.KEY_SUB_PHONE_NUMBER, mSubPhone);
        bundle.putString(SpConfig.KEY_ORDER_NUMBER, mOrder);
        bundle.putString(SpConfig.KEY_NAME, mName);
        bundle.putString(SpConfig.KEY_ID_CARD, mId);
        bundle.putString(SpConfig.KEY_CLIENT_STYLE, mStyle);
        Intent intent = new Intent(mContext, FaceAuthActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    //提示用户为何需要开启此权限。在用户选择拒绝后，再次需要访问该权限时调用
    @OnShowRationale({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void showRationale(final PermissionRequest request) {
        PermissionDialogUtil.showDialog(mContext, R.string.tip, R.string.rationale_message,
                R.string.auth, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                        dialog.dismiss();
                    }
                });
    }

    //用户选择拒绝时的提示
    @OnPermissionDenied({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void permissionsDenied() {
        showMessage(getString(R.string.livenesslib_need_camera_and_external_storage));
    }

    //用户选择不再询问后的提示
    @OnNeverAskAgain({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void nerverAskAgain() {
        PermissionDialogUtil.showDialog(mContext, R.string.permission_tip,
                R.string.never_sak_message, R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UserAuthActivityPermissionsDispatcher.onRequestPermissionsResult(
                UserAuthActivity.this, requestCode, grantResults);
    }
}