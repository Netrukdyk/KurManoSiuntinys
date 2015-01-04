package com.example.kurmanosiuntinys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
			progress.setTitle("Tikrinama...");
			progress.setMessage("Palaukite kol informacija atsinaujins");
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
						Item item = new Item();
						item.setNumber(table.getElementsByTag("strong").text());
						item.setAlias("Siuntinys");
						for (Element row : table.select("tr")) {
							Elements tds = row.select("td");
							if (tds.size() > 2) {
								ItemInfo itemInfo = new ItemInfo();
								itemInfo.setExplain(tds.first().text());
								itemInfo.setPlace(tds.get(1).text()); // place
								itemInfo.setDate(tds.last().text()); // date
								item.addItemInfo(itemInfo);
							}
						}
						if (!item.getLastItemInfo().getPlace().contains("Paðto skirstymo departamentas")
								&& !item.getLastItemInfo().getExplain().contains("Siunta paðte priimta ið siuntëjo"))
							item.setStatus(Item.Status.PASTE);
						else if (item.getLastItemInfo().getPlace().contains("Paðto skirstymo departamentas")
								&& item.getLastItemInfo().getExplain().contains("Siunta iðsiøsta á uþsiená"))
							item.setStatus(Item.Status.PASTE);
						else
							item.setStatus(Item.Status.VILNIUS);
						resultList.add(item);
					}
				}
				// ------------- NOT FOUND ----------------------
				Elements errors = doc.getElementsByClass("notfound");
				Log.v("JSoup", errors.size() + " errors");
				for (Element number : errors) {
					Item item = new Item();
					item.setAlias("Siuntinys");
					item.setNumber(number.getElementsByTag("strong").text());

					Elements details = number.getElementsContainingOwnText("duomenø rasti nepavyko");
					if (!details.isEmpty())
						item.setStatus(Item.Status.NERA);
					else {
						details = number.getElementsContainingOwnText("Neteisingas siuntos numerio formatas");
						if (!details.isEmpty())
							item.setStatus(Item.Status.BLOGAS);
					}
					resultList.add(item);
				}
				doc = null;
				// --------- End Parse HTML
				// ----------------------------------------
				// publishProgress(text); // rodyti progresà kai tikrina
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
			out.setText((endTime - startTime) / 1000.0 + " s");
			updateList(resultList);
		}

	} // --- END OF AsyncTask ---
} // --- END OF ACTIVITY ---
