package com.intelligentz.malchat.malchat.view;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.ChatRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.ChatMessage;

import java.util.ArrayList;

public class NewChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{
    private String username;
    private RecyclerView recyclerView;
    private ChatRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager chatlayoutManager;
    private ImageView imageView;
    private EditText msgTxt;
    private EditText recipTxt;
    private LinearLayout reciLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        username = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(username);
        imageView = (ImageView) findViewById(R.id.send_btn);
        msgTxt = (EditText) findViewById(R.id.msgTxt);
        recipTxt = (EditText) findViewById(R.id.recipTxt);
        reciLayout = (LinearLayout) findViewById(R.id.recipLayout);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!recipTxt.getText().toString().isEmpty() && !msgTxt.getText().toString().isEmpty()) {
                    sendSMS(msgTxt.getText().toString());
                }
            }
        });
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"address", "date", "body"};
//        String selection = "address=? AND body LIKE ?";
        String selection = "address=? AND (body LIKE ? OR body LIKE ?)";
        String[] selectionArgs = new String[]{"77255", "From -"+username+"%", "Mal chat "+username+"%"};
        String orderBy = "date desc";
        return new CursorLoader(this, Uri.parse("content://sms"), projection, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        ArrayList<ChatMessage> messageList = new ArrayList<>();
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String[] columns = new String[]{"address", "date", "body"};
            ChatMessage chatMessage;
            do {
                String body = cursor.getString(cursor.getColumnIndex(columns[2]));
                String date = cursor.getString(cursor.getColumnIndex(columns[1]));
                String[] substrings = new String[0];
                String address = "";
                int startIndex = 0;
                chatMessage = new ChatMessage();
                if (body.startsWith("F")) {
                    body = body.substring(0, body.length()-20);
                    substrings = body.split("\\s");
                    address  = substrings[1].substring(1);
                    chatMessage.setType(1);
                    startIndex = 2;
                } else if (body.startsWith("M")) {
                    substrings = body.split(" ");
                    address  = substrings[2];
                    chatMessage.setType(0);
                    startIndex = 3;
                }
                chatMessage.setAddress(address);
                chatMessage.setDate(date);
                StringBuilder builder = new StringBuilder();
                for(int i = startIndex; i < substrings.length; i++) {
                    builder.append(substrings[i] + " ");
                }
                body = builder.toString();
                chatMessage.setBody(body);
                messageList.add(chatMessage);
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        configureRecyclerView(messageList);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void configureRecyclerView(ArrayList<ChatMessage> messageList) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        chatlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(chatlayoutManager);
        recyclerAdaptor = new ChatRecyclerAdaptor(messageList, this, this);
        recyclerView.setAdapter(recyclerAdaptor);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public void sendSMS(String msg) {
        username = recipTxt.getText().toString().trim();
        getSupportActionBar().setTitle(username);
        msg = "Mal chat " + username + " " + msg;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("77255", null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();

            msgTxt.setText("");
            reciLayout.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(1, null, this);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
