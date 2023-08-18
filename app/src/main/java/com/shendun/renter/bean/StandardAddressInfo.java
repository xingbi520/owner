package com.shendun.renter.bean;

import com.shendun.renter.repository.bean.RoomSourceResponse;

/**
 * 标准地址信息
 */
public class StandardAddressInfo {
    private String dtype;  //"U"表示更新
    private String fhid;
    private String sam;
    private RoomSourceResponse.DataDTO dataDTO;
    private String sam_base64;

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getFhid() {
        return fhid;
    }

    public void setFhid(String fhid) {
        this.fhid = fhid;
    }

    public String getSam() {
        return sam;
    }

    public void setSam(String sam) {
        this.sam = sam;
    }

    public String getSam_base64() {
        return sam_base64;
    }

    public void setSam_base64(String sam_base64) {
        this.sam_base64 = sam_base64;
    }

    public RoomSourceResponse.DataDTO getDataDTO() {
        return dataDTO;
    }

    public void setDataDTO(RoomSourceResponse.DataDTO dataDTO) {
        this.dataDTO = dataDTO;
    }
}
