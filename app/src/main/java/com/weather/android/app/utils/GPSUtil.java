package com.weather.android.app.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.weather.android.app.R;

public class GPSUtil {

    //Check GPS Status true/false
    public static boolean checkGPSStatus(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static AlertDialog buildAlertMessageNoGps(Context context, GPSAlertChoiceListener gpsAlertChoiceListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.gps_enable_text)
                .setCancelable(false)
                .setPositiveButton(R.string.setting, (dialogInterface, i) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    if (gpsAlertChoiceListener != null) {
                        gpsAlertChoiceListener.onCancelClick();
                    }
                });
        return builder.create();
    }

    public static AlertDialog buildAlertMessageDenyPermission(AppCompatActivity context, String msg, LocationPermissionAlertChoiceListener locationPermissionAlertChoiceListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.setting, (dialogInterface, i) -> IntentUtils.openAppSettingsForActivity(context))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    if (locationPermissionAlertChoiceListener != null) {
                        locationPermissionAlertChoiceListener.onCancelClick();
                    }
                });
        return builder.create();
    }

    public interface GPSAlertChoiceListener {
        void onCancelClick();
    }

    public interface LocationPermissionAlertChoiceListener {
        void onCancelClick();
    }
}
