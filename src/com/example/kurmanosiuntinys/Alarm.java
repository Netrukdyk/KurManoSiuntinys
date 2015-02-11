package com.example.kurmanosiuntinys;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Log.v("Updater","Running");
        if(Looper.myLooper() == Looper.getMainLooper()) {
        	Log.v("Updater","UI THREAD");
        }
        
        ResultReceiver mReceiver = new ResultReceiver(new Handler());
        //mReceiver.(context);
        /* Starting Download Service */
        
        /* Starting Download Service */

        Intent newIntent = new Intent(Intent.ACTION_SYNC, null, context, Updater.class);

        /* Send optional extras to Download IntentService */
        newIntent.putExtra("url", "www.inotecha.lt");
        newIntent.putExtra("receiver", mReceiver);
        newIntent.putExtra("requestId", 101);
        
        context.startService(newIntent);
        
        showNotification(context);
    }
    
    public void showNotification(Context context){

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(context, ActivityTrack.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(context)

            .setContentTitle("New Post!")
            .setContentText("Here's an awesome update for you!")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent)
            .setSound(soundUri)

            .addAction(R.drawable.ic_action_settings, "View", pIntent)
            .addAction(0, "Remind", pIntent)

            .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);
    }
    
    
}