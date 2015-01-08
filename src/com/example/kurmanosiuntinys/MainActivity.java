package com.example.kurmanosiuntinys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnTracking, btnCalc, btnSearch, btnPosts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnTracking = (Button) findViewById(R.id.btnTracking);
		btnCalc = (Button) findViewById(R.id.btnCalc);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnPosts = (Button) findViewById(R.id.btnPosts);

		btnTracking.setOnClickListener(this);
		btnCalc.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnPosts.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		String url = null;
		switch (v.getId()) {
			case R.id.btnTracking :
				intent = new Intent(this, ActivityTrack.class);
				break;
			case R.id.btnCalc :
				url = "http://www.post.lt/lt/pagalba/kainu-skaiciuokle/index/letters";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				break;
			case R.id.btnSearch :
				url = "http://www.post.lt/lt/pagalba/pasto-kodu-paieska/index/address";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				break;
			case R.id.btnPosts :
				url = "http://www.post.lt/lt/pagalba/pasto-skyriai-dezutes/index";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				break;
		}
		startActivity(intent);
	}
}
