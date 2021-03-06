package com.intelligentz.malchat.malchat.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.AccountsRecyclerAdaptor;
import com.intelligentz.malchat.malchat.adaptor.ChatRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.luolc.emojirain.EmojiRainLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

public class ChatActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks{
    private String username;
    private RecyclerView recyclerView;
    private ChatRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager chatlayoutManager;
    private ImageView imageView;
    private ImageView attachBtn;
    private EditText msgTxt;
    private Context context;
    private ArrayList<ChatMessage> messageList;
    private DialogPlus dialog;
    private ImageView heartBtn;
    private ImageView funBtn;
    private View view;
    private EmojiRainLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        username = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(username);
        imageView = (ImageView) findViewById(R.id.send_btn);
        mContainer = (EmojiRainLayout) findViewById(R.id.group_emoji_container);
        msgTxt = (EditText) findViewById(R.id.msgTxt);
        attachBtn = (ImageView) findViewById(R.id.attach_btn);
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachItem();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!msgTxt.getText().toString().isEmpty()) {
                    sendSMS(msgTxt.getText().toString());
                    msgTxt.setText("");
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportLoaderManager().initLoader(1, null, this);
        String messageType = getIntent().getStringExtra("messageType");
        if (messageType != null){
            if (messageType.equals("love")) {
                sendSMS("love");
               /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mContainer.clearEmojis();
                    mContainer.addEmoji(R.drawable.hearticon);
                    mContainer.setPer(10);
                    mContainer.setDuration(5000);
                    mContainer.setDropDuration(2400);
                    mContainer.setDropFrequency(500);
                    mContainer.startDropping();
                }*/
            }else if (messageType.equals("fun")){
                sendSMS("fun");
               /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mContainer.clearEmojis();
                    mContainer.addEmoji(R.drawable.funicon);
                    mContainer.setPer(10);
                    mContainer.setDuration(5000);
                    mContainer.setDropDuration(2400);
                    mContainer.setDropFrequency(500);
                    mContainer.startDropping();
                }*/
            }
        }
    }
    private void attachItem() {
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.attach_dialog_layout))
                .setExpanded(false)
                .setGravity(Gravity.BOTTOM)
                .create();

        view = dialog.getHolderView();
        heartBtn = (ImageView) view.findViewById(R.id.heartBtn);
        funBtn = (ImageView) view.findViewById(R.id.funBtn);

        heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS("love");
                dialog.dismiss();
               /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mContainer.clearEmojis();
                    mContainer.addEmoji(R.drawable.hearticon);
                    mContainer.setPer(10);
                    mContainer.setDuration(5000);
                    mContainer.setDropDuration(2400);
                    mContainer.setDropFrequency(500);
                    mContainer.startDropping();
                }*/
            }
        });

        funBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS("fun");
                dialog.dismiss();
                /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mContainer.clearEmojis();
                    mContainer.addEmoji(R.drawable.funicon);
                    mContainer.setPer(10);
                    mContainer.setDuration(5000);
                    mContainer.setDropDuration(2400);
                    mContainer.setDropFrequency(500);
                    mContainer.startDropping();
                }*/
            }
        });
        dialog.show();
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"address", "date", "body"};
//        String selection = "address=? AND body LIKE ?";
        String selection = "address=? AND (body LIKE ? OR body LIKE ?)";
        String[] selectionArgs = new String[]{"77255", "From -"+username+"\n%", "Mal chat "+username+" %"};
        String orderBy = "date desc";
        return new CursorLoader(this, Uri.parse("content://sms"), projection, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        messageList = new ArrayList<>();
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String[] columns = new String[]{"address", "date", "body"};
            ChatMessage chatMessage;
            do {
                String body = cursor.getString(cursor.getColumnIndex(columns[2]));
                String date = cursor.getString(cursor.getColumnIndex(columns[1]));
                String[] substrings = new String[0];
                String address = "";
                chatMessage = new ChatMessage();
                if (body.startsWith("From")) {
                    body = body.substring(0, body.length()-20);
                    substrings = body.split("\\s");
                    address  = substrings[1].substring(1);
                    chatMessage.setType(1);
                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < 2; i++) {
                        builder.append(substrings[i] + " ");
                    }
                    String prefix = builder.toString();
                    prefix = prefix.substring(0, prefix.length()-1);
                    body = body.replace(prefix+"\n\n","");
                    if (!body.equals("fun") && !body.equals("love")) {
                        chatMessage.setAddress(address);
                        chatMessage.setDate(date);
                        chatMessage.setBody(body);
                        messageList.add(chatMessage);
                    }
                } else if (body.startsWith("Mal")) {
                    substrings = body.split("\\s");
                    address  = substrings[2];
                    chatMessage.setType(0);
                    StringBuilder builder = new StringBuilder();
                    for(int i = 3; i < substrings.length; i++) {
                        builder.append(substrings[i] + " ");
                    }
                    body = builder.toString();
                    if (body.trim().equals("fun")) {
                        body = new String(Character.toChars(0x1F602));
                    } else if (body.trim().equals("love")) {
                        body = new String(Character.toChars(0x2764));
                    }
                    chatMessage.setAddress(address);
                    chatMessage.setDate(date);
                    chatMessage.setBody(body);
                    messageList.add(chatMessage);
                }

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
        chatlayoutManager.scrollToPosition(0);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public void sendSMS(String msg) {
        msg = "Mal chat " + username + " " + msg;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("77255", null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
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
