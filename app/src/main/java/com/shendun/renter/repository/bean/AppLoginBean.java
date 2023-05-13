package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class AppLoginBean implements Serializable {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private String phone;
    private int isNeedVerfyCode;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getIsNeedVerfyCode() {
        return isNeedVerfyCode;
    }

    public void setIsNeedVerfyCode(int isNeedVerfyCode) {
        this.isNeedVerfyCode = isNeedVerfyCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 是否需要图形验证码
     * @return 结果
     */
    public boolean isNeedVerfyCode(){
        return 1==this.isNeedVerfyCode;
    }

    @Override
    public String toString() {
        return "AppLoginBean{"
            + "access_token='"
            + access_token
            + '\''
            + ", token_type='"
            + token_type
            + '\''
            + ", refresh_token='"
            + refresh_token
            + '\''
            + ", expires_in="
            + expires_in
            + ", scope='"
            + scope
            + '\''
            + ", phone='"
            + phone
            + '\''
            + ", isNeedVerfyCode="
            + isNeedVerfyCode
            + '}';
    }
}
