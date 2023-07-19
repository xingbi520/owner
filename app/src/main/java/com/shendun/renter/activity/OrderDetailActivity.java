package com.shendun.renter.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orient.me.widget.sw.MultiSwitchListener;
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
import com.shendun.renter.fragment.adapter.PersonAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.GetLockPwd;
import com.shendun.renter.repository.bean.GetLockPwdResponse;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.UploadLockBtPwd;
import com.shendun.renter.repository.bean.OrderDetailRequest;
import com.shendun.renter.repository.bean.OrderDetailResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.widget.layoutmanager.LinearLayoutManagerWrap;
import com.shendun.renter.repository.bean.OrderDetailResponse.ListBean;

import java.util.List;

import timber.log.Timber;

/*
 * 订单详情页
 */
public class OrderDetailActivity extends BaseActivity<ActivityOrderDetailBinding>
    implements View.OnClickListener {
    private final String TAG = OrderDetailActivity.class.getSimpleName();

    private String mPhone;
    private String mOrder;
    private String mOrderStatus;
    private UserInfo mUserInfo;

    private OrderDetailResponse mOrderDetailResponse;

    private PersonAdapter mPersonAdapter;

    private static final String GET_PWD = "1";
    private static final String UPLOAD_PWD = "2";
    private static final String SET_PWD = "1";    //密码设置
    private static final String SET_BT = "2";     //蓝牙功能是否开启设置

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initEvent() {
        initData();
        initRecyclerView();
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
        mBinding.topbar.getCenterTextView().setText(R.string.order_detail);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 6));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));
        mBinding.llOrder.ivDot.setVisibility(View.GONE);
        mBinding.llOrder.tvStatus.setVisibility(View.GONE);
        mBinding.btResetPwd.setOnClickListener(this);
        mBinding.btResetShendunPwd.setOnClickListener(this);
        mBinding.btModify.setOnClickListener(this);
        mBinding.btCohabitant.setOnClickListener(this);
        mBinding.scBt.setOnClickListener(this);

        mPhone = DataHelper.getStringSF(mContext, SpConfig.KEY_PHONE_NUMBER);
        mOrder = getIntent().getStringExtra(SpConfig.KEY_ORDER_NUMBER);
        mOrderStatus = getIntent().getStringExtra(SpConfig.KEY_ORDER_STATUS);

        mUserInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
    }

    private void initRecyclerView() {
        mPersonAdapter = new PersonAdapter();
        mBinding.rv.setAdapter(mPersonAdapter);
        mPersonAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ListBean item = mPersonAdapter.getData().get(position);
                //人员类型1主客2同行客
                String type = item.getZklx();
                if("1".equals(type)){
                    JumpToEditInActivity(false);
                } else {
                    JumpToCohaInActivity(false);
                }
            }
        });
        LinearLayoutManagerWrap linearLayoutManagerWrap = new LinearLayoutManagerWrap(mContext);
        linearLayoutManagerWrap.setOrientation(RecyclerView.VERTICAL);
        mBinding.rv.setLayoutManager(linearLayoutManagerWrap);
    }

    private void loadData() {
        if(TextUtils.isEmpty(mOrder))
            return;

        if(null == mUserInfo)
            return;

        OrderDetailRequest request = new OrderDetailRequest();
        request.setCybh(mUserInfo.getCybh());
        request.setDwbh(mUserInfo.getDwbh());
        request.setJlbh(mOrder);
        getRepository(NetService.class).getOrderDetail(UrlConfig.FD_DDXX, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<OrderDetailResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<OrderDetailResponse> result) {
                        if (result.isSuccessful()) {
                            mOrderDetailResponse = result.getData();
                            mBinding.llOrder.tvRoomNo.setText("房号:" + result.getData().getFh());
                            mBinding.llOrder.tvRoomFloor.setText("楼层:" + result.getData().getFjlc());
                            mBinding.llOrder.tvRoomStyle.setText("房型:" + result.getData().getFjlx());
                            mBinding.llOrder.tvRoomAddress.setText(result.getData().getFwmc() + result.getData().getDh() + "栋");
                            mOrderStatus = result.getData().getZt();
                            setOrderStatus();
                            if(!TextUtils.isEmpty(mOrderStatus)){
                                if(Constants.ROOM_ORDER_READY.equals(mOrderStatus)){
                                    mBinding.llOrder.tvOutTimeSub.setText(R.string.room_pre_time);
                                } else if(Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
                                    mBinding.llOrder.tvOutTimeSub.setText(R.string.room_pre_time);
                                } else {
                                    mBinding.llOrder.tvOutTimeSub.setText(R.string.room_out_time);
                                }
                            }

                            mBinding.llOrder.tvInTime.setText(result.getData().getRzsj());
                            mBinding.llOrder.tvOutTime.setText(result.getData().getTfsj());
                            mBinding.etContent.setText(result.getData().getDd_password());
                            //设置密码/蓝牙显示状态
                            String sType = result.getData().getS_type();
                            if(null == sType || TextUtils.isEmpty(sType)){
                                setPwdVisible(false);
                            } else {
                                setPwdVisible(true);
                                if("2".equals(sType)){
                                    //显示蓝牙设置功能
                                    mBinding.clBt.setVisibility(View.VISIBLE);
                                } else {
                                    //隐藏蓝牙设置功能
                                    mBinding.clBt.setVisibility(View.GONE);
                                }
                            }
                            //设置蓝牙开启/关闭状态
                            String btZt = result.getData().getBt_zt();
                            if(null != btZt && !TextUtils.isEmpty(btZt)
                            && "1".equals(btZt)){
                                //蓝牙开启
                                mBinding.scBt.setChecked(true);
//                                mBinding.msCheck.setCurrentItem(0);
                            } else {
                                //蓝牙关闭
                                mBinding.scBt.setChecked(false);
//                                mBinding.msCheck.setCurrentItem(1);
                            }

                            //订单状态为已入住时，显示密码/蓝牙，其他状态隐藏
                            if(Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
                                setPwdVisible(true);
                                mBinding.clBt.setVisibility(View.VISIBLE);
                            } else {
                                setPwdVisible(false);
                                mBinding.clBt.setVisibility(View.GONE);
                            }
                            //密码显示/隐藏总开关
                            if(null != mUserInfo && mUserInfo.pwdVisible()){
                                setPwdVisible(true);
                            } else {
                                setPwdVisible(false);
                            }

                            List<OrderDetailResponse.ListBean> list = result.getData().getList();
                            if (!list.isEmpty()) {
                                String num = String.format(mContext.getString(R.string.check_in_num), list.size());
                                mBinding.tvCheckInNum.setText(num);
                                mBinding.tvCheckInNum.setVisibility(View.VISIBLE);
                                mPersonAdapter.getData().clear();
                                mPersonAdapter.setNewData(list);
                            } else {
                                mBinding.tvCheckInNum.setVisibility(View.GONE);
                            }
                        } else {
                            if(!TextUtils.isEmpty(result.getMessage()))
                                showCenterToast(result.getMessage());
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
        if (v == mBinding.llOrder.tvAdd) {
            Intent intent = new Intent(mContext, UserAuthActivity.class);
            if(!TextUtils.isEmpty(mOrder)){
                intent.putExtra(SpConfig.KEY_ORDER_NUMBER, mOrder);
            }
            if(!TextUtils.isEmpty(mOrderStatus)){
                if(Constants.ROOM_ORDER_READY.equals(mOrderStatus)){
                    intent.putExtra(ParamConfig.PARAM_OPERATE_STYLE, ConstantConfig.ROOM_AUTH_IN);
                } else if(Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
                    intent.putExtra(ParamConfig.PARAM_OPERATE_STYLE, ConstantConfig.ROOM_ADD_AUTH_IN);
                }
            }
            startActivity(intent);
        } else if(v == mBinding.btResetPwd){
            String pwd = mBinding.etContent.getText().toString();
            if(null == pwd || TextUtils.isEmpty(pwd)){
                showCenterToast(getString(R.string.can_not_null_pwd));
                return;
            }
            setPassword(pwd);
        } else if(v == mBinding.btResetShendunPwd){
            GetLockPwd request = new GetLockPwd();
            request.setPtype(Constants.LOCK_GET_PASSWORD);//2、表示获取门锁密码
            request.setJlbh(mOrder);
            request.setZjhm(mOrderDetailResponse.getZjhm());
            request.setXm(mOrderDetailResponse.getXm());
            request.setZt(mOrderDetailResponse.getZt());
            getRepository(NetService.class).getSdLock(UrlConfig.SDLOCK, request.getRequestBody())
                    .compose(dispatchSchedulers(false))
                    .subscribe(new RepositorySubscriber<ResponseBean<GetLockPwdResponse>>() {
                        @Override
                        protected void onResponse(ResponseBean<GetLockPwdResponse> result) {
                            if (result.isSuccessful()) {
                                mBinding.etContent.setText(result.getData().getDdmm());
                                setPassword(result.getData().getDdmm());
                            } else {
                                if(!TextUtils.isEmpty(result.getMessage()))
                                    showCenterToast(result.getMessage());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable t) {
                            super.onError(t);
                        }
                    });
        } else if(v == mBinding.scBt){
            SwitchCompat switchCompat = (SwitchCompat) v;
            if (switchCompat.isChecked()) {
                //开启蓝牙
                setBt("1");
            } else {
                //关闭蓝牙
                setBt("0");
            }
        } else if(v == mBinding.btModify){
            if(null != mOrderStatus && !TextUtils.isEmpty(mOrderStatus)){
                if(Constants.ROOM_ORDER_READY.equals(mOrderStatus)){
                    //预定修改
                    JumpToEditInActivity(true);
                }
                if(Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
                    //续住修改
                    JumpToEditInActivity(false);
                }
            }
        } else if(v == mBinding.btCohabitant){
            //增加同住人
            JumpToCohaInActivity(true);
        }
    }

    private void setOrderStatus(){
        if(null != mOrderStatus && !TextUtils.isEmpty(mOrderStatus)){
            if(Constants.ROOM_ORDER_READY.equals(mOrderStatus)){
                mBinding.btModify.setText("预定修改");
                mBinding.btModify.setVisibility(View.VISIBLE);
                mBinding.btCohabitant.setVisibility(View.GONE);
            }
            if(Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
                mBinding.btModify.setText("续住修改");
                mBinding.btModify.setVisibility(View.VISIBLE);
                mBinding.btCohabitant.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     * 参数add。true:预定修改; false:续住修改
     */
    private void JumpToEditInActivity(boolean add){
        Intent intent = new Intent(mContext, EditInActivity.class);
        if(add){
            intent.putExtra(ParamConfig.PARAM_PERSON_STATUS, ConstantConfig.ORDER_ADD);
        } else {
            intent.putExtra(ParamConfig.PARAM_PERSON_STATUS, ConstantConfig.ORDER_UADATE);
        }
        if(!TextUtils.isEmpty(mOrder)){
            intent.putExtra(SpConfig.KEY_ORDER_NUMBER, mOrder);
        }
        if(!TextUtils.isEmpty(mOrderStatus)){
            intent.putExtra(SpConfig.KEY_ORDER_STATUS, mOrderStatus);
        }
        if(null != mOrderDetailResponse){
            if(null != mOrderDetailResponse.getFh() && !TextUtils.isEmpty(mOrderDetailResponse.getFh())){
                intent.putExtra(ParamConfig.PARAM_ROOM_NO, mOrderDetailResponse.getFh());
            }
            if(null != mOrderDetailResponse.getFhid() && !TextUtils.isEmpty(mOrderDetailResponse.getFhid())){
                intent.putExtra(ParamConfig.PARAM_ROOM_ID, mOrderDetailResponse.getFhid());
            }
        }
        startActivity(intent);
    }

    private void JumpToCohaInActivity(boolean add){
        Intent intent = new Intent(mContext, CohaInActivity.class);
        intent.putExtra(ParamConfig.PARAM_ORDER_TYPE, ConstantConfig.TYPE_COHA);
        if(add){
            intent.putExtra(ParamConfig.PARAM_PERSON_STATUS, ConstantConfig.ORDER_ADD);
        } else {
            intent.putExtra(ParamConfig.PARAM_PERSON_STATUS, ConstantConfig.ORDER_UADATE);
        }

        if(!TextUtils.isEmpty(mOrder)){
            intent.putExtra(SpConfig.KEY_ORDER_NUMBER, mOrder);
        }
        if(!TextUtils.isEmpty(mOrderStatus)){
            intent.putExtra(SpConfig.KEY_ORDER_STATUS, mOrderStatus);
        }
        if(null != mOrderDetailResponse){
            if(null != mOrderDetailResponse.getFh() && !TextUtils.isEmpty(mOrderDetailResponse.getFh())){
                intent.putExtra(ParamConfig.PARAM_ROOM_NO, mOrderDetailResponse.getFh());
            }
            if(null != mOrderDetailResponse.getFhid() && !TextUtils.isEmpty(mOrderDetailResponse.getFhid())){
                intent.putExtra(ParamConfig.PARAM_ROOM_ID, mOrderDetailResponse.getFhid());
            }
        }
        startActivity(intent);
    }

    /**
     * 更新密码
     */
    private void setPassword(final String pwd){
        if(TextUtils.isEmpty(mOrder))
            return;

        if(null == mUserInfo)
            return;

        UploadLockBtPwd request = new UploadLockBtPwd();
        request.setCybh(mUserInfo.getCybh());
        request.setDwbh(mUserInfo.getDwbh());
        request.setJlbh(mOrder);
        request.setCtype(SET_PWD);
        request.setCvalue(pwd);
        request.setZt("");
        getRepository(NetService.class).uploadPassword(UrlConfig.FD_DD_CONFIG, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<GetLockPwdResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<GetLockPwdResponse> result) {
                        if(!TextUtils.isEmpty(result.getMessage())){
                            showCenterToast(result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                    }
                });
    }

    /**
     * 设置蓝牙
     */
    private void setBt(final String zt){
        if(TextUtils.isEmpty(mOrder))
            return;

        if(null == mUserInfo)
            return;

        UploadLockBtPwd request = new UploadLockBtPwd();
        request.setCybh(mUserInfo.getCybh());
        request.setDwbh(mUserInfo.getDwbh());
        request.setJlbh(mOrder);
        request.setCtype(SET_BT);
        request.setCvalue("");
        request.setZt(zt);
        getRepository(NetService.class).uploadPassword(UrlConfig.FD_DD_CONFIG, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<GetLockPwdResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<GetLockPwdResponse> result) {
                        if(!TextUtils.isEmpty(result.getMessage())){
                            showCenterToast(result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                    }
                });
    }

    private void setPwdVisible(boolean visible){
        mBinding.etContent.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBinding.btResetShendunPwd.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}