package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {

    /**
     * 接口通用状态码——成功
     */
    public static final String RESULT_STATUS_SUCCESS = "200";
    /**
     * 具体业务状态码——成功
     */
    public static final String RESULT_RSP_CODE_SUCCESS = "0000";

    /**
     * 无效的token
     */
    public static final String RESULT_RSP_CODE_INVALID_TOKEN = "1001";

    /**
     * 当前家庭已解散
     */
    public static final String RESULT_RSP_CODE_FAMILY_DISMISS = "10004";

    /**
     * 您已被移出该家庭
     */
    public static final String RESULT_RSP_CODE_REMOVED_FAMILY = "10002";

    private RSPBean<T> RSP;
    private String STATUS;
    private String MSG;

    public String getSTATUS() {
        return STATUS;
    }

    public String getMSG() {
        return MSG;
    }

    public RSPBean getRSP() {
        return RSP;
    }

    public void setRSP(RSPBean RSP) {
        this.RSP = RSP;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }

    public static class RSPBean<T> {
        /**
         * RSP_CODE : 0000
         * RSP_DESC : 成功
         * DATA : {"pageVersion":"1.0.0","pageName":"新版首页","floors":[{"floorId":"1","floorName":"特色业务","floorType":1,"floorSerial":1,"contents":[{"contentId":"1","contentName":"沃家固话","contentPic":"value","contentLinkType":"0","contentLinkPath":"https://www.xxxxx.com","contentSerial":1}]}]}
         */

        private String RSP_CODE;
        private String RSP_DESC;
        private T DATA;

        public String getRSP_CODE() {
            return RSP_CODE;
        }

        public void setRSP_CODE(String RSP_CODE) {
            this.RSP_CODE = RSP_CODE;
        }

        public String getRSP_DESC() {
            return RSP_DESC;
        }

        public void setRSP_DESC(String RSP_DESC) {
            this.RSP_DESC = RSP_DESC;
        }

        public T getDATA() {
            return DATA;
        }

        public void setDATA(T DATA) {
            this.DATA = DATA;
        }
    }

    public boolean isSuccess() {
        return RESULT_STATUS_SUCCESS.equals(STATUS)
            && null != RSP
            && RESULT_RSP_CODE_SUCCESS.equals(RSP.getRSP_CODE());
    }
}