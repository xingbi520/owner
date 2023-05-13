package com.shendun.renter.fragment.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.repository.bean.OrderResponse.DataBean.ListBean;
import com.shendun.renter.repository.bean.Room;

public class RoomChildAdapter extends BaseQuickAdapter<Room, BaseViewHolder> {

    public RoomChildAdapter() {
        super(R.layout.item_room_child);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Room item) {
        helper.setText(R.id.tv_room_no, "房号:" + item.getFh());
        helper.setText(R.id.tv_room_floor, "楼层:" + item.getFjlc());
        helper.setText(R.id.tv_room_style, "房型:" + item.getFjlx());
        helper.setText(R.id.tv_room_address, item.getFwmc() + item.getDh() + "栋");

        String roomStatus = item.getZt();
        if(!TextUtils.isEmpty(roomStatus)){
            if(Constants.ROOM_ORDER_FREE.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_kx);
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_free);
                helper.setText(R.id.tv_status, R.string.room_free);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_C9C9C9));
                helper.setVisible(R.id.tv_check_in, true);
            } else if(Constants.ROOM_ORDER_READY.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_ready);
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_ready);
                helper.setText(R.id.tv_status, R.string.room_ready);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_47CEFC));
                helper.setVisible(R.id.tv_check_in, false);
            } else if(Constants.ROOM_ORDER_CHECK_IN.equals(roomStatus)){
                helper.setImageResource(R.id.iv_image, R.mipmap.room_in);
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_in);
                helper.setText(R.id.tv_status, R.string.room_in);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_F47F72));
                helper.setVisible(R.id.tv_check_in, false);
            }
        }

        helper.addOnClickListener(R.id.tv_check_in);
    }
}
