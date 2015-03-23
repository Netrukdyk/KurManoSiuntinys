package com.example.kurmanosiuntinys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityItem extends Activity {
	TextView itemAlias, itemNumber, itemStatus;
	ImageView logoImg;
	DatabaseHandler db;
	Item item;
	ImageButton btnEdit, btnDelete;
	ProgressDialog	updatingDialog;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		//getActionBar().setBackgroundDrawable(null);
        
		itemAlias = (TextView) findViewById(R.id.itemAlias);
		itemNumber = (TextView) findViewById(R.id.itemNumber);
		itemStatus = (TextView) findViewById(R.id.itemStatus);
		logoImg = (ImageView) findViewById(R.id.itemImg);
		db = new DatabaseHandler(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String number = extras.getString("item");
			if (number != null && number != "") {
				init(number);
			}
		}
	}
	
	public void init(String number){
		item = db.getItem(number);
		itemAlias.setText(item.getAlias());
		itemNumber.setText(item.getNumber());
		Item.Status status = item.getStatus();

		int icon = 0;				
		switch (status) {
			case WRONGNUMBER :
				itemStatus.setText(C.WRONG);
				icon = R.drawable.ic_status_not_found;
				break;
			case NOTFOUND :
				itemStatus.setText(C.NOINFO);
				icon = R.drawable.ic_status_not_found;
				break;
			case TRANSIT :
				itemStatus.setText(C.TRANSIT);
				icon = R.drawable.ic_status_transit;
				break;
			case PICKUP :
				itemStatus.setText(C.PICKUP);
				icon = R.drawable.ic_status_pickup;
			case DELIVERED :
				itemStatus.setText(C.DELIVERED);
				icon = R.drawable.ic_status_delivered;
				break;
			default :
				break;
		}
		if (logoImg != null) logoImg.setImageResource(icon);				
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.itemInfoLayout);
		
		for (ItemInfo itemInfo : item.getItemInfo()) {
			View v = getLayoutInflater().inflate(R.layout.item_info, null);
			TextView date = (TextView) v.findViewById(R.id.date);
			TextView place = (TextView) v.findViewById(R.id.place);
			TextView explain = (TextView) v.findViewById(R.id.explain);					
			explain.setText(itemInfo.getExplain());
			place.setText(itemInfo.getPlace());
			date.setText(itemInfo.getDate());			
			layout.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}		
		
	}
	
	// --- ENTER NEW NUMBER DIALOG -------------------------------------------------
		@SuppressLint("InflateParams")
		private void showInputDialog() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pridëti naujà numerá");
			View dialogView = getLayoutInflater().inflate(R.layout.new_dialog, null);

			final EditText number = (EditText) dialogView.findViewById(R.id.dialogNumber);
			number.setText(item.getNumber());
			number.requestFocus();
		
	        if (C.checkNumber(number.getText().toString())) {
	        	number.setBackgroundColor(C.GREEN);
	        } else number.setBackgroundColor(C.RED);
	        
			// add OnTextChange LIstener
			number.addTextChangedListener(new TextValidator(number) {
			    @Override public void validate(TextView textView, String text) {
			        if (C.checkNumber(text)) {
			        	number.setBackgroundColor(C.GREEN);
			        } else number.setBackgroundColor(C.RED);
			     }
			 });
			final EditText alias = (EditText) dialogView.findViewById(R.id.dialogAlias);
			alias.setText(item.getAlias());
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
					item.setAlias(myAlias);
					item.setNumber(myNumber);
					if (C.checkNumber(myNumber)) {					
						db.updateItem(item);
						init(item.getNumber());
						//refreshData();
						C.exportDB();
						dialog.dismiss();
					} else{						
						Toast.makeText(ActivityItem.this, "Neteisingi duomenys", Toast.LENGTH_SHORT).show();
					}
						
				}
			});
		}
		
		public void refreshData() {
			// service intent
			final Intent msgIntent = new Intent(this, Updater.class);

			updatingDialog = new ProgressDialog(ActivityItem.this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.action_about :
				Intent intent = new Intent(this, ActivityAbout.class);
				startActivity(intent);
				return true;
			case R.id.action_edit :
				showInputDialog();
				return true;
			case R.id.action_delete:
				db.deleteItem(item);
				C.exportDB();
				finish();
				return true;
			default :
				return super.onOptionsItemSelected(menuItem);
		}
	}

}
