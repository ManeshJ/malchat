package com.intelligentz.malchat.malchat.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.intelligentz.malchat.malchat.AbstractActivity;
import com.intelligentz.malchat.malchat.R;
import com.intelligentz.malchat.malchat.p;

public class l extends AbstractActivity {
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ImageView spinner;
    long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        spinner = (ImageView) findViewById(R.id.spinner);
        startTime = System.currentTimeMillis();
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                requestPermission();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        spinner.startAnimation(rotateAnimation);
        //requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSIONS);
        }else {
//            while (System.currentTimeMillis() - startTime < 3000) {
//
//            }
            goToNextActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int permisson : grantResults) {
                if (permisson != PackageManager.PERMISSION_GRANTED) {
                    showMessageOKCancel("You need to provide permisson to access SMS to continue.", null);
                    return;
                }
            }
//            while (System.currentTimeMillis() - startTime < 3000) {
//
//            }
            goToNextActivity();
        }
    }

    private void goToNextActivity() {
        startService(new Intent(this, p.class));
        SharedPreferences mPrefs = getSharedPreferences("malchat.username", Context.MODE_PRIVATE);
        String username = mPrefs.getString("username", null);
        if (username == null) {
            Intent intent = new Intent(this, h.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, f.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(l.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
