package com.example.myapplication.model;

import com.google.firebase.Timestamp;

public class CommentModel {



    private String userId;
    private String content;
    private Timestamp time;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String name;
    private String avatar;


    public CommentModel() {
        // Diperlukan oleh Firebase untuk deserialisasi objek
    }

    public CommentModel(String userId, String content,String name,String avatar,Timestamp time) {
        this.userId = userId;
        this.content = content;
        this.name = name;
        this.time = time;
        this.avatar = avatar;

    }



}
