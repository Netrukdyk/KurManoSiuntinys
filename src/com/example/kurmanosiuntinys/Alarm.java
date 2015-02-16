package com.example.kurmanosiuntinys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Alarm", "Fire");
		
		// Call Updater IntentService on Alarm
		Intent updaterIntent = new Intent(context, Updater.class);
		context.startService(updaterIntent);
	}
	
}