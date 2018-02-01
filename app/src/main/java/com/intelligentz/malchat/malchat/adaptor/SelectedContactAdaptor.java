package com.intelligentz.malchat.malchat.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;

import java.util.ArrayList;

/**
 * Created by lakshan on 2/2/18.
 */

public class SelectedContactAdaptor extends RecyclerView.Adapter<SelectedContactAdaptor.RecyclerViewHolder> {
    private Context context;
    private ArrayList<Contact> selectedContactList;
    public SelectedContactAdaptor(Context context) {
        this.context = context;
        selectedContactList = ContactRecyclerAdaptor.selectedContactList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_horizontal_item_layout,parent,false);
        SelectedContactAdaptor.RecyclerViewHolder recyclerViewHolder = new SelectedContactAdaptor.RecyclerViewHolder(view, context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.contactNameTxt.setText(selectedContactList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return selectedContactList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView contactNameTxt;
        ImageView cancelButtton;
        ArrayList<Contact> contactList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context) {
            super(itemView);
            this.context =context;
            this.contactList = contactList;
            contactNameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            cancelButtton =  itemView.findViewById(R.id.cancel_icon);
            cancelButtton.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            selectedContactList.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}
