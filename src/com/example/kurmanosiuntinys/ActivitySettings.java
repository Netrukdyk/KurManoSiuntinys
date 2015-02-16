package com.example.kurmanosiuntinys;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class ActivitySettings extends Activity implements OnClickListener {
	
	SharedPreferences prefs;
	Switch switchNotificastions;
	DatabaseHandler db;
	private PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getActionBar().setBackgroundDrawable(null);
		
		db = new DatabaseHandler(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Intent alarmIntent = new Intent(this, Alarm.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);       
        
		switchNotificastions = (Switch) findViewById(R.id.switchNotifications);		
		switchNotificastions.setChecked((prefs.getInt("notifications",0))==1);
		switchNotificastions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) 	setAlarm();
				else 			unsetAlarm();
			}
		});
		
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
		findViewById(R.id.btn3).setOnClickListener(this);
	}
			
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn1 :
				db.removeAll();
				Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn2 :
				break;
			case R.id.btn3 :
				break;
		}
	}
	

	
	public void setAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 30; // sek

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval*1000, pendingIntent);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notifications", 1);
        editor.apply();
        
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void unsetAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notifications", 0);
        editor.apply();
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void startAt10() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        		interval, pendingIntent);
    }
    
}