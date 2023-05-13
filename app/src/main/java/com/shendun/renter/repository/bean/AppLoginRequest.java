package com.shendun.renter.repository.bean;

import com.shendun.renter.config.EnvKeyConfig;
import com.shendun.renter.utils.AESBase64Util;
import org.json.JSONException;
import org.json.JSONObject;

public class AppLoginRequest extends BaseRequest {

    private String clientId;
    private String phone;
    private String password;
    private String uuid;
    private String verifyCode;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    protected String key() {
        return "AppLoginV2";
    }

    @Override
    protected JSONObject bodyObject() throws JSONException {
        String param = "";
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("phone", phone);
            paramObject.put("clientSecret",  EnvKeyConfig.AES_KEY);
            paramObject.put("password", password);
            paramObject.put("uuid", uuid);
            paramObject.put("verifyCode", verifyCode);
            param = AESBase64Util.encrypt(paramObject.toString(), EnvKeyConfig.AES_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //构建body
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientId", clientId);
        jsonObject.put("param", param);

        return jsonObject;
    }
}
