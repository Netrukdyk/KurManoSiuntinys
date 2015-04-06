package com.example.kurmanosiuntinys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
	
	private String silenceStart = "21:00";
	private String silenceEnd = "8:00";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Alarm", "Fire");

		if(!isSilenceTime()){
			// Call Updater IntentService on Alarm
			Intent updaterIntent = new Intent(context, Updater.class);
			context.startService(updaterIntent);
		}
	}
	
	private boolean isSilenceTime(){
		return C.isTimeInRange(silenceStart, silenceEnd);
	}

}