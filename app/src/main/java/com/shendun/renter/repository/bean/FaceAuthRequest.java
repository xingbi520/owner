package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FaceAuthRequest implements Serializable {

    private String tel;
    private String txtel;
    private String jlbh;
    private String xm;
    private String zjhm;
    private String zklx;
    private String pic;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getJlbh() {
        return jlbh;
    }

    public void setJlbh(String jlbh) {
        this.jlbh = jlbh;
    }

    public String getTxtel() {
        return txtel;
    }

    public void setTxtel(String txtel) {
        this.txtel = txtel;
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

    public String getZklx() {
        return zklx;
    }

    public void setZklx(String zklx) {
        this.zklx = zklx;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("tel", getTel());
            json.put("txtel", getTxtel());
            json.put("jlbh", getJlbh());
            json.put("xm", getXm());
            json.put("zjhm", getZjhm());
            json.put("zklx", getZklx());
            json.put("pic", getPic());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
