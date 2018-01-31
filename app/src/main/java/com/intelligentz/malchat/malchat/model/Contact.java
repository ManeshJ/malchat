package com.intelligentz.malchat.malchat.model;

/**
 * Created by Lakshan on 2017-01-06.
 */

public class Contact {
    private String username;
    private String mobile_number;
    public Contact(String username) {
        this.username = username;
    }

    public Contact(String username, String mobile_number) {
        this.username = username;
        this.mobile_number = mobile_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}
