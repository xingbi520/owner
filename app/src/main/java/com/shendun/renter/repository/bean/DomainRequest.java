package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class DomainRequest implements Serializable {

    String dwbh;
    String pagesize;
    String currpage;

    public String getDwbh() {
        return dwbh;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
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
            json.put("dwbh", getDwbh());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
