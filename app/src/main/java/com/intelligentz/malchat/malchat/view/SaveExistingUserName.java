package com.intelligentz.malchat.malchat.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;

public class SaveExistingUserName extends AbstractActivity {
    private Button saveBtn;
    String username;
    private Context context;
    private EditText usernameTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_existing_user_name);
        context = this;
        saveBtn = (Button) findViewById(R.id.saveBtn);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserName();
            }
        });
    }

    private void saveUserName() {
        username = usernameTxt.getText().toString().trim();
        SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("username",username);
        editor.commit();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }
}
