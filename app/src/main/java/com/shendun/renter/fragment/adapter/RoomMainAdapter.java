package com.shendun.renter.fragment.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.shendun.architecture.base.BaseFragment;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.fragment.OrderChildFragment;
import com.shendun.renter.fragment.RoomChildDomainFragment;
import com.shendun.renter.fragment.RoomChildFragment;
import com.shendun.renter.fragment.RoomChildGridFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoomMainAdapter extends FragmentStatePagerAdapter {

    private List<String> titleList;
    private BaseFragment mCurrentFragment;

    public RoomMainAdapter(FragmentManager fm, List<String> titleList) {
        super(fm);
        this.titleList = titleList;
    }

    //获取集合中的某个项
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return RoomChildGridFragment.newInstance(Constants.ROOM_ORDER_ALL);
            case 2:
                return RoomChildFragment.newInstance(Constants.ROOM_ORDER_READY);
            case 3:
                return RoomChildFragment.newInstance(Constants.ROOM_ORDER_CHECK_IN);
            case 4:
                return RoomChildDomainFragment.newInstance(Constants.ROOM_ORDER_ALL);
            case 0:
            default:
                return RoomChildFragment.newInstance(Constants.ROOM_ORDER_ALL);
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

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public BaseFragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
