package com.example.kurmanosiuntinys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityItem extends Activity implements OnClickListener{
	TextView itemAlias, itemNumber, itemStatus;
	ImageView logoImg;
	DatabaseHandler db;
	Item item;
	ImageButton btnEdit, btnDelete;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
        
		btnEdit = (ImageButton) findViewById(R.id.btn_edit);
		btnDelete = (ImageButton) findViewById(R.id.btn_delete);
		btnEdit.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
        
		itemAlias = (TextView) findViewById(R.id.itemAlias);
		itemNumber = (TextView) findViewById(R.id.itemNumber);
		itemStatus = (TextView) findViewById(R.id.itemStatus);
		logoImg = (ImageView) findViewById(R.id.itemImg);
		db = new DatabaseHandler(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String number = extras.getString("item");
			if (number != null && number != "") {
				item = db.getItem(number);
				itemAlias.setText(item.getAlias());
				itemNumber.setText(item.getNumber());
				Item.Status status = item.getStatus();

				int icon = 0;				
				switch (status) {
					case WRONGNUMBER :
						itemStatus.setTextColor(Color.RED);
						itemStatus.setText("Neteisingas numeris");
						icon = R.drawable.ic_status_not_found;
						break;
					case NOTFOUND :
						itemStatus.setTextColor(Color.RED);
						itemStatus.setText("Nëra informacijos");
						icon = R.drawable.ic_status_not_found;
						break;
					case TRANSIT :
						itemStatus.setTextColor(Color.GRAY);
						itemStatus.setText("Siunèiama");
						icon = R.drawable.ic_status_transit;
						break;
					case PICKUP :
						itemStatus.setTextColor(0x8050FA50);
						itemStatus.setText("Atsiimti paðte");
						icon = R.drawable.ic_status_pickup;
					case DELIVERED :
						itemStatus.setTextColor(Color.GREEN);
						itemStatus.setText("Pristatyta");
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
		}
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
				return true;
			case R.id.action_delete:
				db.deleteItem(item);
				finish();
				return true;
			default :
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_edit:
				//showInputDialog();
				break;
			case R.id.btn_delete:
				db.deleteItem(item);
				finish();
				break;
		}
		
	}
}
