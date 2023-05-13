package com.shendun.renter.repository.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddV2ZkRequest implements Serializable {

    private String ptype;
    private String stype;
    private String jlbh;
    private String lkbh;
    private String xm;
    private String xb;
    private String mz;
    private String csrq;
    private String zjhm;
    private String xxdz;
    private String lxdh;
    private String rzsj;
    private String tfsj;
    private String fhid;
    private String zklx;
    private String dwbh;
    private String cybh;
    private String ddlypttydm;
    private String ddptbh;
    private String pic;
    private String g_is_minor;
    private String g_is_baby;
    private String g_guardian_relationship;
    private String g_guardian_phone;
    private String g_guardian_name;
    private String g_guardian_idCard;
    private String g_guardian_happening;
    private String g_is_with;
    private String g_with_relationship;
    private String g_with_happening;
    private String g_is_injured;
    private String g_suspicious_descrip;
    private String g_is_patrol_erro;
    private String g_patrol_happening;
    private String bz;

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getJlbh() {
        return jlbh;
    }

    public void setJlbh(String jlbh) {
        this.jlbh = jlbh;
    }

    public String getLkbh() {
        return lkbh;
    }

    public void setLkbh(String lkbh) {
        this.lkbh = lkbh;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public void setXxdz(String xxdz) {
        this.xxdz = xxdz;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public void setRzsj(String rzsj) {
        this.rzsj = rzsj;
    }

    public void setTfsj(String tfsj) {
        this.tfsj = tfsj;
    }

    public void setFhid(String fhid) {
        this.fhid = fhid;
    }

    public void setZklx(String zklx) {
        this.zklx = zklx;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
    }

    public void setCybh(String cybh) {
        this.cybh = cybh;
    }

    public String getDdlypttydm() {
        return ddlypttydm;
    }

    public void setDdlypttydm(String ddlypttydm) {
        this.ddlypttydm = ddlypttydm;
    }

    public String getDdptbh() {
        return ddptbh;
    }

    public void setDdptbh(String ddptbh) {
        this.ddptbh = ddptbh;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getG_is_minor() {
        return g_is_minor;
    }

    public void setG_is_minor(String g_is_minor) {
        this.g_is_minor = g_is_minor;
    }

    public String getG_is_baby() {
        return g_is_baby;
    }

    public void setG_is_baby(String g_is_baby) {
        this.g_is_baby = g_is_baby;
    }

    public String getG_guardian_relationship() {
        return g_guardian_relationship;
    }

    public void setG_guardian_relationship(String g_guardian_relationship) {
        this.g_guardian_relationship = g_guardian_relationship;
    }

    public String getG_guardian_phone() {
        return g_guardian_phone;
    }

    public void setG_guardian_phone(String g_guardian_phone) {
        this.g_guardian_phone = g_guardian_phone;
    }

    public String getG_guardian_name() {
        return g_guardian_name;
    }

    public void setG_guardian_name(String g_guardian_name) {
        this.g_guardian_name = g_guardian_name;
    }

    public String getG_guardian_idCard() {
        return g_guardian_idCard;
    }

    public void setG_guardian_idCard(String g_guardian_idCard) {
        this.g_guardian_idCard = g_guardian_idCard;
    }

    public String getG_guardian_happening() {
        return g_guardian_happening;
    }

    public void setG_guardian_happening(String g_guardian_happening) {
        this.g_guardian_happening = g_guardian_happening;
    }

    public String getG_is_with() {
        return g_is_with;
    }

    public void setG_is_with(String g_is_with) {
        this.g_is_with = g_is_with;
    }

    public String getG_with_relationship() {
        return g_with_relationship;
    }

    public void setG_with_relationship(String g_with_relationship) {
        this.g_with_relationship = g_with_relationship;
    }

    public String getG_with_happening() {
        return g_with_happening;
    }

    public void setG_with_happening(String g_with_happening) {
        this.g_with_happening = g_with_happening;
    }

    public String getG_is_injured() {
        return g_is_injured;
    }

    public void setG_is_injured(String g_is_injured) {
        this.g_is_injured = g_is_injured;
    }

    public String getG_suspicious_descrip() {
        return g_suspicious_descrip;
    }

    public void setG_suspicious_descrip(String g_suspicious_descrip) {
        this.g_suspicious_descrip = g_suspicious_descrip;
    }

    public String getG_is_patrol_erro() {
        return g_is_patrol_erro;
    }

    public void setG_is_patrol_erro(String g_is_patrol_erro) {
        this.g_is_patrol_erro = g_is_patrol_erro;
    }

    public String getG_patrol_happening() {
        return g_patrol_happening;
    }

    public void setG_patrol_happening(String g_patrol_happening) {
        this.g_patrol_happening = g_patrol_happening;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    protected String getJsonStr() {
        JSONObject json = new JSONObject();
        try {
            json.put("ptype", ptype);
            json.put("stype", stype);
            json.put("jlbh", jlbh);
            json.put("lkbh", lkbh);
            json.put("xm", xm);
            json.put("xb", xb);
            json.put("mz", mz);
            json.put("fhid", fhid);
            json.put("zklx", zklx);
            json.put("zjhm", zjhm);
            json.put("lxdh", lxdh);
            json.put("rzsj", rzsj);
            json.put("tfsj", tfsj);
            json.put("dwbh", dwbh);
            json.put("cybh", cybh);
            json.put("ddlypttydm", ddlypttydm);
            json.put("ddptbh", ddptbh);
            json.put("pic", pic);
            json.put("g_is_minor", g_is_minor);
            json.put("g_is_baby", g_is_baby);
            json.put("g_guardian_relationship", g_guardian_relationship);
            json.put("g_guardian_phone", g_guardian_phone);
            json.put("g_guardian_name", g_guardian_name);
            json.put("g_guardian_idCard", g_guardian_idCard);
            json.put("g_guardian_happening", g_guardian_happening);
            json.put("g_is_with", g_is_with);
            json.put("g_with_relationship", g_with_relationship);
            json.put("g_with_happening", g_with_happening);
            json.put("g_is_injured", g_is_injured);
            json.put("g_suspicious_descrip", g_suspicious_descrip);
            json.put("g_is_patrol_erro", g_is_patrol_erro);
            json.put("g_patrol_happening", g_patrol_happening);
            json.put("bz", bz);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
