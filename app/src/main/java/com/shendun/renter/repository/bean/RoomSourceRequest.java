package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RoomSourceRequest implements Serializable {

    String cybh;
    String dwbh;
    String s_val;

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

    public String getS_val() {
        return s_val;
    }

    public void setS_val(String s_val) {
        this.s_val = s_val;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("cybh", getCybh());
            json.put("dwbh", getDwbh());
            json.put("s_val", getS_val());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
