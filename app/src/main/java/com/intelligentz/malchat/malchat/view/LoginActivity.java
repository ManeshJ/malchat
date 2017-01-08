package com.intelligentz.malchat.malchat.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;
import com.nineoldandroids.animation.Animator;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private TextView hiTxt;
    private ImageView flower;
    private TextView already;
    private ImageView register;
    private SmsReceiver receiver;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        hiTxt = (TextView) findViewById(R.id.looks);
        flower = (ImageView) findViewById(R.id.flower);
        already = (TextView) findViewById(R.id.already);
        register = (ImageView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        animate();
    }
    private void unregisterReceiver()
    {
        if (receiver == null)
            return;
        getApplicationContext().unregisterReceiver(receiver);
        receiver = null;
    }

    private void registerReceiver()
    {
        IntentFilter ifilter = new IntentFilter();
        // Use number higher than 999 if you want to be able to stop processing and not to put
        // auth messages into the inbox.
        ifilter.setPriority(1000);
        ifilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        // Create and hold the receiver. We need to unregister on shutdown
        receiver = createReceiver();
        getApplicationContext().registerReceiver( receiver, ifilter );
    }
    private SmsReceiver createReceiver() {
        return new SmsReceiver(new MessageReceiver() {
            @Override
            public void onMessage(String from, String text) {
                String successmsg = "You have successfully subscribed to MalChat";
                if (text.contains(successmsg)){
                    Intent intent = new Intent(context, UserNameChoosingActivity.class);
                    startActivity(intent);
                    unregisterReceiver();
                    finish();
                }
            }
        });
    }
    private void animate(){
        int duration = 2000;
        YoYo.with(Techniques.FadeIn)
                .duration(duration).playOn(findViewById(R.id.looks));
        YoYo.with(Techniques.FadeIn)
                .duration(duration).playOn(findViewById(R.id.flower));
        YoYo.with(Techniques.FadeIn)
                .duration(duration).playOn(findViewById(R.id.register));
        YoYo.with(Techniques.FadeIn)
                .duration(duration).playOn(findViewById(R.id.already));
    }

    private void register(){

    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
}
