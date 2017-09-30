package com.intelligentz.malchat.malchat.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.intelligentz.malchat.malchat.R;

public class RegisterPromptActivity extends AppCompatActivity {
    Button reg_btn;
    Button cancel_btn;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_prompt);
        context = this;
        reg_btn = (Button) findViewById(R.id.register);
        cancel_btn = (Button) findViewById(R.id.cancel);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewUserNameActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
