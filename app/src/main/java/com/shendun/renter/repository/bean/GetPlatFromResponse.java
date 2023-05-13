package com.shendun.renter.repository.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetPlatFromResponse implements Serializable {

    @SerializedName("Recordcount")
    private String Recordcount;
    @SerializedName("Currpage")
    private String Currpage;
    @SerializedName("list")
    private List<ListDTO> list;

    public String getRecordcount() {
        return Recordcount;
    }

    public void setRecordcount(String Recordcount) {
        this.Recordcount = Recordcount;
    }

    public String getCurrpage() {
        return Currpage;
    }

    public void setCurrpage(String Currpage) {
        this.Currpage = Currpage;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        @SerializedName("code")
        private String code;
        @SerializedName("name")
        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
