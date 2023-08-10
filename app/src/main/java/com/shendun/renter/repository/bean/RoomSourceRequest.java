package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RoomSourceRequest implements Serializable {

    String stdzysid;

    public String getStdzysid() {
        return stdzysid;
    }

    public void setStdzysid(String stdzysid) {
        this.stdzysid = stdzysid;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("stdzysid", stdzysid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
