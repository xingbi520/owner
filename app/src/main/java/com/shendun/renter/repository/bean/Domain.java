package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class Domain implements Serializable{
    private String xqdm;
    private String xqmc;

    public String getXqdm() {
        return xqdm;
    }

    public void setXqdm(String xqdm) {
        this.xqdm = xqdm;
    }

    public String getXqmc() {
        return xqmc;
    }

    public void setXqmc(String xqmc) {
        this.xqmc = xqmc;
    }
}
