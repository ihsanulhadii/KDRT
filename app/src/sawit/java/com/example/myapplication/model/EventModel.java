package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class EventModel implements Serializable {

    private String title ;

    private String img ;

    private String link;


    private String tanggal ;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }



    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    private Long commentCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;



    public HashMap<String, Object> getDate() {
        return date;
    }

    public void setDate(HashMap<String, Object> date) {
        this.date = date;
    }

    private HashMap<String, Object> date;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublish() {
        return isPublish;
    }

    public void setPublish(Boolean publish) {
        isPublish = publish;
    }

    private String content;

    private Boolean isPublish;

    public Date getDateValue(String key) {
        if (date != null && date.containsKey(key)) {
            Object value = date.get(key);
            if (value instanceof Date) {
                return (Date) value;
            } else if (value instanceof com.google.firebase.Timestamp) {
                return ((com.google.firebase.Timestamp) value).toDate();
            }
        }
        return null;
    }


}
