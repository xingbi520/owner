package com.shendun.renter.repository.bean;

import java.io.Serializable;
import java.util.List;

public class FaceAuthResponse implements Serializable {

    /**
     * Code : 0
     * Message : 获取成功
     */

    private String Code;
    private String Message;

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
}
