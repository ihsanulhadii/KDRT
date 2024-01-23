package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class VideoModel implements Serializable {

    private String title ;
    private String id;
    private Boolean isPublish;
    private HashMap<String, Object> date;
    private String embed;

    public String getEmbed() {return embed;}

    public void setEmbed(String embed) {this.embed = embed;}



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public HashMap<String, Object> getDate() {
        return date;
    }

    public void setDate(HashMap<String, Object> date) {
        this.date = date;
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public Boolean getPublish() {
        return isPublish;
    }

    public void setPublish(Boolean publish) {
        isPublish = publish;
    }




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
