package com.example.kurmanosiuntinys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ActivityAbout extends Activity {

	LinearLayout selectServer, selectSoftware;
	ImageButton configServers;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

}
