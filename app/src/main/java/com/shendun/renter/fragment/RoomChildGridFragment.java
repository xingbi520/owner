package com.shendun.renter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.shendun.architecture.base.BaseFragment;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.activity.AddInActivity;
import com.shendun.renter.activity.RoomDetailActivity;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.FragmentOrderGridRoomBinding;
import com.shendun.renter.event.RoomSearchEvent;
import com.shendun.renter.fragment.adapter.RoomChildGridAdapter;
import com.shendun.renter.ui.popup.RoomsPopupList;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.Room;
import com.shendun.renter.repository.bean.RoomRequest;
import com.shendun.renter.repository.bean.RoomResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.ui.popup.FilesContentPopupAdapter;
import com.shendun.renter.ui.popup.SortMultiItemEntity;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.widget.ChoosePopupWindow;
import com.shendun.renter.widget.layoutmanager.GridLayoutManagerWrap;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.shendun.renter.ui.popup.SortMultiItemEntity.TYPE_CONTENT;

public class RoomChildGridFragment extends BaseFragment<FragmentOrderGridRoomBinding> {

    private String mOrderType = Constants.ROOM_ORDER_ALL;

    private String oldRoomsContentTitle = RoomsPopupList.RoomsAll;
    private String currentRoomsContentTitle = RoomsPopupList.RoomsAll;

    protected int page = 1;
    protected boolean isRefreshing = false;

    private String mXqdm = "";     //辖区代码
    private String mKeyword = "";  //关键字
    private String mFjlx = "";     //房间类型

    protected RoomChildGridAdapter adapter;

    public static RoomChildGridFragment newInstance(final String orderType) {
        RoomChildGridFragment fragment = new RoomChildGridFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FRAGMENT_ARGUMENTS, orderType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_grid_room;
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
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
        loadData();
    }

    private void clear(){
        unDispose();
        page = 1;
        isRefreshing = true;
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        adapter = new RoomChildGridAdapter();
        mBinding.rv.setAdapter(adapter);
        GridLayoutManagerWrap gridLayoutManagerWrap = new GridLayoutManagerWrap(mContext, 5);
        gridLayoutManagerWrap.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (null == adapter
                            || adapter.getData() == null
                            || adapter.getData().isEmpty()
                            || position == adapter.getData().size()) {
                        return 5;
                    }

                    return 1;
                }
            });
        mBinding.rv.setLayoutManager(gridLayoutManagerWrap);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                Room item = adapter.getData().get(position);
                Intent intent = new Intent(mContext, RoomDetailActivity.class);
                intent.putExtra(SpConfig.KEY_ROOM_DETAIL, item);
                startActivity(intent);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                if (view.getId() == R.id.tv_check_in) {
                    Room item = adapter.getData().get(position);
                    Intent intent = new Intent(mContext, AddInActivity.class);
                    intent.putExtra(ParamConfig.PARAM_ORDER_TYPE, ConstantConfig.TYPE_ORDER);
                    intent.putExtra(ParamConfig.PARAM_ROOM_NO, item.getFh());
                    intent.putExtra(ParamConfig.PARAM_ROOM_ID, item.getFhid());
                    startActivity(intent);
                }
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mBinding.rv);
        adapter.setEnableLoadMore(true);
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        mBinding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_blue));
//        roomChildAdapter.setEmptyView(R.layout.layout_empty, (ViewGroup) mBinding.rv.getParent());

        mBinding.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClContainer(currentRoomsContentTitle, R.color.cloud_normal_dark,
                        R.drawable.ic_content_arrow_up_grey, R.drawable.shape_unselect_radius_17);
                showRoomsPop();
            }
        });
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
                        adapter.setEnableLoadMore(true);
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
                                        adapter.getData().clear();
                                        adapter.notifyDataSetChanged();
                                        return;
                                    }
                                    adapter.loadMoreEnd(false);
                                } else {
                                    if (page == 1) {
                                        adapter.setNewData(list);
                                    } else {
                                        adapter.addData(list);
                                    }
                                    adapter.loadMoreComplete();
                                    page++;
                                }
                            } else {
                                adapter.loadMoreFail();
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
                            adapter.loadMoreFail();
                        }
                    }
                });
    }

    /**
     * 显示内容弹窗
     */
    private void showRoomsPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_bottom_content_popup, view -> {
            BaseMultiItemQuickAdapter adapter = new FilesContentPopupAdapter(
                    RoomsPopupList.createFilesContentList(currentRoomsContentTitle));
            adapter.setOnItemClickListener((adapter1, view1, position) -> {
                SortMultiItemEntity item = (SortMultiItemEntity) adapter1.getItem(position);
                if (item.getItemType() == TYPE_CONTENT) {
                    if(!item.isSelect()){
                        List<SortMultiItemEntity> dataList = adapter1.getData();
                        int num = dataList.isEmpty() ? 0 : dataList.size();
                        for(int i=0; i<num; i++){
                            dataList.get(i).setSelect(false);
                        }
                        item.setSelect(true);
                        adapter1.notifyDataSetChanged();

                        currentRoomsContentTitle = (String) item.getData();
                    }
                }
            });
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setAdapter(adapter);
            GridLayoutManagerWrap gridLayoutManagerWrap = new GridLayoutManagerWrap(mContext, 2);
            gridLayoutManagerWrap.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManagerWrap);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentRoomsContentTitle = oldRoomsContentTitle;
                    setFileContent();

                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldRoomsContentTitle = currentRoomsContentTitle;
                    if("全部房型".equals(currentRoomsContentTitle)){
                        mFjlx = "";
                    } else {
                        mFjlx = currentRoomsContentTitle;
                    }
                    doRefresh();
                    setFileContent();

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {
                currentRoomsContentTitle = oldRoomsContentTitle;
            }
        });
        ((ChoosePopupWindow)popupWindow).show(getActivity().getWindow().getDecorView(), getActivity().getWindow());
    }

    private void setFileContent(){
        if(RoomsPopupList.RoomsAll.equals(currentRoomsContentTitle)){
            setClContainer(currentRoomsContentTitle, R.color.cloud_normal_dark,
                    R.drawable.ic_content_arrow_down_grey, R.drawable.shape_unselect_radius_17);
        } else {
            setClContainer(currentRoomsContentTitle, R.color.color_0085ff,
                    R.drawable.ic_content_arrow_down_blue, R.drawable.shape_selected_radius_17);
        }
    }

    private void setClContainer(String content, int colorId, int resId, int resBackgroundId){
        mBinding.clContainer.setBackground(getResources().getDrawable(resBackgroundId));
        mBinding.tvContent.setText(content);
        mBinding.tvContent.setTextColor(getResources().getColor(colorId));
        mBinding.ivArrow.setImageResource(resId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RoomSearchEvent roomSearchEvent) {
        mKeyword = roomSearchEvent.getKeyWord();
        mXqdm = "";
        mFjlx = "";
        doRefresh();
    }
}
