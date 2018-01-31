package com.intelligentz.malchat.malchat.adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;
import com.intelligentz.malchat.malchat.view.ChatActivity;
import com.intelligentz.malchat.malchat.view.InvitingActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lakshan on 1/31/18.
 */

public class ContactRecyclerAdaptor extends RecyclerView.Adapter<ContactRecyclerAdaptor.RecyclerViewHolder>  {
    private Context context;
    private InvitingActivity invitingActivity;
    private ArrayList<Contact> contactList;
    private ArrayList<Contact> selectedContactList;

    public ContactRecyclerAdaptor(Context context, ArrayList<Contact> contactList){
        this.context = context;
        this.invitingActivity = (InvitingActivity) context;
        this.contactList = contactList;
        this.selectedContactList = new ArrayList<Contact>();
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout,parent,false);
        ContactRecyclerAdaptor.RecyclerViewHolder recyclerViewHolder = new ContactRecyclerAdaptor.RecyclerViewHolder(view, context, contactList);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.contactNameTxt.setText(contact.getUsername());
        holder.phone_number_txt.setText(contact.getMobile_number());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        CircularImageView accountImageView = null;
        TextView contactNameTxt;
        TextView phone_number_txt;
        CheckBox checkBox;
        ArrayList<Contact> contactList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<Contact> contactList) {
            super(itemView);
            this.context =context;
            this.contactList = contactList;
            accountImageView = (CircularImageView) itemView.findViewById(R.id.contact_icon);
            contactNameTxt = (TextView) itemView.findViewById(R.id.contact_name);
            phone_number_txt = (TextView) itemView.findViewById(R.id.mobile);
            checkBox =  itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int pos = getAdapterPosition();
            if (b) {
                selectedContactList.add(contactList.get(pos));
            } else {
                selectedContactList.remove(contactList.get(pos));
            }
            invitingActivity.updateSelectedNumber(selectedContactList.size());
        }
    }

    public ArrayList<Contact> getSelectedContactList() {
        return selectedContactList;
    }
}
