package com.shendun.renter.repository.bean;

import java.io.Serializable;
import java.util.List;

public class DomainResponse implements Serializable {

    /**
     * Code : 0
     * Message : 操作成功
     * Data : {"Recordcount":"2","Currpage":"1","list":[{"fhid":"FH320602W999191226093817","fh":"302","fwid":"FW201912100001","fwmc":"某小区1","fwdz":"","dh":"55","fjlc":"2","fjlx":"单人房","zt":"2","pcode":"32060201","pname":"学田派出所","gcode":"320602","gname":"崇川区分局","dwdm":null,"jgbh":null},{"fhid":"FH320602W999191226093817","fh":"302","fwid":"FW201912100001","fwmc":"某小区1","fwdz":"","dh":"55","fjlc":"2","fjlx":"单人房","zt":"2","pcode":"32060201","pname":"学田派出所","gcode":"320602","gname":"崇川区分局","dwdm":null,"jgbh":null}]}
     */

    private String Code;
    private String Message;
    /**
     * Recordcount : 2
     * Currpage : 1
     * list : [{"fhid":"FH320602W999191226093817","fh":"302","fwid":"FW201912100001","fwmc":"某小区1","fwdz":"","dh":"55","fjlc":"2","fjlx":"单人房","zt":"2","pcode":"32060201","pname":"学田派出所","gcode":"320602","gname":"崇川区分局","dwdm":null,"jgbh":null},{"fhid":"FH320602W999191226093817","fh":"302","fwid":"FW201912100001","fwmc":"某小区1","fwdz":"","dh":"55","fjlc":"2","fjlx":"单人房","zt":"2","pcode":"32060201","pname":"学田派出所","gcode":"320602","gname":"崇川区分局","dwdm":null,"jgbh":null}]
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
        private String Recordcount;
        private String Currpage;

        private List<Domain> list;

        public String getRecordcount() {
            return Recordcount;
        }

        public void setRecordcount(String Recordcount) {
            this.Recordcount = Recordcount;
        }

        public String getCurrpage() {
            return Currpage;
        }

        public void setCurrpage(String Currpage) {
            this.Currpage = Currpage;
        }

        public List<Domain> getList() {
            return list;
        }

        public void setList(List<Domain> list) {
            this.list = list;
        }
    }
}
