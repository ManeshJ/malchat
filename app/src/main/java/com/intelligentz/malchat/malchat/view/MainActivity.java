package com.intelligentz.malchat.malchat.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.AccountsRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.intelligentz.malchat.malchat.model.Contact;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private RecyclerView recyclerView;
    private AccountsRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager accountslayoutManager;
    private CollapsingToolbarLayout toolbarLayout;
    private RapidFloatingActionButton fab;
    private RapidFloatingActionHelper fabHelper;
    private RapidFloatingActionLayout fabLayout;
    private String username;
    private String chatusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("මල් Chat");
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbarLayout.setTitle("");
        toolbarLayout.setBackgroundResource(R.drawable.header_image);
        configureFab();
        getSupportLoaderManager().initLoader(1, null, this);
        username = getIntent().getStringExtra("username");
        chatusername = getIntent().getStringExtra("chatusername");
        if (chatusername != null && !chatusername.equals("MalChat")) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("username",chatusername);
            startActivity(intent);
        }
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
                if (body.startsWith("From")) {
                    substrings = body.split("\\s");
                    address  = substrings[1].substring(1);
                    startIndex = 2;
                } else if (body.startsWith("Mal")) {
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

    private void configureFab() {
        fab = (RapidFloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        fabLayout = (RapidFloatingActionLayout) findViewById(R.id.activity_main_rfal);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Fun Message")
                .setResId(R.drawable.funicon)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Love Message")
                .setResId(R.drawable.hearticon)
                .setIconNormalColor(0xffFFCC00)
                .setIconPressedColor(0xff3e2723)
                .setLabelColor(0xffFF3300)
                .setLabelSizeSp(14)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Text Message")
                .setResId(R.drawable.textmsgicon)
                .setIconNormalColor(0xff0099FF)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xff0099FF)
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(this, 5))
        ;
        fabHelper = new RapidFloatingActionHelper(
                this,
                fabLayout,
                fab,
                rfaContent
        ).build();
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
//        Toast.makeText(this, "clicked label: " + i, Toast.LENGTH_SHORT).show();
//        fabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
        //Toast.makeText(this, "clicked icon: " + i, Toast.LENGTH_SHORT).show();
        switch (i) {
            case 2:
                Intent intent = new Intent(this, NewChatActivity.class);
                startActivity(intent);
        }
        fabHelper.toggleContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upusername) {
            Intent intent = new Intent(this, UpdateUserNameActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        chatusername = intent.getStringExtra("chatusername");
        if (chatusername != null && !chatusername.equals("MalChat")) {
            Intent newintent = new Intent(this, ChatActivity.class);
            intent.putExtra("username",chatusername);
            startActivity(newintent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("මල්Chat");
    }
}
