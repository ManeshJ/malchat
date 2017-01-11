package com.intelligentz.malchat.malchat;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lakshan on 2017-01-11.
 */

public abstract class AbstractActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        ReceiverApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ReceiverApplication.activityPaused();
    }
}
