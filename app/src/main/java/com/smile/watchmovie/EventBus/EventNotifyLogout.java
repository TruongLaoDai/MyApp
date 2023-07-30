package com.smile.watchmovie.EventBus;

public class EventNotifyLogout {
    private boolean isLogout;

    public EventNotifyLogout(boolean isLogout) {
        this.isLogout = isLogout;
    }

    public boolean isLogout() {
        return isLogout;
    }

    public void setLogout(boolean logout) {
        isLogout = logout;
    }
}
