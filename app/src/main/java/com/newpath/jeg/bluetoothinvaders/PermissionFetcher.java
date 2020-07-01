package com.newpath.jeg.bluetoothinvaders;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;


public class PermissionFetcher {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_ENABLE_FINELOC = 1;

    private final String TAG = "PermissionFetcher";
    private Context mContext;


    public static boolean hasLocationPermissions(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return c.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else
            return true;

    }

    public static void requestLocationPermission(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity)c).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_FINELOC);
        }
    }
}
