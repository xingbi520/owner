package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/*
 * 通过该接口获取密码（暂时适用神盾门锁）
 */
public class GetLockPwd implements Serializable {

    private String ptype;
    private String jlbh;
    private String xm;
    private String zjhm;
    private String zt;

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getJlbh() {
        return jlbh;
    }

    public void setJlbh(String jlbh) {
        this.jlbh = jlbh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("ptype", ptype);
            json.put("jlbh", jlbh);
            json.put("xm", xm);
            json.put("zjhm", zjhm);
            json.put("zt", zt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
