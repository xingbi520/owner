package com.shendun.renter.ui.popup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shendun.renter.R;

import java.util.List;

import static com.shendun.renter.ui.popup.SortMultiItemEntity.TYPE_CATEGORY;
import static com.shendun.renter.ui.popup.SortMultiItemEntity.TYPE_CONTENT;

/**
 * Author:xw
 * Time: 10:36 AM
 */
public class FilesContentPopupAdapter extends BaseMultiItemQuickAdapter<SortMultiItemEntity, BaseViewHolder> {
    public FilesContentPopupAdapter(List<SortMultiItemEntity> data) {
        super(data);
        addItemType(TYPE_CATEGORY, R.layout.layout_bottom_popup_category);
        addItemType(TYPE_CONTENT, R.layout.layout_bottom_files_content_popup_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SortMultiItemEntity item) {
        switch (item.getItemType()) {
            case TYPE_CATEGORY:
                String title = (String) item.getData();
                helper.setText(R.id.title, title);
                break;
            case TYPE_CONTENT:
                String content = (String) item.getData();
                helper.getView(R.id.container).setSelected(item.isSelect());
                helper.setGone(R.id.corner, item.isSelect());
                helper.setText(R.id.title, content);
                helper.setTextColor(R.id.title, item.isSelect() ? mContext.getResources().getColor(R.color.color_008aff)
                        : mContext.getResources().getColor(R.color.cloud_normal_dark));
                break;
        }
    }
}
