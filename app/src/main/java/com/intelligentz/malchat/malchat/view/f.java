package com.intelligentz.malchat.malchat.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.adaptor.AccountsRecyclerAdaptor;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

public class f extends AbstractActivity implements LoaderManager.LoaderCallbacks {
    private RecyclerView recyclerView;
    private AccountsRecyclerAdaptor recyclerAdaptor;
    private RecyclerView.LayoutManager accountslayoutManager;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    public static String username;
    private String chatusername;
    private DialogPlus funDialog;
    private View fun_message_view;
    private ImageView sendBtn;
    private EditText usernameTxt;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle("මල් Chat");
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbarLayout.setTitle("");
        toolbarLayout.setBackgroundResource(R.drawable.header_image);
        configureFab();
        getSupportLoaderManager().initLoader(1, null, this);
        boolean isNewUser = getIntent().getBooleanExtra("newuser",false);
        if (isNewUser){
            showNewUserMessage();
        }
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("මල් Chat");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        username = getIntent().getStringExtra("username");
        chatusername = getIntent().getStringExtra("chatusername");
        if (chatusername != null && !chatusername.equals("MalChat")) {
            Intent intent = new Intent(this, b.class);
            intent.putExtra("username",chatusername);
            startActivity(intent);
        }
    }

    private void configureRecyclerView(ArrayList<ChatMessage> messageList) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        accountslayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(accountslayoutManager);
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
                    substrings = body.split("\\s");
                    address  = substrings[2];
                    startIndex = 3;
                }
                chatMessage = new ChatMessage(0, address, body, date);
                boolean isContain = false;
                for (String addressi : addressList) {
                    if (addressi.equalsIgnoreCase(address)) {
                        isContain = true ;
                        break;
                    }
                }
                if (!isContain) {
                    addressList.add(address.toLowerCase());
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, g.class);
                startActivity(intent);
            }
        });
    }

    private void showNewUserMessage() {
        funDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.newusermainactivitydialoglayout))
                .setContentBackgroundResource(R.drawable.fun_message_fragment_background)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create();
        fun_message_view = funDialog.getHolderView();
        Button okBtn = (Button) fun_message_view.findViewById(R.id.okbtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funDialog.dismiss();
            }
        });
        funDialog.show();
    }
    private void showMyUsername() {
        funDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.myusernamelayout))
                .setExpanded(false)
                .setGravity(Gravity.TOP)
                .create();
        fun_message_view = funDialog.getHolderView();
        Button okBtn = (Button) fun_message_view.findViewById(R.id.okbtn);
        TextView usernametxt = (TextView) fun_message_view.findViewById(R.id.headerTxt);
        usernametxt.setText(username);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funDialog.dismiss();
            }
        });
        funDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update_username) {
            Intent intent = new Intent(this, o.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.my_username) {
            showMyUsername();
        } else if (id == R.id.action_faq) {
            Intent intent = new Intent(this, d.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, a.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(this, c.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_terms) {
            Intent intent = new Intent(this, m.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        chatusername = intent.getStringExtra("chatusername");
        if (chatusername != null && !chatusername.equals("MalChat")) {
            Intent newintent = new Intent(this, b.class);
            intent.putExtra("username",chatusername);
            startActivity(newintent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getSupportActionBar().setTitle("මල්Chat");
        if (recyclerAdaptor != null ){
            recyclerAdaptor.changeLastPos();
        }
    }

}