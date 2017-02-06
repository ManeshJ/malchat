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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class i extends AppCompatActivity {
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
        setContentView(R.layout.activity_register);
        context = this;
        hiTxt = (TextView) findViewById(R.id.looks);
        flower = (ImageView) findViewById(R.id.flower);
        already = (Button) findViewById(R.id.already);
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        register = (Button) findViewById(R.id.register);
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
        IntentFilter ifilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        // Use number higher than 999 if you want to be able to stop processing and not to put
        // auth messages into the inbox.
        ifilter.setPriority(1000);
        // Create and hold the receiver. We need to unregister on shutdown
        receiver = createReceiver();
        getApplicationContext().registerReceiver( receiver, ifilter );
    }
    private SmsReceiver createReceiver() {
        return new SmsReceiver(new MessageReceiver() {
            @Override
            public void onMessage(String from, String text) {
                String successmsg = "You have successfully subscribed to MalChat";
                String alreadyMsg = "You are already registered to the MalChat";
                final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                };
                if (from.equals("ideamart") && text.contains(successmsg)){
                    Intent intent = new Intent(context, h.class);
                    startActivity(intent);
                    if (progressDialog.isShowing()) {
                        progressDialog.dismissWithAnimation();
                    }
                    unregisterReceiver();
                    finish();
                } else if (from.equals("ideamart") && text.contains(alreadyMsg)){
                    progressDialog.setTitleText("Failed!")
                            .setContentText("Oops! You are already registered to MalChat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(successListener)
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    unregisterReceiver();
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
        final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        };
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Registering...");
        progressDialog.setContentText("This may take a few seconds");
        progressDialog.getProgressHelper().setRimColor(R.color.colorPrimary);
        progressDialog.show();
        final String SENT_ACTION = "com.intelligentz.malchat.regsent";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT_ACTION), 0);
        broadcastReceiver  = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0,Intent arg1)
            {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        progressDialog.setTitleText("Confirming...")
                                .setContentText("Waiting for confirmation.")
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering you to MalChat. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering you to MalChat. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering you to MalChat. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering you to MalChat. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(SENT_ACTION));
        registerReceiver();
        String msg = "Reg Mal";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("77255", null, msg, sentPI, null);
        } catch (Exception ex) {
            progressDialog.setTitleText("Failed!")
                    .setContentText("There was an error registering you to MalChat. Please try again.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(successListener)
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }



    }

}
