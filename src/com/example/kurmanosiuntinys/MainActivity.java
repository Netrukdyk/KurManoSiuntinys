package com.example.kurmanosiuntinys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView in;
	String list[] = {"RC313227871HK", "RN037964246LT"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getOverflowMenu();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about :
				Intent intent = new Intent(this, ActivityAbout.class);
				startActivity(intent);
				return true;
			case R.id.action_refresh :
				new Tikrinti().execute(list);
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}

	class Tikrinti extends AsyncTask<String, String, String> {
		TextView out;
		private String result;

		protected void onPreExecute() {
			out = (TextView) findViewById(R.id.out);
			out.setText("Tikrinama...");
		}

		@SuppressWarnings("unchecked")
		@SuppressLint("UseValueOf")
		protected String doInBackground(String... numbers) {
			InputStream in = null;
			int count = numbers.length;
			String query = numbers[0];
			String result = "";
			for (int i = 1; i < count; i++) {
				query += "%0D%0A" + numbers[i];
				Log.v("Query", query);
			}
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				URI website = new URI("http://www.post.lt/lt/pagalba/siuntu-paieska/index?num=" + query);
				request.setURI(website);
				HttpResponse response = httpclient.execute(request);
				in = response.getEntity().getContent();

				// --------- Parse HTML ----------------------------------------
				String html = convertInputStreamToString(in);
				Document doc = Jsoup.parse(html);

				// surenka su error
				// Elements errors = doc.getElementsByClass("notfound");
				// for (Element number : errors) {
				// result += number.text() + "\n\n";
				// }
				// Log.v("Table", doc.html());

				Elements tables = doc.select("table");
				Log.v("Num", tables.size() + "x");
				if (tables.size() > 0) {
					for (Element table : tables) {
						String number = table.getElementsByTag("strong").text();
						Log.v("Number", number);
						result += number+"\n";
						for (Element row : table.select("tr")) {
							Elements tds = row.select("td");
							if (tds.size() > 2) {								
								result += tds.last().text() + " : "+tds.get(1).text()+ "\n";
							}
						}
						result += "\n\n";
					}
				}

				// result = String.valueOf(data.isEmpty());
				// for(Element elem : data){
				// elem.toString();
				// result += elem.text() + "\n\n";
				// }

				// if(data.isEmpty()){ // ERROR
				// data = doc.getElementsByClass("notfound");
				// for (Element number:data) {
				// result += number.text()+"\n\n";
				// number.hasText();
				// //number.ha
				// /* Neteisingas siuntos numerio formatas
				// * Pagal pateiktà siuntos numerá, duomenø rasti nepavyko.
				// * Table ...
				// */
				// }
				// }

				// --------- End Parse HTML
				// ----------------------------------------
				// publishProgress(text); // rodyti kai tikrina

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
			return result;
		}

		private String convertInputStreamToString(InputStream inputStream) throws IOException {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
			return result;

		}

		protected void onProgressUpdate(String... a) {
			// Log.v("Log", "You are in progress update ... " + a[0]);

		}

		protected void onPostExecute(String result) {
			out.setText(result);
		}

	}
}
