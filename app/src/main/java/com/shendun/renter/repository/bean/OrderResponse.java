package com.shendun.renter.repository.bean;

import java.io.Serializable;
import java.util.List;

public class OrderResponse implements Serializable {

    /**
     * Code : 0
     * Message : 获取成功
     * Data : {"Recordcount":"总记录数","Currpage":"当前第几页","list":[{"fhid":"0001","fh":"1001"},{" fhid ":"0002","fh":"1002"},{" fhid ":"9999","fh":"9999"}]}
     */

    private String Code;
    private String Message;
    /**
     * Recordcount : 总记录数
     * Currpage : 当前第几页
     * list : [{"fhid":"0001","fh":"1001"},{" fhid ":"0002","fh":"1002"},{" fhid ":"9999","fh":"9999"}]
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
        /**
         * fhid : 0001
         * fh : 1001
         */

        private List<ListBean> list;

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

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private String jlbh;
            private String fhid;
            private String fh;
            private String dh;
            private String fjlc;
            private String fjlx;
            private String fwmc;
            private String xm;
            private String xb;
            private String mz;
            private String csrq;
            private String zjhm;
            private String xxdz;
            private String lxdh;
            private String rzsj;
            private String tfsj;
            private String zt;
            private String djsj;

            public String getFhid() {
                return fhid;
            }

            public void setFhid(String fhid) {
                this.fhid = fhid;
            }

            public String getFh() {
                return fh;
            }

            public void setFh(String fh) {
                this.fh = fh;
            }

            public String getJlbh() {
                return jlbh;
            }

            public void setJlbh(String jlbh) {
                this.jlbh = jlbh;
            }

            public String getDh() {
                return dh;
            }

            public void setDh(String dh) {
                this.dh = dh;
            }

            public String getFjlc() {
                return fjlc;
            }

            public void setFjlc(String fjlc) {
                this.fjlc = fjlc;
            }

            public String getFjlx() {
                return fjlx;
            }

            public void setFjlx(String fjlx) {
                this.fjlx = fjlx;
            }

            public String getFwmc() {
                return fwmc;
            }

            public void setFwmc(String fwmc) {
                this.fwmc = fwmc;
            }

            public String getXm() {
                return xm;
            }

            public void setXm(String xm) {
                this.xm = xm;
            }

            public String getXb() {
                return xb;
            }

            public void setXb(String xb) {
                this.xb = xb;
            }

            public String getMz() {
                return mz;
            }

            public void setMz(String mz) {
                this.mz = mz;
            }

            public String getCsrq() {
                return csrq;
            }

            public void setCsrq(String csrq) {
                this.csrq = csrq;
            }

            public String getZjhm() {
                return zjhm;
            }

            public void setZjhm(String zjhm) {
                this.zjhm = zjhm;
            }

            public String getXxdz() {
                return xxdz;
            }

            public void setXxdz(String xxdz) {
                this.xxdz = xxdz;
            }

            public String getLxdh() {
                return lxdh;
            }

            public void setLxdh(String lxdh) {
                this.lxdh = lxdh;
            }

            public String getRzsj() {
                return rzsj;
            }

            public void setRzsj(String rzsj) {
                this.rzsj = rzsj;
            }

            public String getTfsj() {
                return tfsj;
            }

            public void setTfsj(String tfsj) {
                this.tfsj = tfsj;
            }

            public String getZt() {
                return zt;
            }

            public void setZt(String zt) {
                this.zt = zt;
            }

            public String getDjsj() {
                return djsj;
            }

            public void setDjsj(String djsj) {
                this.djsj = djsj;
            }
        }
    }
}
