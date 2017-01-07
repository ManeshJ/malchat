package com.intelligentz.malchat.malchat.adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.intelligentz.malchat.malchat.view.ChatActivity;
import com.intelligentz.malchat.malchat.view.MainActivity;

import java.util.ArrayList;

/**
 * Created by Lakshan on 2017-01-07.
 */

public class ChatRecyclerAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    ArrayList<ChatMessage> messageList = null;
    Context context = null;
    MainActivity activity;
    public ChatRecyclerAdaptor(ArrayList<ChatMessage> messageList, Context context, MainActivity activity) {
        this.context = context;
        this.messageList = messageList;
        this.activity = activity;
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
        switch (message.getType()) {
            case 0:
                ((SentRecyclerViewHolder)holder).messageTxt.setText(message.getBody());
                break;
            case 1:
                ((ReceivedRecyclerViewHolder)holder).messageTxt.setText(message.getBody());
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
        ArrayList<ChatMessage> messageList = null;
        Context context = null;
        public ReceivedRecyclerViewHolder(View itemView, Context context, ArrayList<ChatMessage> messageList) {
            super(itemView);
            this.context =context;
            this.messageList = messageList;
            messageTxt = (TextView) itemView.findViewById(R.id.chatText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        }
    }

    public class SentRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView messageTxt;
        ArrayList<ChatMessage> messageList = null;
        Context context = null;
        public SentRecyclerViewHolder(View itemView, Context context, ArrayList<ChatMessage> messageList) {
            super(itemView);
            this.context =context;
            this.messageList = messageList;
            messageTxt = (TextView) itemView.findViewById(R.id.chatText);
        }

        @Override
        public void onClick(View view) {
        }
    }
}

