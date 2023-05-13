package com.shendun.renter.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.LogUtils;
import com.shendun.architecture.base.BaseFragment;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.FragmentOrderBinding;
import com.shendun.renter.event.OrderSearchEvent;
import com.shendun.renter.event.RoomSearchEvent;
import com.shendun.renter.fragment.adapter.OrderPagerAdapter;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.Domain;
import com.shendun.renter.repository.bean.DomainRequest;
import com.shendun.renter.repository.bean.DomainResponse;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.ScreenUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Author:xw
 * Time: 5:18 PM
 */
public class OrderMainFragment extends BaseFragment<FragmentOrderBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initEvent() {
        initData();
        initTabLayout();
    }

    @Override
    public FragmentAnimator getFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    private void initData(){
        mBinding.llDel.setOnClickListener(v -> clear());
        mBinding.tvSearch.setOnClickListener(v -> search());
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    private void initTabLayout() {
        List<String> mFragmentNamesList =
            Arrays.asList(getResources().getStringArray(R.array.fragmentOrderNames));
        mBinding.viewPager.setAdapter(
            new OrderPagerAdapter(getChildFragmentManager(), mFragmentNamesList));
        mBinding.viewPager.setOffscreenPageLimit(4);
        mBinding.tb.setViewPager(mBinding.viewPager);
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
            EventBus.getDefault().post(new OrderSearchEvent(keyWord));
        } else {
            EventBus.getDefault().post(new OrderSearchEvent(""));
        }
    }
}
