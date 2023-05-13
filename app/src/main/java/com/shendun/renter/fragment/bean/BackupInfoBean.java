package com.shendun.renter.fragment.bean;

import java.io.Serializable;
import java.util.List;

public class BackupInfoBean implements Serializable {
    /**
     * fullName : BellKate
     * note :
     * phoneNum : ["5555648583","4155553695"]
     */

    private String fullName;
    private String note;
    private List<String> phoneNum;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(List<String> phoneNum) {
        this.phoneNum = phoneNum;
    }
}
