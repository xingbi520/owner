package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddZkRequest implements Serializable {

    private String xm;
    private String xb;
    private String mz;
    private String csrq;
    private String zjhm;
    private String xxdz;
    private String lxdh;
    private String rzsj;
    private String tfsj;
    private String fhid;
    private String dwbh;
    private String cybh;

    public void setXm(String xm) {
        this.xm = xm;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public void setXxdz(String xxdz) {
        this.xxdz = xxdz;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public void setRzsj(String rzsj) {
        this.rzsj = rzsj;
    }

    public void setTfsj(String tfsj) {
        this.tfsj = tfsj;
    }

    public void setFhid(String fhid) {
        this.fhid = fhid;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
    }

    public void setCybh(String cybh) {
        this.cybh = cybh;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("fhid", fhid);
            json.put("xm", xm);
            json.put("zjhm", zjhm);
            json.put("lxdh", lxdh);
            json.put("rzsj", rzsj);
            json.put("tfsj", tfsj);
            json.put("dwbh", dwbh);
            json.put("cybh", cybh);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
