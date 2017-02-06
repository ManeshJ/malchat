package com.intelligentz.malchat.malchat.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.model.Contact;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.w3c.dom.Text;

public class AboutUsActivity extends AppCompatActivity {
    LinearLayout calllayout;
    LinearLayout maillayout;
    LinearLayout fblayout;
    private DialogPlus calldialog;
    private View calldialogview;
    private Context context;
    private ImageView logo;
    private ImageView callicon;
    private ImageView mailicon;
    private ImageView fbicon;
    private TextView calltxt;
    private TextView mailtxt;
    private TextView fbtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        context = this;
        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        calllayout = (LinearLayout) findViewById(R.id.calllayout);
        maillayout = (LinearLayout) findViewById(R.id.maillayout);
        fblayout = (LinearLayout) findViewById(R.id.fblayout);
        logo = (ImageView) findViewById(R.id.logo);
        callicon = (ImageView) findViewById(R.id.callicon);
        mailicon = (ImageView) findViewById(R.id.maiicon);
        fbicon = (ImageView) findViewById(R.id.fbicon);
        calltxt = (TextView) findViewById(R.id.calltxt);
        mailtxt = (TextView) findViewById(R.id.mailtxt);
        fbtxt = (TextView) findViewById(R.id.fbtxt);
        animate();
        calllayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calldialog = DialogPlus.newDialog(context)
                        .setContentHolder(new ViewHolder(R.layout.calldialoglayout))
                        .setExpanded(false)
                        .setGravity(Gravity.BOTTOM)
                        .create();
                calldialogview = calldialog.getHolderView();
                LinearLayout firstnumber = (LinearLayout) calldialogview.findViewById(R.id.first);
                LinearLayout secondnumber = (LinearLayout) calldialogview.findViewById(R.id.second);
                firstnumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calldialog.dismiss();
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:94773472649"));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(phoneIntent);
                    }
                });
                secondnumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calldialog.dismiss();
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:94718739322"));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(phoneIntent);
                    }
                });
                calldialog.show();
            }
        });

        maillayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"intelligentz.lk@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, " ");
                emailIntent.putExtra(Intent.EXTRA_TEXT, " ");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send a mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutUsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fblayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fbintent = newFacebookIntent(getPackageManager(),"https://www.facebook.com/IntelliGentz.lk");
                startActivity(fbintent);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatetada();
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
    private void animate(){
        int duration = 2000;
        YoYo.with(Techniques.Shake)
                .duration(duration).playOn(logo);
    }

    private void animatetada(){
        int duration = 1000;
        YoYo.with(Techniques.Tada)
                .duration(duration).playOn(logo);
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
