package com.ibm.rqm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RQMReceiver extends BroadcastReceiver {

    private static final String TAG = "RQMReceiver";
    private static final int ID = 1;

    public RQMReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.d(TAG, "RQM Receiver: receive report broadcast");


        //TODO: obtain report data from intent.

        //get Notification Service.
        NotificationManager notifyMgr = (NotificationManager)context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO: set the content of notificaiton
        //Here is an example.
        Intent rqmIntent = new Intent(context, MainActivity.class);
        rqmIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, rqmIntent, 0);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher) //the icon of ntf
                .setTicker("查看您的报表")
                .setContentTitle("IBM_RQM")
                .setContentText("您的报表有更新，请查看。")
                .setContentIntent(pIntent)
                //Big view
                //.setStyle(new Notification.BigTextStyle())
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        //launch the Notificaiton
        notifyMgr.notify(ID, notification);

        Log.d(TAG, "Rqm Notification sended.");

    }
}
