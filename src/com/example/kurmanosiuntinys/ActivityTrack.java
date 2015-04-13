package com.example.kurmanosiuntinys;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityTrack extends Activity {

	String				list[]	= { "RC313227871HK", "RN037964246LT", "RS117443425NL", "RT123456789LT", "R123456LT" };
	DatabaseHandler		db;
	BroadcastReceiver	receiver;
	ImageButton			btnAdd, btnRefresh;

	ProgressDialog		updatingDialog;
	SharedPreferences 	prefs;
	String				msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		// getActionBar().setBackgroundDrawable(null);
		
		getActionBar().setHomeButtonEnabled(true); // enable home btn
		getOverflowMenu();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		db = new DatabaseHandler(this);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updatingDialog.dismiss();
				// String text = intent.getStringExtra("msg");
				Log.v("Track", "Update completed");
				updateList();
				// Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Updater.ACTION_UPDATED);
		registerReceiver(receiver, filter);

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		updateList();
		super.onResume();
	}
	@Override
	protected void onPause() {
		updateList();
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

	public void updateList() {
		int excludeOlds = (prefs.getInt(C.SWITCH_HIDE, C.DEFAULT_SWITCH_HIDE)==1) ? prefs.getInt(C.VALUE_HIDE, C.DEFAULT_VALUE_HIDE) : 0 ;
		Boolean reverse = (prefs.getInt(C.VALUE_ORDER, C.DEFAULT_VALUE_ORDER)==0) ? true : false ;
		updateList(db.getAllItems(false, reverse, excludeOlds));
	}

	public void updateList(List<Item> resultList) {
		ListView myListView = (ListView) findViewById(R.id.listItems);
		ListAdapter customAdapter = new ListAdapter(this, R.layout.list, resultList);
		//
		// TextView footer = (TextView) view.findViewById(R.id.loadMore);
		// getListView().addFooterView(footer);

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
		case R.id.action_about:
			Intent intent = new Intent(this, ActivityAbout.class);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			Intent intentSettings = new Intent(this, ActivitySettings.class);
			startActivity(intentSettings);
			updateList();
			return true;
		case R.id.action_add:
			showInputDialog();
			return true;
		case R.id.action_refresh:
			refreshData();
			return true;
		case R.id.action_backup:
			backup();
			return true;
		case R.id.action_restore:
			restore();
			return true;
		case android.R.id.home:
			Intent homeIntent = new Intent(this, MainActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// --- ENTER NEW NUMBER DIALOG -------------------------------------------------
	@SuppressLint("InflateParams")
	private void showInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pridëti naujà numerá");
		View dialogView = getLayoutInflater().inflate(R.layout.new_dialog, null);

		final EditText number = (EditText) dialogView.findViewById(R.id.dialogNumber);
		number.requestFocus();
		number.setBackgroundColor(C.RED);

		// add OnTextChange LIstener
		number.addTextChangedListener(new TextValidator(number) {
			@Override
			public void validate(TextView textView, String text) {
				if (C.checkNumber(text)) {
					number.setBackgroundColor(C.GREEN);
				} else
					number.setBackgroundColor(C.RED);
			}
		});
		final EditText alias = (EditText) dialogView.findViewById(R.id.dialogAlias);
		builder.setView(dialogView);
		builder.setPositiveButton("OK", null);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String myAlias = (alias.getText().toString() == "") ? "Siuntinys" : alias.getText().toString();
				String myNumber = number.getText().toString();
				if (C.checkNumber(myNumber)) {
					db.addItem(new Item(myAlias, myNumber, 1, C.getDate()));
					updateList();
					refreshData();
					backup();
					dialog.dismiss();
				} else {

					Toast.makeText(ActivityTrack.this, "Neteisingi duomenys", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	public void backup() {
		msg = (C.exportDB()) ? "Backup Successful!" : "Backup Failed!";
		C.exportNums(db.getAllItemLite());
		updateList();
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void restore() {
		
		String imported = C.importNums().trim(); // try import from txt
		String[] separated = imported.split("\n");

		if (imported != "") { // if ok, parse numbers
			db.removeAll();
			for (String itemLine : separated) {
				Log.v("PARSE", itemLine);
				String[] item = itemLine.split("\\|");
				Log.v("PARSE2", item[0] + " xxx " + item[1]);

				if (C.checkNumber(item[0])) {
					db.addItem(new Item(item[1], item[0], 1, C.getDate()));
				}
			}
			msg = separated.length + " imported";
		} else { // if txt fail, import all db if exists
			msg = (C.importDB()) ? "Import Successful!" : "Import Failed!";
		}

		updateList();
		refreshData();
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void refreshData() {
		// service intent
		final Intent msgIntent = new Intent(this, Updater.class);

		updatingDialog = new ProgressDialog(ActivityTrack.this);
		updatingDialog.setMessage("Atnaujinama...");
		updatingDialog.setCancelable(false);
		updatingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Atðaukti", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		updatingDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				stopService(msgIntent);
			}
		});
		updatingDialog.show();

		// call service
		startService(msgIntent);
	}

} // --- END OF ACTIVITY ---
