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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class ActivitySettings extends Activity implements OnClickListener {

	SharedPreferences prefs;
	DatabaseHandler db;
	private PendingIntent pendingIntent;
	private Switch switchNotificastions, switchAutoUpdate, switchSilence, switchAutoHide;
	
	private final String autoUpdate = "auto_update";
	private final String notifications = "notifications";
	private final String silence = "silence";
	private final String autoHide = "auto_hide";
	private final String timer = "timer";
	
	private LinearLayout changeTimer, changeHide;
	private TextView valueTimer, valueHide, valueSilence, valueShort;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//getActionBar().setBackgroundDrawable(null);

		db = new DatabaseHandler(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		Intent alarmIntent = new Intent(this, Alarm.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
		
		switchAutoUpdate = (Switch) findViewById(R.id.switchAutoUpdate);
		switchNotificastions = (Switch) findViewById(R.id.switchNotifications);
		switchSilence = (Switch) findViewById(R.id.switchSilence);
		switchAutoHide = (Switch) findViewById(R.id.switchAutoHide);
		
		changeTimer = (LinearLayout) findViewById(R.id.changeTimer);
		valueTimer = (TextView) findViewById(R.id.valueTimer);
		
		changeHide = (LinearLayout) findViewById(R.id.changeHide);
		valueHide = (TextView) findViewById(R.id.valueHide);
		
		valueSilence = (TextView) findViewById(R.id.valueSilence);
		valueShort = (TextView) findViewById(R.id.valueShort);
		
		switchAutoUpdate.setChecked((prefs.getInt(autoUpdate, 0)) == 1);
		switchNotificastions.setChecked((prefs.getInt(notifications, 0)) == 1);
		switchSilence.setChecked((prefs.getInt(silence, 0)) == 1);
		switchAutoHide.setChecked((prefs.getInt(autoHide, 0)) == 1);
		
		valueTimer.setText(prefs.getInt(timer, 0)+" valandos");

		switchAutoUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					setAlarm();
				else
					unsetAlarm();
				setPrefs(autoUpdate, isChecked? 1:0);
			}
		});
		switchNotificastions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setPrefs(notifications, isChecked? 1:0);
			}
		});
		switchSilence.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setPrefs(silence, isChecked? 1:0);
			}
		});
		switchAutoHide.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setPrefs(autoHide, isChecked? 1:0);
			}
		});
		
		findViewById(R.id.btn1).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn1 :
				db.removeAll();
				Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
				break;
		}
	}

	private void setPrefs(String name, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(name, value);
		editor.apply();
		Toast.makeText(this, name +" = "+value, Toast.LENGTH_SHORT).show();
	}

	public void setAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int interval = 3*60*60; // sek // 3 val
		manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval * 1000, pendingIntent);
		Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
	}

	public void unsetAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		manager.cancel(pendingIntent);
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
		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
	}

}