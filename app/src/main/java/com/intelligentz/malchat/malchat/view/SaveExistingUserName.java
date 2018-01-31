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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SaveExistingUserName extends AbstractActivity {
    private Button saveBtn;
    String username;
    Button cancelBtn;
    private SmsReceiver receiver;
    private SweetAlertDialog progressDialog;
    private Context context;
    private EditText usernameTxt;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_existing_user_name);
        context = this;
        saveBtn = (Button) findViewById(R.id.saveBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserName();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void savegivenUserName() {
        username = usernameTxt.getText().toString().trim();
        SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("username",username);
        editor.commit();
        Intent intent = new Intent(context, InviteSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username",username);
        intent.putExtra("newuser",true);
        startActivity(intent);
        finish();
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
        progressDialog.setTitleText("Verifying...");
        progressDialog.setContentText("This may take a few seconds");
        progressDialog.getProgressHelper().setRimColor(R.color.colorPrimary);
        progressDialog.show();
        final String SENT_ACTION = "com.intelligentz.malchat.unsent";
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
        String msg = "Mal un "+username.trim();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("77255", null, msg, sentPI, null);
        } catch (Exception ex) {
            progressDialog.setTitleText("Failed!")
                    .setContentText("There was an error saving your username. Please try again.")
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
                String successmsg = "Congratulations! Oyage username eka thamai";
                String alreadyMsg = "Oya daapu username eka thawa ekkenek kalinma";
                String haveunMsg = "Hi Yaluwa, oyata username ekak denatamath thiyenawa";
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
                        Intent intent = new Intent(context, InviteSelectionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("username",username);
                        intent.putExtra("newuser",true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        unregisterReceiver();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismissWithAnimation();
                        }
                        finish();
                    } else if (text.contains(alreadyMsg)) {
                        progressDialog.setTitleText("Failed!")
                                .setContentText("Oops! Someone else uses the username you entered. Please enter a different one.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        unregisterReceiver();
                    } else if (text.contains(haveunMsg)) {
                        final SweetAlertDialog.OnSweetClickListener haveunlistener = new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putString("username",username);
                                editor.commit();
                                Intent intent = new Intent(context, InviteSelectionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("username",username);
                                intent.putExtra("newuser",true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                unregisterReceiver();
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        };
                        String[] words = text.split("\\s");
                        String newusername = words[words.length-4].trim();
                        newusername = newusername.substring(0, newusername.length()-1);
                        if (username.trim().equals(newusername)) {
                            progressDialog.setTitleText("Username Successfully Verified!")
                                    .setContentText("Your username is "+ username + ".")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(haveunlistener)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        } else {
                            username = newusername;
                            progressDialog.setTitleText("Invalid username entered!")
                                    .setContentText("Your username is "+ newusername + ".")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(haveunlistener)
                                    .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        }

                    }

                }
            }
        });
    }
}
