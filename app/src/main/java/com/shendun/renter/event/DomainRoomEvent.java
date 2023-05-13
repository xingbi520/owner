package com.shendun.renter.event;

/**
 * Author:xuwei
 * Time: 3:17 PM
 */
public class DomainRoomEvent {

    String domainCode;

    public DomainRoomEvent(String domainCode){
        this.domainCode = domainCode;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }
}
