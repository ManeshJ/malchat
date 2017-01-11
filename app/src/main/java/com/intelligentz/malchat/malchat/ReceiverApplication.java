package com.intelligentz.malchat.malchat;

import android.app.Application;

/**
 * Created by Lakshan on 2017-01-06.
 */

public class ReceiverApplication extends Application{
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
