package com.intelligentz.malchat.malchat.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class e extends AbstractActivity {
    private TextView hiTxt;
    private ImageView flower;
    private Button already;
    private Button register;
    private SmsReceiver receiver;
    private Context context;
    private SweetAlertDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        hiTxt = (TextView) findViewById(R.id.looks);
        flower = (ImageView) findViewById(R.id.flower);
        already = (Button) findViewById(R.id.already);
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, n.class);
                startActivity(intent);
            }
        });
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, i.class);
                startActivity(intent);
            }
        });
        animate();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
