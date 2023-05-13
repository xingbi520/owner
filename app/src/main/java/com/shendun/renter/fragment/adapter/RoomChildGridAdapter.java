package com.shendun.renter.fragment.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.repository.bean.Room;

public class RoomChildGridAdapter extends BaseQuickAdapter<Room, BaseViewHolder> {

    public RoomChildGridAdapter() {
        super(R.layout.item_room_child_grid);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Room item) {
        helper.setText(R.id.tv_room_no, item.getFh());
        helper.setText(R.id.tv_floor_no, item.getFjlc());
        helper.setText(R.id.tv_room_style, item.getFjlx());

        String roomStatus = item.getZt();
        if(!TextUtils.isEmpty(roomStatus)){
            if(Constants.ROOM_ORDER_FREE.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_kx);
                helper.setGone(R.id.tv_check_in, true);
            } else if(Constants.ROOM_ORDER_READY.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_ready);
                helper.setGone(R.id.tv_check_in, false);
            } else if(Constants.ROOM_ORDER_CHECK_IN.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_in);
                helper.setGone(R.id.tv_check_in, false);
            }
        }

        helper.addOnClickListener(R.id.tv_check_in);
    }
}
