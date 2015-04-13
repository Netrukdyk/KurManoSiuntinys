package com.example.kurmanosiuntinys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityCodes extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_codes);
		Button redirect = (Button) findViewById(R.id.redirectCodes);
		
		redirect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = null;
				String url = null;
				url = "http://www.post.lt/lt/pagalba/pasto-kodu-paieska/index/address";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
			}
		});		
	}
}
