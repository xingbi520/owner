package com.shendun.renter.event;

public class TabJumpEvent {
    private int positon;
    private boolean refreshContent;

    public TabJumpEvent(int positon) {
        this.positon = positon;
    }

    public TabJumpEvent(int positon, boolean refreshContent) {
        this.positon = positon;
        this.refreshContent = refreshContent;
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }

    public boolean isRefreshContent() {
        return refreshContent;
    }

    public void setRefreshContent(boolean refreshContent) {
        this.refreshContent = refreshContent;
    }
}
