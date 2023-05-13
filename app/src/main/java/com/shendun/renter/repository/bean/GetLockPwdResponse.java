package com.shendun.renter.repository.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetLockPwdResponse implements Serializable {

    @SerializedName("ddmm")
    private String ddmm;

    public String getDdmm() {
        return ddmm;
    }

    public void setDdmm(String ddmm) {
        this.ddmm = ddmm;
    }
}
