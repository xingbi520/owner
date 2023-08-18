package com.shendun.renter.fragment;

import static com.shendun.renter.config.SpConfig.SP_AUTO_LOGIN;
import static com.shendun.renter.config.SpConfig.SP_PASSWORD;
import static com.shendun.renter.config.SpConfig.SP_PHONE;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.shendun.architecture.base.BaseFragment;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.architecture.utils.DeviceUtils;
import com.shendun.renter.R;
import com.shendun.renter.activity.LoginActivity;
import com.shendun.renter.activity.MainActivity;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.databinding.FragmentMeBinding;
import com.shendun.renter.event.TabJumpEvent;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;

import org.greenrobot.eventbus.EventBus;

public class MeFragment extends BaseFragment<FragmentMeBinding> implements View.OnClickListener{

    private boolean isCompanyShow = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initEvent() {
        UserInfo userInfo = CacheManager.readFromJson(mContext, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null != userInfo){
            mBinding.tvNickName.setText(userInfo.getUsername());
            mBinding.tvCybh.setText(userInfo.getCybh());
            mBinding.tvCompanyName.setText("单位名称:" + userInfo.getFr());
            mBinding.tvCompanyCode.setText("单位编号:" + userInfo.getFr());
            mBinding.tvCompanyAddress.setText("单位地址:" + userInfo.getFr());
            mBinding.tvFrName.setText("法人姓名:" + userInfo.getFr());
            mBinding.tvFrSfz.setText("身份证号:" + userInfo.getFrsfz());
            mBinding.tvFrPhone.setText("法人电话:" + userInfo.getFrlxdh());
            mBinding.tvFrYyzz.setText("信用代码:" + userInfo.getYyzz());
        }

        mBinding.tvVersion.setText("版本:" + DeviceUtils.getVersionName(mContext));

        mBinding.clCompanyInfo.setOnClickListener(this);
        mBinding.clOrder.setOnClickListener(this);
        mBinding.clQuit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.clCompanyInfo) {
            if(!isCompanyShow) {
                isCompanyShow = true;
            } else {
                isCompanyShow = false;
            }
            mBinding.clCompany.setVisibility(isCompanyShow ? View.VISIBLE : View.GONE);
        } else if(view == mBinding.clOrder){
            EventBus.getDefault().post(new TabJumpEvent(MainActivity.MAIN_POSITION, false));
        } else if(view == mBinding.clQuit){
            logOut();
            startActivity(new Intent(mContext, LoginActivity.class));
            MainActivity mainActivity = (MainActivity) _mActivity;
            mainActivity.finish();
        }
    }

    private void logOut(){
        DataHelper.removeSF(mContext, SP_PHONE);
        DataHelper.removeSF(mContext, SP_PASSWORD);
        DataHelper.removeSF(mContext, SP_AUTO_LOGIN);
    }
}
