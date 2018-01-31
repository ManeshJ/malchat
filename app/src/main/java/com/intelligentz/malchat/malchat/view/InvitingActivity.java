package com.intelligentz.malchat.malchat.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.ContactRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.Contact;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InvitingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACT = 123;
    private static final int MAX_PICK_CONTACT = 10;

    private Context context;
    private String username;
    private int sentCount = 0;
    private ArrayList<Contact> contactList;
    private ArrayList<Contact> selectedContactList;
    private RecyclerView recyclerView;
    private ContactRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager contactlayoutManager;
    private MaterialFancyButton invite_btn;
    private TextView selected_number_lbl;
    private SweetAlertDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviting);
        this.context = this;
        this.username = getIntent().getStringExtra("username");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        invite_btn = (MaterialFancyButton) findViewById(R.id.send_btn);
        selected_number_lbl = (TextView) findViewById(R.id.selectednumber_lbl);
        new RetrieveContacts().execute();
        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactList = recyclerAdaptor.getSelectedContactList();
                sendSMS();
            }
        });
    }

    private void getContactList(){
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
        contactList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
                Cursor phones = this.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int itype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                    final boolean isMobile =
                            itype == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ||
                                    itype == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE||
                                        itype == ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                    if (isMobile) this.contactList.add(new Contact(name,phoneNumber));
                }
                phones.close();
            }
        }
    }
    private void loadRecyclerView(){
        contactlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(contactlayoutManager);
        recyclerAdaptor = new ContactRecyclerAdaptor(this, contactList);
        recyclerView.setAdapter(recyclerAdaptor);
        recyclerView.setNestedScrollingEnabled(false);
    }

//    private void openContactList(){
//
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//        startActivityForResult(intent, PICK_CONTACT);
//
//        Intent phonebookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
//        phonebookIntent.putExtra("additional", "phone-multi");
//        phonebookIntent.putExtra("maxRecipientCount", MAX_PICK_CONTACT);
//        phonebookIntent.putExtra("FromMMS", true);
//        startActivityForResult(phonebookIntent, REQUEST_CODE_PICK_CONTACT);
//    }

//        @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case REQUEST_CODE_PICK_CONTACT:
//                    final EditText phoneInput = (EditText) findViewById(R.id.phoneNumberInput);
//                    Cursor cursor = null;
//                    String phoneNumber = "";
//                    List<String> allNumbers = new ArrayList<String>();
//                    int phoneIdx = 0;
//                    try {
//                        Uri result = data.getData();
//                        String id = result.getLastPathSegment();
//                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
//                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
//                        if (cursor.moveToFirst()) {
//                            while (cursor.isAfterLast() == false) {
//                                phoneNumber = cursor.getString(phoneIdx);
//                                allNumbers.add(phoneNumber);
//                                cursor.moveToNext();
//                            }
//                        } else {
//                            //no results actions
//                        }
//                    } catch (Exception e) {
//                        //error actions
//                    } finally {
//                        if (cursor != null) {
//                            cursor.close();
//                        }
//
//                        final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(InvitingActivity.this);
//                        builder.setTitle("Choose a number");
//                        builder.setItems(items, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                String selectedNumber = items[item].toString();
//                                selectedNumber = selectedNumber.replace("-", "");
//                            }
//                        });
//                        AlertDialog alert = builder.create();
//                        if(allNumbers.size() > 1) {
//                            alert.show();
//                        } else {
//                            String selectedNumber = phoneNumber.toString();
//                            selectedNumber = selectedNumber.replace("-", "");
//                        }
//
//                        if (phoneNumber.length() == 0) {
//                            //no numbers found actions
//                        }
//                    }
//                    break;
//
//
//                    Bundle bundle = data.getExtras();
//
//                    String result = bundle.getString("result");
//                    ArrayList<String> contacts = bundle.getStringArrayList("result");
//
//
//                    Log.i("Contatc", "launchMultiplePhonePicker bundle.toString()= " + contacts.toString());
//
//            }
//        } else {
//            //activity result error actions
//        }
//    }

    public void updateSelectedNumber(int number){
        if (number > 0) {
            this.selected_number_lbl.setText(String.valueOf(number)+ " Selected");
        } else {
            this.selected_number_lbl.setText("None Selected");
        }
    }

    public void sendSMS() {
        final int totalinvites = selectedContactList.size();
        final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("username",username);
                intent.putExtra("newuser",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };
        if (progressDialog == null) {
            progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitleText("Sending Invitations...");
            progressDialog.setCancelable(false);
            progressDialog.setContentText("Sent " + String.valueOf(sentCount) + "/" + String.valueOf(totalinvites));
            progressDialog.getProgressHelper().setRimColor(R.color.colorPrimary);
            progressDialog.show();
        }
        final String SENT_ACTION = "com.intelligentz.malchat.invsent"+String.valueOf(sentCount);
        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT_ACTION), 0);
        broadcastReceiver  = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        sentCount += 1;
                        if (sentCount >= totalinvites){
                            progressDialog.setTitleText("Success!")
                                    .setContentText("All the messages were sent")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(successListener)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        } else {
                            progressDialog.setTitleText("Sending...")
                                    .setContentText("Sent " + String.valueOf(sentCount) + "/" + String.valueOf(totalinvites))
                                    .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                            sendSMS();
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error sending. Sent "+String.valueOf(sentCount)+"/"+String.valueOf(totalinvites))
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error sending. Sent "+String.valueOf(sentCount)+"/"+String.valueOf(totalinvites))
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error sending. Sent "+String.valueOf(sentCount)+"/"+String.valueOf(totalinvites))
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        progressDialog.setTitleText("Failed!")
                                .setContentText("There was an error sending. Sent "+String.valueOf(sentCount)+"/"+String.valueOf(totalinvites))
                                .setConfirmText("OK")
                                .setConfirmClickListener(successListener)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(SENT_ACTION));
        String msg = "Data නැතුව chat/message කරමුද? මම නම් දාගත්තා. ඔයත් දාගන්න. පහල Link එකෙන් දැන්ම Mal Chat Download කරගන්න. www.bit.ly/MalChat ";
        String number = selectedContactList.get(sentCount).getMobile_number();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, sentPI, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    class RetrieveContacts extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitleText("Retrieving Contacts...");
            progressDialog.setContentText("Please wait");
            progressDialog.getProgressHelper().setRimColor(R.color.colorPrimary);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            getContactList();
            return null;
        }



        protected void onPostExecute(String file_url) {
            if (progressDialog.isShowing()) {
                progressDialog.dismissWithAnimation();
            }
            loadRecyclerView();
        }
    }
}
