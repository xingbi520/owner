package com.shendun.renter.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.shendun.renter.fragment.MeFragment;
import com.shendun.renter.fragment.OrderMainFragment;
import com.shendun.renter.fragment.RoomMainFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new OrderMainFragment();
            case 2:
                return new MeFragment();
            case 0:
            default:
                return new RoomMainFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
