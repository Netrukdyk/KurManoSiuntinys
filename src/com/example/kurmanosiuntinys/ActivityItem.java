package com.example.kurmanosiuntinys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ActivityItem extends Activity {
	TextView		itemAlias, itemNumber;
	ImageView		logoImg;
	DatabaseHandler	db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);

		itemAlias = (TextView) findViewById(R.id.itemAlias);
		itemNumber = (TextView) findViewById(R.id.itemNumber);
		logoImg = (ImageView) findViewById(R.id.itemImg);
		db = new DatabaseHandler(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String number = extras.getString("item");
			if (number != null && number != "") {
				Item item = db.getItem(number);
				itemAlias.setText(item.getAlias());
				itemNumber.setText(item.getNumber());
				Item.Status status = item.getStatus();

				int icon = 0;
				switch (status) {
				case BLOGAS:
				case NERA:
					icon = R.drawable.ic_box_red;
					break;
				case VILNIUS:
					icon = R.drawable.ic_box_yellow;
					break;
				case PASTE:
				case PASIIMTA:
					icon = R.drawable.ic_box_green;
					break;
				default:
					break;
				}
				if (logoImg != null)
					logoImg.setImageResource(icon);

				TableLayout tl = (TableLayout) findViewById(R.id.itemInfoTableLayout);
				for (ItemInfo itemInfo : item.getItemInfo()) {
					TableRow tr = new TableRow(this);
					tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
					TextView explain = new TextView(this);
					TextView place = new TextView(this);
					TextView date = new TextView(this);
					explain.setText(itemInfo.getExplain());
					place.setText(itemInfo.getPlace());
					date.setText(itemInfo.getDate());					
					explain.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
					place.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
					date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
					tr.addView(explain);
					tr.addView(place);
					tr.addView(date);
					tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			Intent intent = new Intent(this, ActivityAbout.class);
			startActivity(intent);
			return true;
		case R.id.action_edit:
			return true;
		case R.id.action_delete:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
