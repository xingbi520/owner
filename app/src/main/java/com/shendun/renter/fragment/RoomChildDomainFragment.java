package com.shendun.renter.fragment;

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
import com.shendun.renter.R;
import com.shendun.renter.activity.AddInActivity;
import com.shendun.renter.activity.RoomDetailActivity;
import com.shendun.renter.activity.UserAuthActivity;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.FragmentOrderRoomBinding;
import com.shendun.renter.event.DomainRoomEvent;
import com.shendun.renter.event.RoomSearchEvent;
import com.shendun.renter.fragment.adapter.RoomChildAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.Room;
import com.shendun.renter.repository.bean.RoomRequest;
import com.shendun.renter.repository.bean.RoomResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.widget.layoutmanager.LinearLayoutManagerWrap;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RoomChildDomainFragment extends BaseFragment<FragmentOrderRoomBinding> {

    private String mOrderType = Constants.ROOM_ORDER_ALL;

    protected int page = 1;
    protected boolean isRefreshing = false;

    private String mXqdm = "";     //辖区代码
    private String mKeyword = "";  //关键字
    private String mFjlx = "";     //房间类型

    protected RoomChildAdapter roomChildAdapter;

    public static RoomChildDomainFragment newInstance(final String orderType) {
        RoomChildDomainFragment fragment = new RoomChildDomainFragment();
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
        roomChildAdapter.getData().clear();
        roomChildAdapter.notifyDataSetChanged();
        roomChildAdapter.setEnableLoadMore(false);
        roomChildAdapter.loadMoreEnd(true);
        loadData();
    }

    private void clear(){
        unDispose();
        page = 1;
        isRefreshing = true;
        roomChildAdapter.getData().clear();
        roomChildAdapter.notifyDataSetChanged();
        roomChildAdapter.setEnableLoadMore(false);
        roomChildAdapter.loadMoreEnd(true);
    }

    private void initRecyclerView() {
        roomChildAdapter = new RoomChildAdapter();
        mBinding.rv.setAdapter(roomChildAdapter);
        LinearLayoutManagerWrap linearLayoutManagerWrap = new LinearLayoutManagerWrap(mContext);
        linearLayoutManagerWrap.setOrientation(RecyclerView.VERTICAL);
        mBinding.rv.setLayoutManager(linearLayoutManagerWrap);
        roomChildAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Room item = roomChildAdapter.getData().get(position);
                Intent intent = new Intent(mContext, RoomDetailActivity.class);
                intent.putExtra(SpConfig.KEY_ROOM_DETAIL, item);
                startActivity(intent);
            }
        });
        roomChildAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_check_in) {
                    Room item = roomChildAdapter.getData().get(position);
                    Intent intent = new Intent(mContext, AddInActivity.class);
                    intent.putExtra(ParamConfig.PARAM_ORDER_TYPE, ConstantConfig.TYPE_ORDER);
                    intent.putExtra(ParamConfig.PARAM_ROOM_NO, item.getFh());
                    intent.putExtra(ParamConfig.PARAM_ROOM_ID, item.getFhid());
                    startActivity(intent);
                }
            }
        });
        roomChildAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mBinding.rv);
        roomChildAdapter.setEnableLoadMore(true);
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        mBinding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_blue));
//        roomChildAdapter.setEmptyView(R.layout.layout_empty, (ViewGroup) mBinding.rv.getParent());
    }

    private void loadData() {
        UserInfo userInfo = CacheManager.readFromJson(getContext(), ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            return;
        }

        RoomRequest request = new RoomRequest();
        request.setCybh(userInfo.getCybh());
        request.setDwbh(userInfo.getDwbh());
        request.setZt(mOrderType);
        request.setXqdm(mXqdm);
        request.setS_val(mKeyword);
        request.setFjlx(mFjlx);
        request.setCurrpage(String.valueOf(page));
        request.setPagesize(String.valueOf(Constants.ROOM_ORDER_PAGE_SIZE));
        getRepository(NetService.class).getRooms(UrlConfig.FD_SEARCH_FH, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .doFinally(() -> {
                    if (isRefreshing) {
                        isRefreshing = false;
                        mBinding.refreshLayout.setRefreshing(false);
                        roomChildAdapter.setEnableLoadMore(true);
                    }
                })
                .subscribe(new RepositorySubscriber<RoomResponse>() {
                    @Override
                    protected void onResponse(RoomResponse bean) {
                        try {
                            if (bean != null && bean.getCode().equals(Constants.RESPONSE_SUCCEED)) {
                                RoomResponse.DataBean pageBean = bean.getData();
                                List<Room> list = pageBean.getList();
                                if (list.isEmpty()) {
                                    if (page == 1) {
                                        roomChildAdapter.getData().clear();
                                        roomChildAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                    roomChildAdapter.loadMoreEnd();
                                } else {
                                    if (page == 1) {
                                        roomChildAdapter.setNewData(list);
                                    } else {
                                        roomChildAdapter.addData(list);
                                    }
                                    roomChildAdapter.loadMoreComplete();
                                    page++;
                                }
                            } else {
                                roomChildAdapter.loadMoreFail();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        if (page == 1) {
                            showMessage(R.string.exception_default_tip);
                        } else {
                            roomChildAdapter.loadMoreFail();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RoomSearchEvent roomSearchEvent) {
        mKeyword = roomSearchEvent.getKeyWord();
        doRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DomainRoomEvent domainRoomEvent) {
        String code = domainRoomEvent.getDomainCode();
        if(null != code){
            mXqdm = code;
            doRefresh();
        }
    }
}
