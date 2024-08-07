package com.shendun.renter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.app.RenterApplication;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.bean.StandardAddressInfo;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityRoomDetailBinding;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.Room;
import com.shendun.renter.repository.bean.RoomRequest;
import com.shendun.renter.repository.bean.RoomResponse;
import com.shendun.renter.repository.bean.RoomSourceRequest;
import com.shendun.renter.repository.bean.RoomSourceResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.xqrcode.CustomCaptureActivity;
import com.xuexiang.xqrcode.XQRCode;

import java.util.List;

import Decoder.BASE64Decoder;

/*
 * 房间详情页
 */
public class RoomDetailActivity extends BaseActivity<ActivityRoomDetailBinding>
    implements View.OnClickListener {

    private static final int REQUEST_QRCODE = 1;

    private Room mRoom;
    private StandardAddressInfo mStandardAddressInfo;

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

        mStandardAddressInfo = new StandardAddressInfo();
        RenterApplication.getContext().setStandardAddressInfo(mStandardAddressInfo);
        mStandardAddressInfo.setDtype("U");

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

            mStandardAddressInfo.setFhid(mRoom.getFhid());
        }

        mBinding.llRoom.tvScan.setOnClickListener(this);
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
                                    BASE64Decoder decoder = new BASE64Decoder();
                                    byte[] houseNum = decoder.decodeBuffer(list.get(0).getMp_base64());
                                    if(houseNum != null && houseNum.length > 0){
                                        Bitmap pic = BitmapFactory.decodeByteArray(houseNum, 0, houseNum.length);
                                        mBinding.ivHouseNum.setImageBitmap(pic);
                                    }
                                    byte[] suSafe = decoder.decodeBuffer(list.get(0).getSam_base64());
                                    if(suSafe != null && suSafe.length > 0){
                                        Bitmap pic = BitmapFactory.decodeByteArray(suSafe, 0, suSafe.length);
                                        mBinding.ivSuSafe.setImageBitmap(pic);
                                    }
                                    mStandardAddressInfo.setSam_base64(list.get(0).getSam_base64());
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
        if (v == mBinding.llRoom.tvScan) {
            CustomCaptureActivity.start(this, REQUEST_QRCODE, R.style.XQRCodeTheme_Custom);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_QRCODE){
            handleScanResult(data);
        }
    }

    /**
     * 处理二维码扫描结果
     *
     * @param data
     */
    private void handleScanResult(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    String result = bundle.getString(XQRCode.RESULT_DATA);
                    LogUtils.d("Scan content:" + result);

                    String id = "";
                    final String strCode = "code=";
                    final String strStdzysid = "stdzysid=";
                    if(result.contains(strCode)){
                        int index = result.lastIndexOf(strCode) + strCode.length();
                        id = result.substring(index);
                    } else if(result.contains(strStdzysid)){
                        int index = result.lastIndexOf(strStdzysid) + strStdzysid.length();
                        id = result.substring(index);
                    } else {
                        id = result;
                    }
                    LogUtils.d("Scan id:" + id);
                    mStandardAddressInfo.setSam(id);
                    checkRoomSource(id);
                }
            }
        }
    }

    private void checkRoomSource(String id) {
        UserInfo userInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            return;
        }

        RoomSourceRequest request = new RoomSourceRequest();
        request.setStdzysid(id);
        getRepository(NetService.class).getRoomSource(UrlConfig.GET_DZXX, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<RoomSourceResponse>() {
                    @Override
                    protected void onResponse(RoomSourceResponse bean) {
                        if (bean != null && bean.getCode().equals(Constants.RESPONSE_SUCCEED)) {
                            RoomSourceResponse.DataDTO data = bean.getData();
                            if(data != null){
                                mStandardAddressInfo.setDataDTO(data);
                                Intent intent  = new Intent(RoomDetailActivity.this, RoomAddrActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            showCenterToast(bean.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                    }
                });
    }
}