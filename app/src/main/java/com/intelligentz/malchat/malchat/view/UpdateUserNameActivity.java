package com.intelligentz.malchat.malchat.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateUserNameActivity extends AbstractActivity {
    private Button saveBtn;
    private SmsReceiver receiver;
    String username;
    private SweetAlertDialog progressDialog;
    private Context context;
    private EditText usernameTxt;
    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        context = this;
        saveBtn = (Button) findViewById(R.id.saveBtn);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserName();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void saveUserName() {
        username = usernameTxt.getText().toString().trim();
        if (username.isEmpty()) return;;
        final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        };
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Updating...");
        progressDialog.setContentText("This may take a few seconds");
        progressDialog.getProgressHelper().setRimColor(R.color.colorPrimary);
        progressDialog.show();
        final String SENT_ACTION = "com.intelligentz.malchat.upsent";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT_ACTION), 0);
        broadcastReceiver = new BroadcastReceiver()
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
                                .setContentText("There was an error registering username. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering username. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering username. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error registering username. Please try again.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver
                , new IntentFilter(SENT_ACTION));
        registerReceiver();
        String msg = "Mal up "+username;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("77255", null, msg, sentPI, null);
        } catch (Exception ex) {
            progressDialog.setTitleText("Failed!")
                    .setContentText("There was an error registering your username. Please try again.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(successListener)
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }

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
                String successmsg = "Congratulations! Oyage aluth username eka thamai";
                String alreadyMsg = "Oops! Username already exist";
                final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                };
                if (from.equals("77255")){
                    if (text.contains(successmsg)) {
                        SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("username",username);
                        editor.commit();
                        MainActivity.username = username;
                        unregisterReceiver();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismissWithAnimation();
                        }
                        finish();
                    } else if (text.contains(alreadyMsg)) {
                        progressDialog.setTitleText("Failed!")
                                .setContentText("Someone is already using that username. Please try another one.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        unregisterReceiver();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
