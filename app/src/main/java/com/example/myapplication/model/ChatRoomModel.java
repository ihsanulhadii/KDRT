package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class ChatRoomModel implements Serializable {

    private String id ;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private String sender ;

    private String receiver;


}
