package com.example.kurmanosiuntinys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Alarm extends BroadcastReceiver {

	SharedPreferences prefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Alarm", "Fire");

		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		if (!isSilenceTime()) {
			// Call Updater IntentService on Alarm
			Intent updaterIntent = new Intent(context, Updater.class);
			context.startService(updaterIntent);
		}
	}

	private boolean isSilenceTime() {
		if (prefs.getInt(C.SWITCH_SILENCE, C.DEFAULT_SWITCH_SILENCE) == 1)
			return true;
		return C.isTimeInRange(prefs.getString(C.VALUE_SILENCE_START, C.DEFAULT_VALUE_SILENCE_START), prefs.getString(C.VALUE_SILENCE_END, C.DEFAULT_VALUE_SILENCE_END));
	}

}