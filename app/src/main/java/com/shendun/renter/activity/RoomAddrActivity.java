package com.shendun.renter.activity;

import android.view.View;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.renter.R;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.databinding.ActivityRoomAddrBinding;
import com.shendun.renter.repository.bean.RoomSourceResponse;
import com.shendun.renter.utils.ScreenUtil;

/*
 * 标准地址显示页
 */
public class RoomAddrActivity extends BaseActivity<ActivityRoomAddrBinding> {
    private final static String TAG = RoomAddrActivity.class.getSimpleName();


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

        RoomSourceResponse.DataDTO data = (RoomSourceResponse.DataDTO) getIntent().getSerializableExtra(ParamConfig.PARAM_EXTRA);
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
}