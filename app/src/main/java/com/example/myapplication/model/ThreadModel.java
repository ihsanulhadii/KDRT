package com.example.myapplication.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class ThreadModel implements Serializable {
    private String userId;
    private String title;
    private String img;
    private String chronology ;

    private String id;

    public HashMap<String, Object> getDate() {
        return date;
    }

    public void setDate(HashMap<String, Object> date) {
        this.date = date;
    }

    private HashMap<String, Object> date;

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

    public String getChronology() {return chronology;}

    public void setChronology(String chronology) {
        this.chronology = chronology;
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
