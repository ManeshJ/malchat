package com.intelligentz.malchat.malchat.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;

public class ContactUsActivity extends AppCompatActivity {
    private LinearLayout facebooklayout;
    private LinearLayout mailLayout;
    private LinearLayout instalayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        facebooklayout = (LinearLayout) findViewById(R.id.face);
        mailLayout = (LinearLayout) findViewById(R.id.email);
        instalayout = (LinearLayout) findViewById(R.id.insta);

        facebooklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fbintent = newFacebookIntent(getPackageManager(),"https://www.facebook.com/n/?malchatlk");
                startActivity(fbintent);
            }
        });

        mailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"malchatlk@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, " ");
                emailIntent.putExtra(Intent.EXTRA_TEXT, " ");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send a mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactUsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        instalayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.instagram.com/malchatlk");
                Intent instaintent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(instaintent);
            }
        });
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
