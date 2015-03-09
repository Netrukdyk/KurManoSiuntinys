package com.example.kurmanosiuntinys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
	
	public static final String inputFormat = "HH:mm";

	private Date date;
	private Date dateBegin;
	private Date dateEnd;

	private String silenceStart = "21:00";
	private String silenceEnd = "8:00";

	SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
	
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
	    Calendar now = Calendar.getInstance();

	    int hour = now.get(Calendar.HOUR);
	    int minute = now.get(Calendar.MINUTE);

	    date = parseDate(hour + ":" + minute);
	    dateBegin = parseDate(silenceStart);
	    dateEnd = parseDate(silenceEnd);

	    if ( dateBegin.before( date ) && dateEnd.after(date)) {
	    	// in range
	        return true;
	    }
		return false;
	}
	
	private Date parseDate(String date) {

	    try {
	        return inputParser.parse(date);
	    } catch (java.text.ParseException e) {
	        return new Date(0);
	    }
	}	

	
}