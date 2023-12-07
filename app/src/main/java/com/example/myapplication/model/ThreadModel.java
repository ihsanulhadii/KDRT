package com.example.myapplication.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Map;

public class ThreadModel implements Serializable {
    private String userId;
    private String title;
    private String img;
    private String description ;
    private String id;
    private String name;
    private String avatar;
    private Timestamp createdDate;

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }



    public ThreadModel(){

    }


    public ThreadModel(String id,String title, String userId, String description, String img, Timestamp createdDate, String name, String avatar) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.description = description;
        this.img = img;
        this.name = name;
        this.createdDate = createdDate;
        this.avatar = avatar;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


}
