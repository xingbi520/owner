package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class GetPwdResponse implements Serializable {


    /**
     * Code : 0
     * Message : 操作成功
     * Data : {"usercode":"登录用户名","password":"登录密码"}
     */

    private String Code;
    private String Message;
    /**
     * usercode : 登录用户名
     * password : 登录密码
     */

    private DataBean Data;

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean Data) {
        this.Data = Data;
    }

    public static class DataBean {
        private String usercode;
        private String password;

        public String getUsercode() {
            return usercode;
        }

        public void setUsercode(String usercode) {
            this.usercode = usercode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Override
    public String toString() {
        return "GetPwdResponse{"
                + "Code='" + Code + '\''
                + ", Message='" + Message
                + ", usercode='" + Data.getUsercode()
                + ", password='" + Data.getPassword()
                + '}';
    }
}
