package com.example.myapplication.model;

import java.io.Serializable;

public class ArtikelModel implements Serializable {

    private String title ;

    private String img ;

    private String description ;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
