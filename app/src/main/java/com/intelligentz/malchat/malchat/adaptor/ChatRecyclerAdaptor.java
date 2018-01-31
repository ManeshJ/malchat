package com.intelligentz.malchat.malchat.adaptor;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.intelligentz.malchat.malchat.view.ChatActivity;
import com.intelligentz.malchat.malchat.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lakshan on 2017-01-07.
 */

public class ChatRecyclerAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    ArrayList<ChatMessage> messageList = null;
    Context context = null;
    Activity activity;
    private View.OnLongClickListener longClickListener;
    public ChatRecyclerAdaptor(ArrayList<ChatMessage> messageList, final Context context, Activity activity) {
        this.context = context;
        this.messageList = messageList;
        this.activity = activity;
        this.longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                if (view.getId() == R.id.chatText){
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.copymenu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.copy) {
                                TextView textView = (TextView) view;
                                CharSequence text = textView.getText();
                                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(text, text);
                                clipboard.setPrimaryClip(clip);
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
                return false;
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = messageList.get(position);
        if (chatMessage != null) {
            return chatMessage.getType();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_chat_layout,parent,false);
                ChatRecyclerAdaptor.SentRecyclerViewHolder recyclerViewHolder = new ChatRecyclerAdaptor.SentRecyclerViewHolder(view, context, messageList);
                return recyclerViewHolder;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_chat_layout,parent,false);
                ChatRecyclerAdaptor.ReceivedRecyclerViewHolder receivedrecyclerViewHolder = new ChatRecyclerAdaptor.ReceivedRecyclerViewHolder(view, context, messageList);
                return receivedrecyclerViewHolder;
            default:
                return  null;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        long val = Long.valueOf(message.getDate());
        Date toDate = new Date(System.currentTimeMillis());
        Date date=new Date(val);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM  dd");
        if (df2.format(date).equals(df2.format(toDate))) {
            df2 = new SimpleDateFormat("HH:mma");
        }else {
            df2 = new SimpleDateFormat("MMM  dd -HH:mma");
        }
        switch (message.getType()) {
            case 0:
                ((SentRecyclerViewHolder)holder).messageTxt.setText(message.getBody());
                ((SentRecyclerViewHolder)holder).dateTxt.setText(df2.format(date));
                break;
            case 1:
                ((ReceivedRecyclerViewHolder)holder).messageTxt.setText(message.getBody());
                ((ReceivedRecyclerViewHolder)holder).dateTxt.setText(df2.format(date));
        }
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    @Override
    public void onClick(View view) {

    }

    public class ReceivedRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView messageTxt;
        TextView dateTxt;
        ArrayList<ChatMessage> messageList = null;
        Context context = null;
        public ReceivedRecyclerViewHolder(View itemView, Context context, ArrayList<ChatMessage> messageList) {
            super(itemView);
            this.context =context;
            this.messageList = messageList;
            messageTxt = (TextView) itemView.findViewById(R.id.chatText);
            dateTxt = (TextView) itemView.findViewById(R.id.date_txt);
            messageTxt.setOnLongClickListener(longClickListener);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        }
    }

    public class SentRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageTxt;
        TextView dateTxt;
        ArrayList<ChatMessage> messageList = null;
        Context context = null;
        public SentRecyclerViewHolder(View itemView, Context context, ArrayList<ChatMessage> messageList) {
            super(itemView);
            this.context =context;
            this.messageList = messageList;
            messageTxt = (TextView) itemView.findViewById(R.id.chatText);
            dateTxt = (TextView) itemView.findViewById(R.id.date_txt);
            messageTxt.setOnLongClickListener(longClickListener);
        }

        @Override
        public void onClick(View view) {
        }
    }
}

