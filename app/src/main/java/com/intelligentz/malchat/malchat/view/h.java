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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.sms.MessageReceiver;
import com.intelligentz.malchat.malchat.sms.SmsReceiver;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class h extends AbstractActivity {
    private Button saveBtn;
    private Button cancelBtn;
    private SmsReceiver receiver;
    String username;
    private SweetAlertDialog progressDialog;
    private Context context;
    private EditText usernameTxt;
    private BroadcastReceiver broadcastReceiver;
    private DialogPlus funDialog;
    private View fun_message_view;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_name);
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
        showNewUserMessage();
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
        Button cancelbtn = (Button) fun_message_view.findViewById(R.id.cancelbtn);
        checkBox = (CheckBox) fun_message_view.findViewById(R.id.check);
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
                if (checkBox.isChecked()) {
                    register();
                    funDialog.dismiss();
                } else {
                    Toast toast = Toast.makeText(context, "Please read and accept Terms & Conditions", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        funDialog.show();
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
        String msg = "Mal un "+username;
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
                        Intent intent = new Intent(context, f.class);
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
                                .setContentText("Oops! Username you entered already exists. Please enter a different one.")
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
                                Intent intent = new Intent(context, f.class);
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
                        username = words[words.length-4].trim();
                        username = username.substring(0,username.length()-1);
                        progressDialog.setTitleText("You already have a username")
                                .setContentText("Your username is "+ username + ".")
                                .setConfirmText("OK")
                                .setConfirmClickListener(haveunlistener)
                                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }

                }
            }
        });
    }

//===================================================================================================

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
        registerRegisterReceiver();
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

    private void registerRegisterReceiver() {
        IntentFilter ifilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        // Use number higher than 999 if you want to be able to stop processing and not to put
        // auth messages into the inbox.
        ifilter.setPriority(1000);
        // Create and hold the receiver. We need to unregister on shutdown
        receiver = createRegisterReceiver();
        getApplicationContext().registerReceiver( receiver, ifilter );
    }
    private SmsReceiver createRegisterReceiver() {
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

                    if (progressDialog.isShowing()) {
                        progressDialog.setTitleText("Success!")
                                .setContentText("You successfully registered with MalChat")
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                    unregisterReceiver();
                } else if (from.equals("ideamart") && text.contains(alreadyMsg)){
                    Intent intent = new Intent(context, n.class);
                    startActivity(intent);
                    unregisterReceiver();
                    finish();
                }
            }
        });
    }
}
