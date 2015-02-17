package com.example.kurmanosiuntinys;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
	BroadcastReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		getActionBar().setBackgroundDrawable(null);
		getOverflowMenu();
		db = new DatabaseHandler(this);
		
		receiver = new BroadcastReceiver(){
	        @Override
	        public void onReceive(Context context, Intent intent) {
				String text = intent.getStringExtra("msg");
				Log.v("Track","Update completed");
				updateList();
	            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
				 Intent intentSettings = new Intent(this, ActivitySettings.class);
				 startActivity(intentSettings);
				 updateList();
				return true;
			case R.id.action_add :
				showInputDialog();
				return true;
			case R.id.action_refresh :				
				Intent msgIntent = new Intent(this, Updater.class);
				startService(msgIntent);
				return true;
			default :
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
		    @Override public void validate(TextView textView, String text) {
		        if (C.checkNumber(text)) {
		        	number.setBackgroundColor(C.GREEN);
		        } else number.setBackgroundColor(C.RED);
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
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
		@Override
			public void onClick(View v){			
				String myAlias = (alias.getText().toString() == "") ? "Siuntinys" : alias.getText().toString();
				String myNumber = number.getText().toString();
				if (C.checkNumber(myNumber)) {					
					db.addItem(new Item(myAlias, myNumber, 1, C.getDate()));
					updateList();
					dialog.dismiss();
				} else{
					
					Toast.makeText(ActivityTrack.this, "Neteisingi duomenys", Toast.LENGTH_SHORT).show();
				}
					
			}
		});
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}


} // --- END OF ACTIVITY ---
