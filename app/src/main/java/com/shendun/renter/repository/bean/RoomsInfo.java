package com.shendun.renter.repository.bean;

import java.io.Serializable;
import java.util.List;

public class RoomsInfo<T> implements Serializable{
    private String Recordcount;
    private String Currpage;
    private List<T> list;

    public String getRecordcount() {
        return Recordcount;
    }

    public void setRecordcount(String recordcount) {
        Recordcount = recordcount;
    }

    public String getCurrpage() {
        return Currpage;
    }

    public void setCurrpage(String currpage) {
        Currpage = currpage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
