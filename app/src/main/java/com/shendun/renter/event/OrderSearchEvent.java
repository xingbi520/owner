package com.shendun.renter.event;

/**
 * Author:xuwei
 * Time: 3:17 PM
 */
public class OrderSearchEvent {

    String keyWord;

    public OrderSearchEvent(String keyWord){
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
