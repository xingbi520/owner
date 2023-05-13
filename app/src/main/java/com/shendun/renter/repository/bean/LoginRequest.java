package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginRequest implements Serializable {

    private String tel;
    private String password;
    private String unicode;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("usercode", getTel());
            json.put("password", getPassword());
            json.put("imei", getUnicode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
