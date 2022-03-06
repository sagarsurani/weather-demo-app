package com.weather.android.app.utils;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.weather.android.app.BuildConfig;


public class IntentUtils {
    public static void openAppSettingsForActivity(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, Constants.REQUEST_SETTINGS);
        }
    }
}
