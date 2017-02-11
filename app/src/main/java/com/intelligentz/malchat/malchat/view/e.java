package com.intelligentz.malchat.malchat.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;
import com.nineoldandroids.animation.Animator;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.w3c.dom.Text;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class e extends AbstractActivity {
    private TextView hiTxt;
    private ImageView flower;
    private Button already;
    private Button register;
    private SmsReceiver receiver;
    private Context context;
    private DialogPlus funDialog;
    private SweetAlertDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private View fun_message_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
//        hiTxt = (TextView) findViewById(R.id.looks);
//        flower = (ImageView) findViewById(R.id.flower);
//        already = (Button) findViewById(R.id.already);
//        already.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, UserNameChoosingActivity.class);
//                startActivity(intent);
//            }
//        });
//        register = (Button) findViewById(R.id.register);
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, RegisterActivity.class);
//                startActivity(intent);
//            }
//        });

        showNewUserMessage();
        //animate();
    }

    private void showNewUserMessage() {
        funDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.termsandconditions_dialog))
                .setCancelable(false)
                //.setContentBackgroundResource(R.drawable.fun_message_fragment_background)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create();
        fun_message_view = funDialog.getHolderView();
        Button regbtn = (Button) fun_message_view.findViewById(R.id.regbtn);
        Button cancelbtn = (Button) fun_message_view.findViewById(R.id.cancelBtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funDialog.dismiss();
                finish();
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funDialog.dismiss();
            }
        });
        funDialog.show();
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
