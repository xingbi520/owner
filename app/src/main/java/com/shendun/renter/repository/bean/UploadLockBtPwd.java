package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UploadLockBtPwd implements Serializable {

    private String cybh;
    private String dwbh;
    private String jlbh;
    private String ctype;
    private String cvalue;
    private String zt;

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

    public String getJlbh() {
        return jlbh;
    }

    public void setJlbh(String jlbh) {
        this.jlbh = jlbh;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getCvalue() {
        return cvalue;
    }

    public void setCvalue(String cvalue) {
        this.cvalue = cvalue;
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
            json.put("cybh", cybh);
            json.put("dwbh", dwbh);
            json.put("jlbh", jlbh);
            json.put("ctype", ctype);
            json.put("cvalue", cvalue);
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
