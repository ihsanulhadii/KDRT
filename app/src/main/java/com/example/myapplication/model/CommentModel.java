package com.example.myapplication.model;

import com.google.firebase.Timestamp;

public class CommentModel {



    private String userId;
    private String content;
    private Timestamp time;

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

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



}
