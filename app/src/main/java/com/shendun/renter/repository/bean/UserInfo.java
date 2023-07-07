package com.shendun.renter.repository.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private String username;//用户中文全称
    private String dwbh;//所属单位编号
    private String dwmc;//所属单位名称
    private String cybh;//从业人员编号
    private String dwdz;//所属单位地址
    private String fr;    //法人姓名
    private String frsfz; //法人身份证
    private String frlxdh;//电话
    private String yyzz;  //信用代码
    private String password_zt; //密码设置是否可用，默认=0，密码设置功能不可见，=1密码设置功能可见

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDwbh() {
        return dwbh;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
    }

    public String getDwmc() {
        return dwmc;
    }

    public void setDwmc(String dwmc) {
        this.dwmc = dwmc;
    }

    public String getCybh() {
        return cybh;
    }

    public void setCybh(String cybh) {
        this.cybh = cybh;
    }

    public String getDwdz() {
        return dwdz;
    }

    public void setDwdz(String dwdz) {
        this.dwdz = dwdz;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getFrlxdh() {
        return frlxdh;
    }

    public void setFrlxdh(String frlxdh) {
        this.frlxdh = frlxdh;
    }

    public String getFrsfz() {
        return frsfz;
    }

    public void setFrsfz(String frsfz) {
        this.frsfz = frsfz;
    }

    public String getYyzz() {
        return yyzz;
    }

    public void setYyzz(String yyzz) {
        this.yyzz = yyzz;
    }

    public String getPassword_zt() {
        return password_zt;
    }

    public void setPassword_zt(String password_zt) {
        this.password_zt = password_zt;
    }

    //密码设置是否可用，默认=0，密码设置功能不可见，=1密码设置功能可见
    public boolean pwdVisible(){
        if(null != password_zt && !TextUtils.isEmpty(password_zt)
        && "1".equals(password_zt)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", dwbh=" + dwbh + ", dwmc=" + dwmc
                + ", cybh=" + cybh + "]";
    }
}
