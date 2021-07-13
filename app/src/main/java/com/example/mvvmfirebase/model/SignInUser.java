package com.example.mvvmfirebase.model;

import java.io.Serializable;

public class SignInUser implements Serializable {
    private String uId;
    private String name;
    private String email;
    private String imageUrl;
    private boolean isLogIn;

    public SignInUser(){

    }

    public SignInUser(String uId, String name, String email, String imageUrl, boolean isLogIn) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.isLogIn = isLogIn;
    }

    public SignInUser(String uId, String name, String email, String imageUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isLogIn() {
        return isLogIn;
    }

    public void setLogIn(boolean logIn) {
        isLogIn = logIn;
    }
}
