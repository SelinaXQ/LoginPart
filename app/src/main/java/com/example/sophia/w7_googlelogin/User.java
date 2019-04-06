package com.example.sophia.w7_googlelogin;

public class User {
    String userId;
    String userName;
    String email;
    String insName;
    String profileUrl;
    String pwd;
    String email_pwd;

    public User() {

    }

    public User(String userId, String userName, String email, String pwd, String email_pwd, String insName, String profileUrl){
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.pwd = pwd;
        this.email_pwd = email_pwd;
        this.insName = insName;
        this.profileUrl = profileUrl;
    }

    public String getEmail_pwd(){
        return email_pwd;
    }

    public String getPwd(){
        return pwd;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getInsName() {
        return insName;
    }
}
