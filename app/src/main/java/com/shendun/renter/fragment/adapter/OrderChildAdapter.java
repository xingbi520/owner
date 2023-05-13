package com.shendun.renter.fragment.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shendun.renter.R;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.repository.bean.OrderResponse.DataBean.ListBean;

public class OrderChildAdapter extends BaseQuickAdapter<ListBean, BaseViewHolder> {

    public OrderChildAdapter() {
        super(R.layout.item_order_for_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ListBean item) {
        helper.setText(R.id.tv_room_no, "房号:" + item.getFh());
        helper.setText(R.id.tv_room_floor, "楼层:" + item.getFjlc());
        helper.setText(R.id.tv_room_style, "房型:" + item.getFjlx());
        helper.setText(R.id.tv_room_address, item.getFwmc() + item.getDh() + "栋");

        String roomStatus = item.getZt();
        if(!TextUtils.isEmpty(roomStatus)){
            if(Constants.ROOM_ORDER_READY.equals(roomStatus)){
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_ready);
                helper.setText(R.id.tv_status, R.string.room_ready);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_47CEFC));
                helper.setText(R.id.tv_check_in, R.string.action_cancel_order);
                helper.setVisible(R.id.tv_check_in, true);
                helper.setText(R.id.tv_out_time_sub, R.string.room_pre_time);
            } else if(Constants.ROOM_ORDER_CHECK_IN.equals(roomStatus)){
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_in);
                helper.setText(R.id.tv_status, R.string.room_in);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_F47F72));
                helper.setText(R.id.tv_check_in, R.string.action_room_out);
                helper.setVisible(R.id.tv_check_in, true);
                helper.setText(R.id.tv_out_time_sub, R.string.room_pre_time);
            } else {
                helper.setImageResource(R.id.iv_dot, R.mipmap.ic_checkin_free);
                helper.setText(R.id.tv_status, R.string.room_out);
                helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.color_C9C9C9));
                helper.setVisible(R.id.tv_check_in, false);
                helper.setText(R.id.tv_out_time_sub, R.string.room_out_time);
            }
        }

        helper.setText(R.id.tv_in_time, item.getRzsj());
        helper.setText(R.id.tv_out_time, item.getTfsj());
        helper.addOnClickListener(R.id.tv_check_in);
    }
}
