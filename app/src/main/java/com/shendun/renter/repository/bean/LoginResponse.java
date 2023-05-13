package com.shendun.renter.repository.bean;

import java.io.Serializable;

public class LoginResponse implements Serializable {

    /**
     * Code : 0
     * Message : 用户登录成功！
     * Data : {"username":"曹益达","dwbh":"JG320620191203000001","dwmc":"网约房","cybh":"CY320620200212000002"}
     */

    private String Code;
    private String Message;
    /**
     * username : 曹益达
     * dwbh : JG320620191203000001
     * dwmc : 网约房
     * cybh : CY320620200212000002
     */

    private UserInfo Data;

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

    public UserInfo getData() {
        return Data;
    }

    public void setData(UserInfo Data) {
        this.Data = Data;
    }
}
