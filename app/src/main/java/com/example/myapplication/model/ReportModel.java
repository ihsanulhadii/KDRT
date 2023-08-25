package com.example.myapplication.model;


import java.io.Serializable;

public class ReportModel implements Serializable {
    private String userId;
    private String title;
    private String img;
    private String addres;

    private String phoneMumber;
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

    public void setAddres(String addres) {this.addres = addres;}

    public void setPhoneMumber(String phoneMumber) {this.phoneMumber = phoneMumber;}

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
