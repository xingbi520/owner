package com.shendun.renter.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.shendun.architecture.base.BaseFragment;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.architecture.utils.DataHelper;
import com.shendun.renter.R;
import com.shendun.renter.activity.OrderDetailActivity;
import com.shendun.renter.activity.UserAuthActivity;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.FragmentOrderRoomBinding;
import com.shendun.renter.dialog.NormalDialog;
import com.shendun.renter.dialog.animation.FadeEnter.FadeEnter;
import com.shendun.renter.dialog.animation.ZoomExit.ZoomOutExit;
import com.shendun.renter.event.OrderSearchEvent;
import com.shendun.renter.event.RoomSearchEvent;
import com.shendun.renter.fragment.adapter.OrderChildAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.GetLockPwd;
import com.shendun.renter.repository.bean.GetLockPwdResponse;
import com.shendun.renter.repository.bean.OrderModifyRequest;
import com.shendun.renter.repository.bean.OrderRequest;
import com.shendun.renter.repository.bean.OrderResponse;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.widget.layoutmanager.LinearLayoutManagerWrap;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class OrderChildFragment extends BaseFragment<FragmentOrderRoomBinding> {

    private String mOrderType = Constants.ROOM_ORDER_ALL;
    private Context mContext;

    protected int page = 1;
    protected boolean isRefreshing = false;

    private String mKeyword = "";  //关键字

    protected OrderChildAdapter orderChildAdapter;

    public static OrderChildFragment newInstance(final String orderType) {
        OrderChildFragment fragment = new OrderChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FRAGMENT_ARGUMENTS, orderType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_room;
    }

    @Override
    protected void initEvent() {
        Bundle bundle = getArguments();
        if(null != bundle){
            mOrderType = bundle.getString(Constants.FRAGMENT_ARGUMENTS);
        }
        mContext = getContext();

        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        doRefresh();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportVisible();
        clear();
    }

    /**
     * 初始化下拉刷新业务
     */
    private void initRefreshLayout() {
        mBinding.refreshLayout.setOnRefreshListener(() -> {
            doRefresh();
        });
        mBinding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_blue));
    }

    private void doRefresh() {
        unDispose();
        page = 1;
        isRefreshing = true;
        orderChildAdapter.getData().clear();
        orderChildAdapter.notifyDataSetChanged();
        orderChildAdapter.setEnableLoadMore(false);
        orderChildAdapter.loadMoreEnd(true);
        loadData();
    }

    private void clear(){
        unDispose();
        page = 1;
        isRefreshing = true;
        orderChildAdapter.getData().clear();
        orderChildAdapter.notifyDataSetChanged();
        orderChildAdapter.setEnableLoadMore(false);
        orderChildAdapter.loadMoreEnd(true);
    }

    private void initRecyclerView() {
        orderChildAdapter = new OrderChildAdapter();
        mBinding.rv.setAdapter(orderChildAdapter);
        LinearLayoutManagerWrap linearLayoutManagerWrap = new LinearLayoutManagerWrap(mContext);
        linearLayoutManagerWrap.setOrientation(RecyclerView.VERTICAL);
        mBinding.rv.setLayoutManager(linearLayoutManagerWrap);
        orderChildAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrderResponse.DataBean.ListBean item = orderChildAdapter.getData().get(position);
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                if(!TextUtils.isEmpty(item.getJlbh())){
                    intent.putExtra(SpConfig.KEY_ORDER_NUMBER, item.getJlbh());
                }
                if(!TextUtils.isEmpty(item.getZt())){
                    intent.putExtra(SpConfig.KEY_ORDER_STATUS, item.getZt());
                }
                startActivity(intent);
            }
        });
        orderChildAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_check_in) {
                    OrderResponse.DataBean.ListBean item = orderChildAdapter.getData().get(position);
                    actionOrder(item, item.getJlbh(), item.getZt());
                }
            }
        });
        orderChildAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mBinding.rv);
        orderChildAdapter.setEnableLoadMore(true);
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        mBinding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_blue));
//        orderRoomAdapter.setEmptyView(R.layout.layout_empty, (ViewGroup) mBinding.rv.getParent());
    }

    private void loadData() {
        UserInfo userInfo = CacheManager.readFromJson(getContext(), ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            return;
        }

        OrderRequest request = new OrderRequest();
        request.setCybh(userInfo.getCybh());
        request.setDwbh(userInfo.getDwbh());
        request.setZt(mOrderType);
        request.setS_val(mKeyword);
        request.setCurrpage(String.valueOf(page));
        request.setPagesize(String.valueOf(Constants.ROOM_ORDER_PAGE_SIZE));
        getRepository(NetService.class).getOrders(UrlConfig.FD_DDLB, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .doFinally(() -> {
                    if (isRefreshing) {
                        isRefreshing = false;
                        mBinding.refreshLayout.setRefreshing(false);
                        orderChildAdapter.setEnableLoadMore(true);
                    }
                })
                .subscribe(new RepositorySubscriber<OrderResponse>() {
                    @Override
                    protected void onResponse(OrderResponse result) {
                        if ("0".equals(result.getCode())) {
                            List<OrderResponse.DataBean.ListBean> list = result.getData().getList();
                            if (list.isEmpty()) {
                                if (page == 1) {
                                    orderChildAdapter.getData().clear();
                                    orderChildAdapter.notifyDataSetChanged();
                                    return;
                                }
                                orderChildAdapter.loadMoreEnd();
                            } else {
                                if (page == 1) {
                                    orderChildAdapter.setNewData(list);
                                } else {
                                    orderChildAdapter.addData(list);
                                }
                                orderChildAdapter.loadMoreComplete();
                                page++;
                            }
                        } else {
                            orderChildAdapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        if (page == 1) {
                            showMessage(R.string.exception_default_tip);
                        } else {
                            orderChildAdapter.loadMoreFail();
                        }
                    }
                });
    }

    private void actionOrder(final OrderResponse.DataBean.ListBean bean, final String jlbh, final String zt){
        if(null == bean || null == bean.getZt() || TextUtils.isEmpty(bean.getZt())){
            return;
        }

        String content = "";
        String action = "";
        if (Constants.ROOM_ORDER_READY.equals(bean.getZt())) {
            content = getString(R.string.cancel_order_ask);
            action = Constants.ORDER_ACTION_CANCEL;
        } else if(Constants.ROOM_ORDER_CHECK_IN.equals(bean.getZt())){
            content = getString(R.string.room_out_ask);
            action = Constants.ORDER_ACTION_CHECKOUT;
        }

        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.isContentShow(true)
                .titleTextSize(50f)
                .content(content)
                .contentTextSize(50f)
                .contentTextColor(getResources().getColor(R.color.cloud_normal_dark))
                .style(NormalDialog.STYLE_TWO)//
                .showAnim(new FadeEnter())
                .btnNum(2)
                .btnTextSize(45f,45f)
                .btnText(getString(R.string.no),
                        getString(R.string.yes))
                .btnTextColor(getResources().getColor(R.color.cloud_light_dark),
                        getResources().getColor(R.color.cloud_theme))
                .dismissAnim(new ZoomOutExit())//
                .show();
        String finalAction = action;
        dialog.setOnBtnClickL(() -> {
            dialog.dismiss();
        }, () -> {
            cancelOrder(bean, finalAction);
            dialog.dismiss();
        });
    }


    private void cancelOrder(final OrderResponse.DataBean.ListBean bean, final String action){
        UserInfo userInfo = CacheManager.readFromJson(getContext(), ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            return;
        }

        OrderModifyRequest request = new OrderModifyRequest();
        request.setCybh(userInfo.getCybh());
        request.setDwbh(userInfo.getDwbh());
        request.setJlbh(bean.getJlbh());
        request.setZt(action);//3退房，4取消
        getRepository(NetService.class).modifyOrder(UrlConfig.FD_DDZT, request.getRequestBody())
                .compose(dispatchSchedulers(true))
                .subscribe(new RepositorySubscriber<ResponseBean>() {
                    @Override
                    protected void onResponse(ResponseBean result) {
                        if(result.isSuccessful()){
                            //退房需要释放门锁密码
                            if(Constants.ORDER_ACTION_CHECKOUT.equals(action)){
                                showCenterToast(getString(R.string.room_out_success));
                                releaseLock(bean);
                            } else {
                                showCenterToast(getString(R.string.cancel_success));
                            }

                            doRefresh();
                        } else {
                            if(null != result.getMessage() && !TextUtils.isEmpty(result.getMessage())){
                                showCenterToast(result.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        showCenterToast(getString(R.string.exception_default_tip));
                    }
                });
    }

    private void releaseLock(final OrderResponse.DataBean.ListBean bean){
        if(null == bean){
            return;
        }

        GetLockPwd request = new GetLockPwd();
        request.setPtype(Constants.LOCK_REGISTER_CANCEL);//1.表示门锁的登记和退房
        request.setJlbh(bean.getJlbh());
        request.setXm(bean.getXm());
        request.setZjhm(bean.getZjhm());
        request.setZt("0");
        getRepository(NetService.class).getSdLock(UrlConfig.SDLOCK, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<GetLockPwdResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<GetLockPwdResponse> result) {
                        if (!result.isSuccessful() && !TextUtils.isEmpty(result.getMessage())) {
//                            showCenterToast(result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OrderSearchEvent orderSearchEvent) {
        mKeyword = orderSearchEvent.getKeyWord();
        doRefresh();
    }
}
