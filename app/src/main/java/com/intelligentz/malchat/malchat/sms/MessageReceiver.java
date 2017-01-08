package com.intelligentz.malchat.malchat.sms;

/**
 * Created by Lakshan on 2017-01-09.
 */

public interface MessageReceiver {
    void onMessage(String from, String text);
}
