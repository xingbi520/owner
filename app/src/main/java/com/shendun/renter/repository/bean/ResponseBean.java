package com.shendun.renter.repository.bean;

/**
 * Created by xw
 * on 16-5-23.
 */
public class ResponseBean<T> {

    private String Code;
    private String Message;
    private T Data;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSuccessful(){
        if (null != Code && "0".equals(Code)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "code:" + Code
                + " + message:" + Message
                + " + result:" + (Data != null ? Data.toString() : null);
    }
}
