package com.example.kurmanosiuntinys;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class ActivitySettings extends Activity implements OnClickListener {

	SharedPreferences prefs;
	DatabaseHandler db;
	private PendingIntent pendingIntent;
	private Switch switchNotificastions, switchAutoUpdate, switchSilence, switchAutoHide;



	private LinearLayout changeTimer, changeHide, layoutSilence;
	private TextView valueTimer, valueHide, valueSilenceStart, valueSilenceEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// getActionBar().setBackgroundDrawable(null);

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

		layoutSilence = (LinearLayout) findViewById(R.id.silence);
		valueSilenceStart = (TextView) findViewById(R.id.valueSilenceStart);
		valueSilenceEnd = (TextView) findViewById(R.id.valueSilenceEnd);
		
		updateValues();

		changeTimer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimerDialog();
			}
		});

		valueSilenceStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment();

				newFragment.show(getFragmentManager(), "timeStart");
			}
		});

		valueSilenceEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "timeEnd");
			}
		});
		
		changeHide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showHideDialog();
			}
		});		

		switchAutoUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (isChecked) setAlarm();
				else unsetAlarm();
				
				changeTimer.setVisibility((isChecked) ? LinearLayout.VISIBLE :LinearLayout.GONE);
				switchNotificastions.setVisibility((isChecked) ? Switch.VISIBLE :Switch.GONE);
				setPrefs(C.SWITCH_AUTOUPDATE, isChecked ? 1 : 0);				
			}
		});
		switchNotificastions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setPrefs(C.SWITCH_NOTIFICATIONS, isChecked ? 1 : 0);
			}
		});
		switchSilence.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				layoutSilence.setVisibility((isChecked) ? LinearLayout.VISIBLE : LinearLayout.GONE);
				setPrefs(C.SWITCH_SILENCE, isChecked ? 1 : 0);
			}
		});
		switchAutoHide.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeHide.setVisibility((isChecked) ? LinearLayout.VISIBLE :LinearLayout.GONE);
				setPrefs(C.SWITCH_HIDE, isChecked ? 1 : 0);
			}
		});

		findViewById(R.id.btn1).setOnClickListener(this);
	}

	private void updateValues() {
		switchAutoUpdate.setChecked((prefs.getInt(C.SWITCH_AUTOUPDATE, C.DEFAULT_SWITCH_AUTOUPDATE)) == 1);
		changeTimer.setVisibility((switchAutoUpdate.isChecked()) ? LinearLayout.VISIBLE :LinearLayout.GONE);
		switchNotificastions.setVisibility((switchAutoUpdate.isChecked()) ? Switch.VISIBLE :Switch.GONE);
		
		switchNotificastions.setChecked((prefs.getInt(C.SWITCH_NOTIFICATIONS, C.DEFAULT_SWITCH_NOTIFICATIONS)) == 1);
		switchSilence.setChecked((prefs.getInt(C.SWITCH_SILENCE, C.DEFAULT_SWITCH_SILENCE)) == 1);
		layoutSilence.setVisibility((switchSilence.isChecked()) ? LinearLayout.VISIBLE : LinearLayout.GONE);
		switchAutoHide.setChecked((prefs.getInt(C.SWITCH_HIDE, C.DEFAULT_SWITCH_HIDE)) == 1);
		changeHide.setVisibility((switchAutoHide.isChecked()) ? LinearLayout.VISIBLE :LinearLayout.GONE);

		valueTimer.setText(prefs.getInt(C.VALUE_TIMER, C.DEFAULT_VALUE_TIMER) + " valandos");

		valueSilenceStart.setText(prefs.getString(C.VALUE_SILENCE_START, C.DEFAULT_VALUE_SILENCE_START));
		valueSilenceEnd.setText(prefs.getString(C.VALUE_SILENCE_END, C.DEFAULT_VALUE_SILENCE_END));

		valueHide.setText(prefs.getInt(C.VALUE_HIDE, C.DEFAULT_VALUE_HIDE) + " dienos");
	}

	private void showTimerDialog() {
		final CharSequence colors[] = new CharSequence[] { "6", "12", "24" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setPrefs(C.VALUE_TIMER, Integer.valueOf(String.valueOf(colors[which])));
			}
		});
		builder.setTitle("Intervalas (valandos):");
		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = super.onCreateDialog(savedInstanceState);

			String title = null;
			int hour = 0, minute = 0;

			if (this.getTag() == "timeStart") {
				title = "Tylos pradþia";
				hour = Integer.valueOf(prefs.getString(C.VALUE_SILENCE_START, C.DEFAULT_VALUE_SILENCE_START).split(":")[0]);
			} else if (this.getTag() == "timeEnd") {
				title = "Tylos pabaiga";
				hour = Integer.valueOf(prefs.getString(C.VALUE_SILENCE_END, C.DEFAULT_VALUE_SILENCE_END).split(":")[0]);
			}
			dialog.setTitle(title);
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (this.getTag() == "timeStart") {
				setPrefs(C.VALUE_SILENCE_START, hourOfDay + ":" + String.format("%02d", minute));
			} else if (this.getTag() == "timeEnd") {
				setPrefs(C.VALUE_SILENCE_END, hourOfDay + ":" + String.format("%02d", minute));
			}

		}
	} // end timer picker
	
	private void showHideDialog(){
		final CharSequence hideValues[] = new CharSequence[] { "7", "14", "28", "90" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(hideValues, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setPrefs(C.VALUE_HIDE, Integer.valueOf(String.valueOf(hideValues[which])));
			}
		});
		builder.setTitle("Dienos:");
		final AlertDialog dialog = builder.create();
		dialog.show();		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			db.removeAll();
			Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void setPrefs(String name, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(name, value);
		editor.apply();
		updateValues();
		//Toast.makeText(this, name + " = " + value, Toast.LENGTH_SHORT).show();
	}

	private void setPrefs(String name, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name, value);
		editor.apply();
		updateValues();
		//Toast.makeText(this, name + " = " + value, Toast.LENGTH_SHORT).show();
	}

	public void setAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int interval = prefs.getInt(C.VALUE_TIMER, 6) * 60 * 60; // sek // 3 val
		manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval * 1000, pendingIntent);
		//Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
	}

	public void unsetAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		manager.cancel(pendingIntent);
		//Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
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