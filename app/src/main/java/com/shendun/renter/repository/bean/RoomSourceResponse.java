package com.shendun.renter.repository.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomSourceResponse implements Serializable {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataDTO data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO implements Serializable{
        @SerializedName("st_dzysid")
        private String stDzysid;
        @SerializedName("xzb")
        private String xzb;
        @SerializedName("yzb")
        private String yzb;
        @SerializedName("dzxx")
        private String dzxx;
        @SerializedName("zrqdm")
        private String zrqdm;
        @SerializedName("zrqmc")
        private String zrqmc;
        @SerializedName("pcsdm")
        private String pcsdm;
        @SerializedName("pcsmc")
        private String pcsmc;
        @SerializedName("fjdm")
        private String fjdm;
        @SerializedName("fjmc")
        private String fjmc;

        public String getStDzysid() {
            return stDzysid;
        }

        public void setStDzysid(String stDzysid) {
            this.stDzysid = stDzysid;
        }

        public String getXzb() {
            return xzb;
        }

        public void setXzb(String xzb) {
            this.xzb = xzb;
        }

        public String getYzb() {
            return yzb;
        }

        public void setYzb(String yzb) {
            this.yzb = yzb;
        }

        public String getDzxx() {
            return dzxx;
        }

        public void setDzxx(String dzxx) {
            this.dzxx = dzxx;
        }

        public String getZrqdm() {
            return zrqdm;
        }

        public void setZrqdm(String zrqdm) {
            this.zrqdm = zrqdm;
        }

        public String getZrqmc() {
            return zrqmc;
        }

        public void setZrqmc(String zrqmc) {
            this.zrqmc = zrqmc;
        }

        public String getPcsdm() {
            return pcsdm;
        }

        public void setPcsdm(String pcsdm) {
            this.pcsdm = pcsdm;
        }

        public String getPcsmc() {
            return pcsmc;
        }

        public void setPcsmc(String pcsmc) {
            this.pcsmc = pcsmc;
        }

        public String getFjdm() {
            return fjdm;
        }

        public void setFjdm(String fjdm) {
            this.fjdm = fjdm;
        }

        public String getFjmc() {
            return fjmc;
        }

        public void setFjmc(String fjmc) {
            this.fjmc = fjmc;
        }
    }
}
