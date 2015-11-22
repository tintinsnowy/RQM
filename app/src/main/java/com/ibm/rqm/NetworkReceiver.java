package com.ibm.rqm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * 监听网络变化，并用isConnected变量记录
 */
public class NetworkReceiver extends BroadcastReceiver{

    public static final String TAG = "NetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo == null || !activeNetInfo.isConnected()) {
                isConnected = false;
            } else {
                isConnected = true;
            }
        }

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("isConnected", isConnected).commit();

    }
}
