package com.intelligentz.malchat.malchat.adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.ChatMessage;
import com.intelligentz.malchat.malchat.view.ChatActivity;
import com.intelligentz.malchat.malchat.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lakshan on 11/5/16.
 */
public class AccountsRecyclerAdaptor extends RecyclerView.Adapter<AccountsRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<ChatMessage> mesageList = null;
    Context context = null;
    MainActivity activity;
    public AccountsRecyclerAdaptor(ArrayList<ChatMessage> mesageList, Context context, MainActivity activity) {
        this.context = context;
        this.mesageList = mesageList;
        this.activity = activity;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, mesageList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ChatMessage message = mesageList.get(position);
        holder.contactNameTxt.setText(message.getAddress());
        holder.lastMsgTxt.setText(message.getBody());
        long val = Long.valueOf(message.getDate());
        Date toDate = new Date(System.currentTimeMillis());
        Date date=new Date(val);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM  dd");

        if (df2.format(date).equals(df2.format(toDate))) {
            df2 = new SimpleDateFormat("HH:mma");
        }
        holder.lastSeenTxt.setText(df2.format(date));

    }
    @Override
    public int getItemCount() {
        return mesageList.size();
    }
    @Override
    public void onClick(View view) {

    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircularImageView accountImageView = null;
        TextView contactNameTxt;
        TextView lastMsgTxt;
        TextView lastSeenTxt;
        ArrayList<ChatMessage> messageList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<ChatMessage> messageList) {
            super(itemView);
            this.context =context;
            this.messageList = messageList;
            accountImageView = (CircularImageView) itemView.findViewById(R.id.contact_icon);
            contactNameTxt = (TextView) itemView.findViewById(R.id.contact_name);
            lastMsgTxt = (TextView) itemView.findViewById(R.id.last_msg);
            lastSeenTxt = (TextView) itemView.findViewById(R.id.last_seen);
            accountImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username",messageList.get(getAdapterPosition()).getAddress());
            context.startActivity(intent);
        }
    }
}
