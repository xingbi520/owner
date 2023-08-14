package com.shendun.renter.activity;

import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.app.RenterApplication;
import com.shendun.renter.bean.StandardAddressInfo;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityRoomAddrBinding;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.RoomSourceResponse;
import com.shendun.renter.repository.bean.StandardAddressRequest;
import com.shendun.renter.utils.ScreenUtil;

/*
 * 标准地址显示页
 */
public class RoomAddrActivity extends BaseActivity<ActivityRoomAddrBinding>
    implements View.OnClickListener {
    private final static String TAG = RoomAddrActivity.class.getSimpleName();

    StandardAddressInfo mStandardAddressInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room_addr;
    }

    @Override
    protected void initEvent() {
        QMUIStatusBarHelper.translucent(this);

        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.room_address_detail);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 6));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));
        mBinding.btUpload.setOnClickListener(this);

        mStandardAddressInfo = RenterApplication.getContext().getStandardAddressInfo();
        RoomSourceResponse.DataDTO data = mStandardAddressInfo.getDataDTO();
        mBinding.ltId.tvTitle.setText(R.string.room_address_id);
        mBinding.ltId.tvContent.setText(data.getStDzysid());
        mBinding.ltXPosition.tvTitle.setText(R.string.room_x_position);
        mBinding.ltXPosition.tvContent.setText(data.getXzb());
        mBinding.ltYPosition.tvTitle.setText(R.string.room_y_position);
        mBinding.ltYPosition.tvContent.setText(data.getYzb());
        mBinding.ltAddressInfo.tvTitle.setText(R.string.room_address_info);
        mBinding.ltAddressInfo.tvContent.setText(data.getDzxx());
        mBinding.ltResponsibleCode.tvTitle.setText(R.string.room_responsible_code);
        mBinding.ltResponsibleCode.tvContent.setText(data.getZrqdm());
        mBinding.ltResponsibleName.tvTitle.setText(R.string.room_responsible_name);
        mBinding.ltResponsibleName.tvContent.setText(data.getZrqmc());
        mBinding.ltPoliceCode.tvTitle.setText(R.string.room_police_code);
        mBinding.ltPoliceCode.tvContent.setText(data.getPcsdm());
        mBinding.ltPoliceName.tvTitle.setText(R.string.room_police_name);
        mBinding.ltPoliceName.tvContent.setText(data.getPcsmc());
        mBinding.ltBranchCode.tvTitle.setText(R.string.room_branch_code);
        mBinding.ltBranchCode.tvContent.setText(data.getFjdm());
        mBinding.ltBranchName.tvTitle.setText(R.string.room_branch_name);
        mBinding.ltBranchName.tvContent.setText(data.getFjmc());
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.btUpload) {
            StandardAddressRequest request = new StandardAddressRequest();
            request.setDtype(mStandardAddressInfo.getDtype());
            request.setFhid(mStandardAddressInfo.getFhid());
            request.setSam(mStandardAddressInfo.getSam());
            request.setSt_dzysid(mStandardAddressInfo.getDataDTO().getStDzysid());
            request.setXzb(mStandardAddressInfo.getDataDTO().getXzb());
            request.setYzb(mStandardAddressInfo.getDataDTO().getYzb());
            request.setDzxx(mStandardAddressInfo.getDataDTO().getDzxx());
            request.setZrqdm(mStandardAddressInfo.getDataDTO().getZrqdm());
            request.setZrqmc(mStandardAddressInfo.getDataDTO().getZrqmc());
            request.setPcsdm(mStandardAddressInfo.getDataDTO().getPcsdm());
            request.setPcsmc(mStandardAddressInfo.getDataDTO().getPcsmc());
            request.setFjdm(mStandardAddressInfo.getDataDTO().getFjdm());
            request.setFjmc(mStandardAddressInfo.getDataDTO().getFjmc());
            request.setSam_base64(mStandardAddressInfo.getSam_base64());
            getRepository(NetService.class).uploadStandardAddress(UrlConfig.FD_FJXX, request.getRequestBody())
                    .compose(dispatchSchedulers(true))
                    .subscribe(new RepositorySubscriber<ResponseBean>() {
                        @Override
                        protected void onResponse(ResponseBean bean) {
                            try {
                                if (bean != null) {
                                    showCenterToast(bean.getMessage());
                                    if(bean.isSuccessful()){
                                        finish();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable t) {
                            super.onError(t);
                        }
                    });
        }
    }
}