package com.shendun.renter.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.gzuliyujiang.wheelpicker.widget.OptionWheelLayout;
import com.shendun.architecture.base.BaseFragment;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.FragmentRoomMainBinding;
import com.shendun.renter.event.DomainRoomEvent;
import com.shendun.renter.event.RoomSearchEvent;
import com.shendun.renter.fragment.adapter.RoomMainAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.Domain;
import com.shendun.renter.repository.bean.DomainRequest;
import com.shendun.renter.repository.bean.DomainResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.ui.popup.FilesContentPopupAdapter;
import com.shendun.renter.ui.popup.RoomsPopupList;
import com.shendun.renter.ui.popup.SortMultiItemEntity;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.widget.ChoosePopupWindow;
import com.shendun.renter.widget.layoutmanager.GridLayoutManagerWrap;
import com.shendun.renter.widget.tablayout.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

import static com.shendun.renter.ui.popup.SortMultiItemEntity.TYPE_CONTENT;

/**
 * Author:xw
 * Time: 5:18 PM
 */
public class RoomMainFragment extends BaseFragment<FragmentRoomMainBinding> {

    private List<Domain> domainList;

    private String domainCode;
    private String domainName;
    private int domainPosition = 0;

    private RoomMainAdapter roomMainAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_room_main;
    }

    @Override
    protected void initEvent() {
        initData();
        initTabLayout();
        loadData();
    }

    @Override
    public FragmentAnimator getFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    private void initData(){
        domainList = new ArrayList<>();
        mBinding.llDel.setOnClickListener(v -> clear());
        mBinding.tvSearch.setOnClickListener(v -> search());
    }

    private void initTabLayout() {
        List<String> mFragmentNamesList =
            Arrays.asList(getResources().getStringArray(R.array.fragmentRoomNames));
        roomMainAdapter = new RoomMainAdapter(getChildFragmentManager(), mFragmentNamesList);
        mBinding.viewPager.setAdapter(roomMainAdapter);
        mBinding.viewPager.setOffscreenPageLimit(5);
        mBinding.tb.setViewPager(mBinding.viewPager);
        mBinding.tb.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (4 == position) {
                    showDomainPop();
                }
            }

            @Override
            public void onTabReselect(int position) {
                if (4 == position) {
                    showDomainPop();
                }
            }
        });

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void clear(){
        mBinding.etContent.setText("");
    }

    private void search(){
        String keyWord = mBinding.etContent.getText().toString();
        if(keyWord != null && !TextUtils.isEmpty(keyWord)){
            EventBus.getDefault().post(new RoomSearchEvent(keyWord));
        } else {
            EventBus.getDefault().post(new RoomSearchEvent(""));
        }
    }

    /**
     * 获得辖区数据
     */
    private void loadData() {
        UserInfo userInfo = CacheManager.readFromJson(getContext(), ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            return;
        }

        DomainRequest request = new DomainRequest();
        request.setDwbh(userInfo.getDwbh());
        getRepository(NetService.class).getDomains(UrlConfig.GET_XQDM, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<DomainResponse>() {
                    @Override
                    protected void onResponse(DomainResponse bean) {
                        try {
                            if (bean != null && bean.getCode().equals(Constants.RESPONSE_SUCCEED)) {
                                DomainResponse.DataBean pageBean = bean.getData();
                                List<Domain> list = pageBean.getList();
                                if (null != list && !list.isEmpty()) {
                                    domainList = list;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        showMessage(R.string.exception_default_tip);
                    }
                });
    }

    /**
     * 显示辖区弹窗
     */
    private void showDomainPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_bottom_domain_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = new ArrayList<>();
            list.add("全部分局");
            for(Domain domain : domainList){
                list.add(domain.getXqmc());
            }
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(domainPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    domainPosition = wheelLayout.getWheelView().getCurrentPosition();
                    if(0 == domainPosition){
                        domainCode = "";
                    } else if(domainPosition > 0 && domainPosition <= domainList.size()){
                        domainCode = domainList.get(domainPosition-1).getXqdm();
                    }
                    refreshByDomain(domainCode);

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getActivity().getWindow().getDecorView(), getActivity().getWindow());
    }

    private void refreshByDomain(String domainCode){
        EventBus.getDefault().post(new DomainRoomEvent(domainCode));
    }
}
