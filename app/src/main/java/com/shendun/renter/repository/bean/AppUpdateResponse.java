package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class AppUpdateResponse implements Serializable {
    /**
     * versionCode  : 这个值是我发布版本时候给你的
     * versionName  : 这个值是我发布版本时候给你的
     * downloadUrl  : 下载地址
     * createTime : apk放服务器上的时间
     */

    private String versionCode;
    private String versionName;
    private String downloadUrl;
    private String createTime;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
