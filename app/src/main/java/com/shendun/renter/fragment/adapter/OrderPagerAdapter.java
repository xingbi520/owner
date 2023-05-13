package com.shendun.renter.fragment.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.shendun.renter.bean.Constants;
import com.shendun.renter.fragment.OrderChildFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> titleList;

    public OrderPagerAdapter(FragmentManager fm, List<String> titleList) {
        super(fm);
        this.titleList = titleList;
    }

    //获取集合中的某个项
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return OrderChildFragment.newInstance(Constants.ROOM_ORDER_READY);
            case 2:
                return OrderChildFragment.newInstance(Constants.ROOM_ORDER_CHECK_IN);
            case 3:
                return OrderChildFragment.newInstance(Constants.ROOM_ORDER_CHECK_OUT);
            case 0:
            default:
                return OrderChildFragment.newInstance(Constants.ROOM_ORDER_ALL);
        }
    }

    //返回绘制项的数目
    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
