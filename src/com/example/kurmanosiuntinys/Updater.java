package com.example.kurmanosiuntinys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class Updater extends IntentService {
	static final String	ACTION_UPDATED	= "ACTION_UPDATED";
	DatabaseHandler		db;
	SharedPreferences	prefs;

	public Updater() {
		super("SimpleIntentService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.db = new DatabaseHandler(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v("Updater", "IntentService started");

		// get data from db
		List<Item> itemList = db.getAllItems(true);

		// do http request
		List<Item> resultList = checkOnline(itemList);

		List<Item> changedList = null;
		// update db
		if (resultList != null) {
			changedList = db.updateItems(resultList);
		}

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ACTION_UPDATED);

		// Log.v("PREFS",prefs.getInt("notifications",0)+"");
		if (changedList != null && prefs.getInt("notifications", 0) == 1 && changedList.size() > 0)
			for (Item item : changedList) {
				broadcastIntent.putExtra("msg", item.getAlias() + " " + item.getNumber());
				showNotification(this, "Informacija atsinaujino", item.getAlias() + " " + item.getNumber());
			}

		// processing done here….

		sendBroadcast(broadcastIntent);

		Log.v("Updater", "Done");
	}

	// --- HTTP LOADER -----------------------------------------------
	// class Tikrinti extends AsyncTask<List<Item>, String, List<Item>> {
	// TextView out;
	// private List<Item> resultList = new ArrayList<Item>();
	// ProgressDialog progress;
	// ActivityTrack mainActivity;
	// long startTime, endTime;

	// protected void onPreExecute() {
	// startTime = System.currentTimeMillis();
	// out = (TextView) findViewById(R.id.out);
	// out.setText("Tikrinama...");
	// progress = new ProgressDialog(mainActivity);
	// progress.setTitle("Tikrinama...");
	// progress.setMessage("Palaukite kol informacija atsinaujins");
	// progress.show();
	// }

	private List<Item> checkOnline(List<Item> numbers) {
		if (numbers.size() == 0)
			return null;

		int count = numbers.size();

		List<Item> resultList = new ArrayList<Item>();
		resultList.clear();

		int n = count / 5;
		int m = count % 5;
		// 0-1 5-1 6-2 10-2 11-3
		for (int i = 0; i < n; i++) {
			resultList.addAll(checkOnline5(numbers.subList(i * 5, i * 5 + 5)));
		}
		resultList.addAll(checkOnline5(numbers.subList(n * 5, n * 5 + m)));

		return resultList;

	}

	private List<Item> checkOnline5(List<Item> numbers) {
		
		List<Item> resultList = new ArrayList<Item>();
		resultList.clear();
		
		if (numbers.size() == 0)
			return resultList;



		// formuojam get uþklausà
		String query = "";
		for (Item number : numbers)
			query += "%0D%0A" + number.getNumber();

		query = query.substring(6); // nukerpa pirmà new line
		Log.v("Updater", "GET Query=" + query);

		InputStream in = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			URI website = new URI("http://www.post.lt/lt/pagalba/siuntu-paieska/index?num=" + query);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			in = response.getEntity().getContent();

			// --------- Parse HTML ----------------------------------------
			String html = convertInputStreamToString(in);
			Log.v("SIZE", html.length() + "");
			Document doc = Jsoup.parse(html);
			// ------------- FOUND -------------------------
			Elements tables = doc.select("table");
			Log.v("JSoup", tables.size() + " tables");
			if (tables.size() > 0) {
				for (Element table : tables) {
					Item item = new Item();
					item.setNumber(table.getElementsByTag("strong").text());
					item.setAlias(db.getItem(item.getNumber()).getAlias());
					for (Element row : table.select("tr")) {
						Elements tds = row.select("td");
						if (tds.size() > 2) {
							ItemInfo itemInfo = new ItemInfo();
							itemInfo.setItemNumber(item.getNumber());
							itemInfo.setExplain(tds.first().text());
							itemInfo.setPlace(tds.get(1).text()); // place
							itemInfo.setDate(tds.last().text()); // date
							item.addItemInfo(itemInfo);
						}
					}
					
					// STATUS RULES
					if (item.getLastItemInfo().getExplain().contains("Siunta pristatyta ir áteikta gavëjui"))
						item.setStatus(Item.Status.DELIVERED);
					else if (item.getLastItemInfo().getExplain().contains("siunta perduota kurjeriui/laiðkininkui arba palikta paðte"))
						item.setStatus(Item.Status.PICKUP);
					else
						item.setStatus(Item.Status.TRANSIT);
					resultList.add(item);
				}
			}
			// ------------- NOT FOUND ----------------------
			Elements errors = doc.getElementsByClass("notfound");
			Log.v("JSoup", errors.size() + " errors");
			for (Element number : errors) {
				Item item = new Item();
				item.setNumber(number.getElementsByTag("strong").text());
				item.setAlias(db.getItem(item.getNumber()).getAlias());

				Elements details = number.getElementsContainingOwnText("duomenø rasti nepavyko");
				if (!details.isEmpty())
					item.setStatus(Item.Status.NOTFOUND);
				else {
					details = number.getElementsContainingOwnText("Neteisingas siuntos numerio formatas");
					if (!details.isEmpty())
						item.setStatus(Item.Status.WRONGNUMBER);
				}
				resultList.add(item);
			}
			doc = null;
			// --------- End Parse HTML -----------------------------------
			// publishProgress(text); // rodyti progresà kai tikrina
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
		return resultList;
	}

	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		StringBuilder result = new StringBuilder(inputStream.available()); // !! NOT String +=
		while ((line = bufferedReader.readLine()) != null)
			result.append(line);
		inputStream.close();
		return result.toString();
	}

	public void showNotification(Context context, String title, String text) {

		// define sound URI, the sound to be played when there's a notification
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// intent triggered, you can add other intent for other actions
		Intent intent = new Intent(context, ActivityTrack.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// this is it, we'll build the notification!
		// in the addAction method, if you don't want any icon, just set the first param to 0
		Notification mNotification = new Notification.Builder(context)

		.setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).setSound(soundUri).setLights(Color.RED, 300, 300)
				.addAction(R.drawable.ic_action_settings, "View", pIntent).addAction(0, "Remind", pIntent).setPriority(Notification.PRIORITY_MAX).build();
		mNotification.defaults = 0;
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// If you want to hide the notification after it was selected, do the code below
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, mNotification);
	}
	// protected void onPostExecute(List<Item> resultList) {
	// progress.dismiss();
	// endTime = System.currentTimeMillis();
	// out.setText((endTime - startTime) / 1000.0 + " s");

	// updateList();
	// }

	// } // --- END OF AsyncTask ---

}