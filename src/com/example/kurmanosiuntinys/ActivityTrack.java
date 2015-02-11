package com.example.kurmanosiuntinys;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityTrack extends Activity implements OnClickListener {

	String list[] = {"RC313227871HK", "RN037964246LT", "RS117443425NL", "RT123456789LT", "R123456LT"};
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		getActionBar().setBackgroundDrawable(null);
		getOverflowMenu();
		db = new DatabaseHandler(this);
		//updateList();
		
		Intent alarmIntent = new Intent(ActivityTrack.this, Alarm.class);
        pendingIntent = PendingIntent.getBroadcast(ActivityTrack.this, 0, alarmIntent, 0);
	}
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Updater.ACTION_UPDATED);
        registerReceiver(receiver, filter);
        updateList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }	

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			String text = intent.getStringExtra("msg");
			Log.v("Track","Updated completed");
            Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT);
        }
    };

	public void updateList() {
		updateList(db.getAllItems(false));
	}

	public void updateList(List<Item> resultList) {
		ListView myListView = (ListView) findViewById(R.id.listItems);
		ListAdapter customAdapter = new ListAdapter(this, R.layout.list, resultList);
		myListView.setAdapter(customAdapter);
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView number = (TextView) view.findViewById(R.id.number);
				// String item = number.getText().toString();
				// Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
				Intent i = new Intent(getApplicationContext(), ActivityItem.class);
				i.putExtra("item", number.getText().toString());
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about :
				Intent intent = new Intent(this, ActivityAbout.class);
				startActivity(intent);
				return true;
			case R.id.action_settings :
				// Intent intent = new Intent(this, ActivitySetting.class);
				// startActivity(intent);
				// db.removeAll();
				//updateList();
				start();
				
				return true;
			case R.id.action_add :
				showInputDialog();
				cancel();
				return true;
			case R.id.action_refresh :				
				Intent msgIntent = new Intent(this, Updater.class);
				startService(msgIntent);				
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("InflateParams")
	private void showInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pridëti naujà numerá");

		View dialogView = getLayoutInflater().inflate(R.layout.new_dialog, null);

		final EditText number = (EditText) dialogView.findViewById(R.id.dialogNumber);
		number.requestFocus();
		final EditText alias = (EditText) dialogView.findViewById(R.id.dialogAlias);
		builder.setView(dialogView);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String myAlias = (alias.getText().toString() == "") ? "Siuntinys" : alias.getText().toString();
				String myNumber = number.getText().toString();

				if (myNumber.length() >= 12) {
					db.addItem(new Item(myAlias, myNumber, 1, C.getDate()));
					updateList();
					dialog.dismiss();
				} else
					Toast.makeText(ActivityTrack.this, "Neteisingi duomenys", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		// builder.show();
		AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();

		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY , 0);
	}
	
	private PendingIntent pendingIntent;
	
	public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
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
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        		interval, pendingIntent);
    }

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}


} // --- END OF ACTIVITY ---
