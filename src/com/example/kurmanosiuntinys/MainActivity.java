package com.example.kurmanosiuntinys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	String	list[]	= { "RC313227871HK", "RN037964246LT", "RT123456789LT", "R123456LT" };

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

	public void updateList(List<Item> resultList) {
		ListView myListView = (ListView) findViewById(R.id.listItems);
		ListAdapter customAdapter = new ListAdapter(this, R.layout.list, resultList);
		myListView.setAdapter(customAdapter);
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
		case R.id.action_about:
			Intent intent = new Intent(this, ActivityAbout.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			new Tikrinti(this).execute(list);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}

	// ---- HTTP LOADER
	// -------------------------------------------------------------------------------------
	class Tikrinti extends AsyncTask<String, String, List<Item>> {
		TextView			out;
		private String		result;
		private List<Item>	resultList	= new ArrayList<Item>();
		ProgressDialog		progress;
		MainActivity		mainActivity;
		long				startTime, endTime;

		public Tikrinti(MainActivity mainActivity) {
			this.mainActivity = mainActivity;
		}

		protected void onPreExecute() {
			startTime = System.currentTimeMillis();
			out = (TextView) findViewById(R.id.out);
			out.setText("Tikrinama...");
			progress = new ProgressDialog(mainActivity);
			progress.setTitle("Tikrinama");
			progress.setMessage("Wait while loading...");
			progress.show();

		}

		@SuppressLint("UseValueOf")
		protected List<Item> doInBackground(String... numbers) {
			InputStream in = null;
			int count = numbers.length;
			String query = numbers[0];
			resultList.clear();
			for (int i = 1; i < count; i++) {
				query += "%0D%0A" + numbers[i];
			}
			Log.v("Query", query);
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
				// ------------- FOUND -------------------------
				Elements tables = doc.select("table");
				Log.v("JSoup", tables.size() + " tables");
				if (tables.size() > 0) {
					for (Element table : tables) {
						Item x = new Item();
						x.number = table.getElementsByTag("strong").text();
						x.alias = "Siuntinys";
						for (Element row : table.select("tr")) {
							Elements tds = row.select("td");
							if (tds.size() > 2) {
								x.explain = tds.first().text(); // explain
								x.place = tds.get(1).text(); // place
								x.date = tds.last().text(); // date
							}
						}
						if (!x.getPlace().contains("Paðto skirstymo departamentas") && !x.getExplain().contains("Siunta paðte priimta ið siuntëjo"))
							x.status = Item.Status.PASTE;
						else if (x.getPlace().contains("Paðto skirstymo departamentas") && x.getExplain().contains("Siunta iðsiøsta á uþsiená"))
							x.status = Item.Status.PASTE;
						else
							x.status = Item.Status.VILNIUS;
						resultList.add(x);
					}
				}
				// ------------- NOT FOUND ----------------------
				Elements errors = doc.getElementsByClass("notfound");
				Log.v("JSoup", errors.size() + " errors");
				for (Element number : errors) {
					Item x = new Item();
					x.alias = "Siuntinys";
					x.status = Item.Status.NERA;
					x.number = number.getElementsByTag("strong").text();
					x.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
					Elements details = number.getElementsContainingOwnText("duomenø rasti nepavyko");
					if (!details.isEmpty())
						x.place = details.first().text().split("\\.")[0] + ".";
					else {
						details = number.getElementsContainingOwnText("Neteisingas siuntos numerio formatas");
						if (!details.isEmpty()) {
							String sentences[] = details.first().text().split("\\. ");
							x.place = sentences[0] + ".";
							x.explain = sentences[1];
						}
					}
					// x.explain = details.last().text();
					resultList.add(x);
				}
				doc = null;
				// --------- End Parse HTML
				// ----------------------------------------
				// publishProgress(text); // rodyti kai tikrina

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
			return resultList;
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

		protected void onPostExecute(List<Item> resultList) {
			progress.dismiss();
			endTime = System.currentTimeMillis();
			result = (endTime - startTime) / 1000.0 + " s";
			out.setText(result);
			updateList(resultList);
		}

	} // --- END OF AsyncTask ---
} // --- END OF ACTIVITY ---
