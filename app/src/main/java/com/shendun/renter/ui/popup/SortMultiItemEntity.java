package com.shendun.renter.ui.popup;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Author:xw
 * Time: 2:14 PM
 */
public class SortMultiItemEntity<T> implements MultiItemEntity {

    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_CONTENT = 2;

    private int type;

    private boolean select;

    private T data;

    public SortMultiItemEntity(int type, boolean select, T data) {
        this.type = type;
        this.select = select;
        this.data = data;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public int getType() {
        return type;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public T getData() {
        return data;
    }
}
