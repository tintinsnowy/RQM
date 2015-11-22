package com.ibm.rqm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by Dio on 2015/04/06.
 */
public class BootReceiver extends BroadcastReceiver{

    private SharedPreferences mPrefs;
    private String AlarmTime;
    private AlarmManager mAManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        mPrefs = context.getSharedPreferences("RQMAlarmTime", Activity.MODE_PRIVATE);
        AlarmTime = mPrefs.getString("notification_time", "");
        int hourOfDay = Integer.parseInt(AlarmTime.substring(0,1));
        int minute = Integer.parseInt(AlarmTime.substring(3,4));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        PendingIntent sender= PendingIntent.getBroadcast(context, 0, intent, 0);

        mAManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mAManager.set(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(),
                sender
        );
    }
}

