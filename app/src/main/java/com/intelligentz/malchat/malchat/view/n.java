package com.intelligentz.malchat.malchat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;

public class n extends AbstractActivity {
    private Button noUserNAmeBtn;
    private Button alreadyUserNameBtn;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_choosing);
        context = this;
        noUserNAmeBtn = (Button) findViewById(R.id.noUserNamebtn);
        alreadyUserNameBtn = (Button) findViewById(R.id.alreadyUserNameBtn);
        noUserNAmeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, h.class);
                startActivity(intent);
            }
        });
        alreadyUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, j.class);
                startActivity(intent);
            }
        });

    }
}
