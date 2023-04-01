package com.smile.watchmovie.model;

public class User {
    private int iD;
    private byte[] avatar;
    private String name;
    private String username;
    private String password;

    public User() {
    }

    public User(int iD, byte[] avatar, String name, String username, String password) {
        this.iD = iD;
        this.avatar = avatar;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public int getiD() {
        return iD;
    }

    public void setiD(int iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
