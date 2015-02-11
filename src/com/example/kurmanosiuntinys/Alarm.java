package com.example.kurmanosiuntinys;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class Alarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Alarm", "Fire");
		
		// Call Updater IntentService
		Intent updaterIntent = new Intent(context, Updater.class);
		context.startService(updaterIntent);

	}
	
/*  
 *  Update completed, result here. 
 */
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String	ACTION_UPDATED	= "ACTION_UPDATED";
		@Override
		public void onReceive(Context context, Intent intent) {
			String text = intent.getStringExtra("msg");
			showNotification(context, "Updated", text);
		}
	}

	public void showNotification(Context context, String title, String text) {

		// define sound URI, the sound to be played when there's a notification
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// intent triggered, you can add other intent for other actions
		Intent intent = new Intent(context, ActivityTrack.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// this is it, we'll build the notification!
		// in the addAction method, if you don't want any icon, just set the first param to 0
		Notification mNotification = new Notification.Builder(context)

		.setContentTitle("New Post!").setContentText("Here's an awesome update for you!").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).setSound(soundUri)

		.addAction(R.drawable.ic_action_settings, "View", pIntent).addAction(0, "Remind", pIntent)

		.build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// If you want to hide the notification after it was selected, do the code below
		// myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, mNotification);
	}

}