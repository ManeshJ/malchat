package com.intelligentz.malchat.malchat.model;

/**
 * Created by Lakshan on 2017-01-07.
 */

public class ChatMessage {
    int type;
    String address;
    String body;
    String date;

    public ChatMessage() {
    }

    public ChatMessage(int type, String address, String body, String date) {
        this.type = type;
        this.address = address;
        this.body = body;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
