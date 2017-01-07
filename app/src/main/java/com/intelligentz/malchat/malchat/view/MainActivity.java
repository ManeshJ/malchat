package com.intelligentz.malchat.malchat.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.AccountsRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.intelligentz.malchat.malchat.model.Contact;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{
    private RecyclerView recyclerView;
    private AccountsRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager accountslayoutManager;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("මල් Chat");
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("මල් Chat");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportLoaderManager().initLoader(1, null, this);
    }

    private void configureRecyclerView(ArrayList<ChatMessage> messageList) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        accountslayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(accountslayoutManager);
//        ArrayList<Contact> contactList = new ArrayList<>();
//        contactList.add(new Contact("Pasindu"));
//        contactList.add(new Contact("Dineth"));
//        contactList.add(new Contact("Chamika"));
//        contactList.add(new Contact("Dilum"));
//        contactList.add(new Contact("Heshan"));
//        contactList.add(new Contact("Manesh"));
//        contactList.add(new Contact("Sandeepa"));
//        contactList.add(new Contact("Yasiru"));
//        contactList.add(new Contact("Shalith"));
//        contactList.add(new Contact("Hansika"));
//        contactList.add(new Contact("Kumudu"));
//        contactList.add(new Contact("Sameera"));
//        contactList.add(new Contact("Darika"));
//        contactList.add(new Contact("Hashini"));
//        contactList.add(new Contact("Keet"));
//        contactList.add(new Contact("Vindula"));
        recyclerAdaptor = new AccountsRecyclerAdaptor(messageList, this, this);
        recyclerView.setAdapter(recyclerAdaptor);
        recyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"address", "date", "body"};
//        String selection = "address=? AND body LIKE ?";
        String selection = "address=? AND (body LIKE ? OR body LIKE ?)";
        String[] selectionArgs = new String[]{"77255", "From %", "Mal chat %"};
        String orderBy = "date desc";
        return new CursorLoader(this, Uri.parse("content://sms"), projection, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        ArrayList<ChatMessage> messageList = new ArrayList<>();
        ArrayList<String> addressList = new ArrayList<>();
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String[] columns = new String[]{"address", "date", "body"};
            ChatMessage chatMessage;
            do {
                String body = cursor.getString(cursor.getColumnIndex(columns[2]));
                String date = cursor.getString(cursor.getColumnIndex(columns[1]));
                String[] substrings = new String[0];
                String address = "";
                int startIndex = 0;
                if (body.startsWith("F")) {
                    substrings = body.split("\\s");
                    address  = substrings[1].substring(1);
                    startIndex = 2;
                } else if (body.startsWith("M")) {
                    substrings = body.split(" ");
                    address  = substrings[2];
                    startIndex = 3;
                }
                chatMessage = new ChatMessage(0, address, body, date);
                if (!addressList.contains(address)) {
                    addressList.add(address);
                    StringBuilder builder = new StringBuilder();
                    for(int i = startIndex; i < substrings.length; i++) {
                        builder.append(substrings[i] + " ");
                    }
                    body = builder.toString();
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
}
