package com.example.myapplication.model;


import java.io.Serializable;

public class ThreadModel implements Serializable {
    private String userId;
    private String title;
    private String img;
    private String kronologisingkat;
    private String keseluruhan;
    private String id;

  //  private Date date;

   /* public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }*/



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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getKronologisingkat() {
        return kronologisingkat;
    }

    public void setKronologisingkat(String kronologisingkat) {
        this.kronologisingkat = kronologisingkat;
    }

    public String getKeseluruhan() {
        return keseluruhan;
    }

    public void setKeseluruhan(String keseluruhan) {
        this.keseluruhan = keseluruhan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
