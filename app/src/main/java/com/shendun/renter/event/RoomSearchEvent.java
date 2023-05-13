package com.shendun.renter.event;

/**
 * Author:xuwei
 * Time: 3:17 PM
 */
public class RoomSearchEvent {

    String keyWord;

    public RoomSearchEvent(String keyWord){
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
