package com.intelligentz.malchat.malchat.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Lakshan on 2017-01-09.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_EXTRA_NAME = "pdus";
    private final MessageReceiver messageReceiver;
    public SmsReceiver(MessageReceiver messageReceiver)
    {
        if ( messageReceiver == null )
            throw new IllegalArgumentException("messageReceiver");
        this.messageReceiver = messageReceiver;
    }

    @Override
    /**
     * Method that handles SMSs.
     * It iterates over all the messages and forward each method to IMessageReceiver object.
     */
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();
                messageReceiver.onMessage(address, body);
            }
        }
        // Uncomment this if you do not want the SMS put into the inbox (for priority > 999)
         this.abortBroadcast();
    }
}