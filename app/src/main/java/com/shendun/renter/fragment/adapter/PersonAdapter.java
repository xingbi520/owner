package com.shendun.renter.fragment.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shendun.renter.R;
import com.shendun.renter.repository.bean.OrderDetailResponse.ListBean;

public class PersonAdapter extends BaseQuickAdapter<ListBean, BaseViewHolder> {

    public PersonAdapter() {
        super(R.layout.item_person_detail);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ListBean item) {
        helper.setText(R.id.tv_content_name, item.getXm());
        String xb = item.getXb();
        if(!TextUtils.isEmpty(xb))
            helper.setText(R.id.tv_content_sex, "1".equals(xb) ? "男" : "女");
        helper.setText(R.id.tv_content_id, item.getZjhm());
        helper.setText(R.id.tv_content_phone, item.getLxdh());
    }
}
