package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RoomRequest implements Serializable {

    String cybh;
    String dwbh;
    String zt;
    String xqdm;
    String s_val;
    String fjlx;
    String fhid;
    String pagesize;
    String currpage;

    public String getCybh() {
        return cybh;
    }

    public void setCybh(String cybh) {
        this.cybh = cybh;
    }

    public String getDwbh() {
        return dwbh;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getXqdm() {
        return xqdm;
    }

    public void setXqdm(String xqdm) {
        this.xqdm = xqdm;
    }

    public String getS_val() {
        return s_val;
    }

    public void setS_val(String s_val) {
        this.s_val = s_val;
    }

    public String getFjlx() {
        return fjlx;
    }

    public void setFjlx(String fjlx) {
        this.fjlx = fjlx;
    }

    public String getFhid() {
        return fhid;
    }

    public void setFhid(String fhid) {
        this.fhid = fhid;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public String getCurrpage() {
        return currpage;
    }

    public void setCurrpage(String currpage) {
        this.currpage = currpage;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("cybh", getCybh());
            json.put("dwbh", getDwbh());
            json.put("zt", getZt());
            json.put("xqdm", getXqdm());
            json.put("s_val", getS_val());
            json.put("fjlx", getFjlx());
            json.put("fhid", getFhid());
            json.put("pagesize", getPagesize());
            json.put("currpage", getCurrpage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
