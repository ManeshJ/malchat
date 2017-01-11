package com.intelligentz.malchat.malchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;
import com.intelligentz.malchat.malchat.view.MainActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Lakshan on 2017-01-11.
 */

public class SMSReceivingService extends Service {
    private SmsReceiver receiver;
    private IntentFilter ifilter;

    @Override
    public void onCreate()
    {
        super.onCreate();

        //SMS event receiver
        registerReceiver();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerReceiver()
    {
        ifilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        // Use number higher than 999 if you want to be able to stop processing and not to put
        // auth messages into the inbox.
        ifilter.setPriority(1000);
        // Create and hold the receiver. We need to unregister on shutdown
        receiver = createReceiver();
        getApplicationContext().registerReceiver( receiver, ifilter );
        registerReceiver(receiver, ifilter);
    }
    private SmsReceiver createReceiver() {
        return new SmsReceiver(new MessageReceiver() {
            @Override
            public void onMessage(String from, String body) {
                String userMag = "From";
                final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                };
                if (from.equals("77255")){
                    SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
                    String username = mPrefs.getString("username",null);
                    if (username != null && !ReceiverApplication.isActivityVisible()) {
                        String address = "MalChat";
                        if (body.startsWith("From")) {
                            String[] substrings = body.split("\\s");
                            address  = substrings[1].substring(1);
                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < 2; i++) {
                                builder.append(substrings[i] + " ");
                            }
                            String prefix = builder.toString();
                            prefix = prefix.substring(0, prefix.length()-1);
                            body = body.replace(prefix+"\n\n","");
                        }
                        NotificationManager notificationManager = (NotificationManager)
                                getSystemService(NOTIFICATION_SERVICE);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("chatusername", address);
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                        Notification n;
                        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                          n = new Notification.Builder(getApplicationContext())
                                .setContentTitle(address)
                                .setContentText(body).setLargeIcon(largeIcon)
                                .setSmallIcon(R.drawable.flowericon)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true).
                                          setStyle(new Notification.BigTextStyle().bigText(body)).build();
                        } else {
                            n = new Notification.Builder(getApplicationContext())
                                    .setContentTitle(address)
                                    .setContentText(body).setLargeIcon(largeIcon)
                                    .setSmallIcon(R.drawable.flowericon)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true).
                                            getNotification();
                        }
                        notificationManager.notify(0, n);
                    }
                }
            }
        });
    }
}