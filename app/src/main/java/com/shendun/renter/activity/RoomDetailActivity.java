package com.shendun.renter.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityOrderDetailBinding;
import com.shendun.renter.databinding.ActivityRoomDetailBinding;
import com.shendun.renter.fragment.adapter.PersonAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.GetLockPwd;
import com.shendun.renter.repository.bean.GetLockPwdResponse;
import com.shendun.renter.repository.bean.OrderDetailRequest;
import com.shendun.renter.repository.bean.OrderDetailResponse;
import com.shendun.renter.repository.bean.OrderDetailResponse.ListBean;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.Room;
import com.shendun.renter.repository.bean.RoomRequest;
import com.shendun.renter.repository.bean.RoomResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.widget.layoutmanager.LinearLayoutManagerWrap;

import java.util.List;

/*
 * 订单详情页
 */
public class RoomDetailActivity extends BaseActivity<ActivityRoomDetailBinding>
    implements View.OnClickListener {

    private Room mRoom;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room_detail;
    }

    @Override
    protected void initEvent() {
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    private void initData(){
        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.room_detail);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 6));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));

        mRoom = (Room)getIntent().getSerializableExtra(SpConfig.KEY_ROOM_DETAIL);
        if(null != mRoom){
            mBinding.llRoom.tvRoomNo.setText("房号:" + mRoom.getFh());
            mBinding.llRoom.tvRoomFloor.setText("楼层:" + mRoom.getFjlc());
            mBinding.llRoom.tvRoomStyle.setText("房型:" + mRoom.getFjlx());
            mBinding.llRoom.tvRoomAddress.setText(mRoom.getFwmc() + mRoom.getDh() + "栋");

            String roomStatus = mRoom.getZt();
            if(!TextUtils.isEmpty(roomStatus)){
                if(Constants.ROOM_ORDER_FREE.equals(roomStatus)){
                    mBinding.llRoom.ivImage.setImageResource(R.mipmap.room_kx);
                    mBinding.llRoom.ivDot.setImageResource(R.mipmap.ic_checkin_free);
                    mBinding.llRoom.tvStatus.setText(R.string.room_free);
                    mBinding.llRoom.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_C9C9C9));
                    mBinding.llRoom.tvCheckIn.setVisibility(View.VISIBLE);
                } else if(Constants.ROOM_ORDER_READY.equals(roomStatus)){
                    mBinding.llRoom.ivImage.setImageResource(R.mipmap.room_ready);
                    mBinding.llRoom.ivDot.setImageResource(R.mipmap.ic_checkin_ready);
                    mBinding.llRoom.tvStatus.setText(R.string.room_ready);
                    mBinding.llRoom.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_47CEFC));
                    mBinding.llRoom.tvCheckIn.setVisibility(View.GONE);
                } else if(Constants.ROOM_ORDER_CHECK_IN.equals(roomStatus)){
                    mBinding.llRoom.ivImage.setImageResource(R.mipmap.room_in);
                    mBinding.llRoom.ivDot.setImageResource(R.mipmap.ic_checkin_in);
                    mBinding.llRoom.tvStatus.setText(R.string.room_in);
                    mBinding.llRoom.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_F47F72));
                    mBinding.llRoom.tvCheckIn.setVisibility(View.GONE);
                }
            }
        }

        mBinding.llRoom.tvCheckIn.setVisibility(View.GONE);
        mBinding.llRoom.tvCheckIn.setOnClickListener(this);
    }

    private void loadData() {
        UserInfo userInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            return;
        }

        RoomRequest request = new RoomRequest();
        request.setCybh(userInfo.getCybh());
        request.setDwbh(userInfo.getDwbh());
        request.setZt((null != mRoom) ? mRoom.getZt() : "");
        request.setXqdm("");
        request.setS_val("");
        request.setFjlx("");
        request.setFhid((null != mRoom) ? mRoom.getFhid() : "");
        request.setCurrpage("1");
        request.setPagesize(String.valueOf(Constants.ROOM_ORDER_PAGE_SIZE));
        getRepository(NetService.class).getRooms(UrlConfig.FD_SEARCH_FH, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<RoomResponse>() {
                    @Override
                    protected void onResponse(RoomResponse bean) {
                        try {
                            if (bean != null && bean.getCode().equals(Constants.RESPONSE_SUCCEED)) {
                                RoomResponse.DataBean pageBean = bean.getData();
                                List<Room> list = pageBean.getList();
                                if (!list.isEmpty()) {
                                    mBinding.tvHouseNo.setText(list.get(0).getMpsn());
                                    mBinding.tvHealthCode.setText(list.get(0).getSam());
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

    @Override
    public void onClick(View v) {
        if (v == mBinding.llRoom.tvCheckIn) {

        }
    }
}