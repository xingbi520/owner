package com.shendun.renter.repository.bean;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class StandardAddressRequest implements Serializable {
    private String dtype;
    private String fhid;
    private String sam;
    private String st_dzysid;
    private String xzb;
    private String yzb;
    private String dzxx;
    private String zrqdm;
    private String zrqmc;
    private String pcsdm;
    private String pcsmc;
    private String fjdm;
    private String fjmc;
    private String sam_base64;

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getFhid() {
        return fhid;
    }

    public void setFhid(String fhid) {
        this.fhid = fhid;
    }

    public String getSam() {
        return sam;
    }

    public void setSam(String sam) {
        this.sam = sam;
    }

    public String getSt_dzysid() {
        return st_dzysid;
    }

    public void setSt_dzysid(String st_dzysid) {
        this.st_dzysid = st_dzysid;
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

    public String getSam_base64() {
        return sam_base64;
    }

    public void setSam_base64(String sam_base64) {
        this.sam_base64 = sam_base64;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("dtype", dtype);
            json.put("fhid", fhid);
            json.put("sam", sam);
            json.put("st_dzysid", st_dzysid);
            json.put("xzb", xzb);
            json.put("yzb", yzb);
            json.put("dzxx", dzxx);
            json.put("zrqdm", zrqdm);
            json.put("zrqmc", zrqmc);
            json.put("pcsdm", pcsdm);
            json.put("pcsmc", pcsmc);
            json.put("fjdm", fjdm);
            json.put("fjmc", fjmc);
            json.put("sam_base64", sam_base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
