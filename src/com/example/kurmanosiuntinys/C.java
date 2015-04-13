package com.example.kurmanosiuntinys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

public class C {
	public static final String	IP			= "192.168.1.200";
	public static final int		PORT		= 7000;
	public static final String	PREFS		= "Settings";
	public static final int		RED			= Color.argb(88, 215, 88, 88);
	public static final int		GREEN		= Color.argb(88, 88, 215, 88);
	public static final String	WRONG		= "Neteisingas numeris";
	public static final String	NOINFO		= "Nëra informacijos";
	public static final String	TRANSIT		= "Siunèiama";
	public static final String	PICKUP		= "Atsiimti paðte";
	public static final String	DELIVERED	= "Pristatyta";

	// SHARED PREFERENCES VARIANBLES
	public final static String SWITCH_AUTOUPDATE = "auto_update";
	public final static String SWITCH_NOTIFICATIONS = "notifications";
	public final static String SWITCH_SILENCE = "silence";
	public final static String SWITCH_HIDE = "auto_hide";

	public final static String VALUE_TIMER = "timer";
	public final static String VALUE_SILENCE_START = "silence_start";
	public final static String VALUE_SILENCE_END = "silence_end";
	public final static String VALUE_ORDER = "order";
	public final static String VALUE_HIDE = "hide";
	
	// DEFAULT SHARED PREFERENCES VALUES
	public final static int DEFAULT_SWITCH_AUTOUPDATE = 1;
	public final static int DEFAULT_SWITCH_NOTIFICATIONS = 1;
	public final static int DEFAULT_SWITCH_SILENCE = 1;
	public final static int DEFAULT_SWITCH_HIDE = 1;

	public final static int DEFAULT_VALUE_TIMER = 6;
	public final static String DEFAULT_VALUE_SILENCE_START = "23:00";
	public final static String DEFAULT_VALUE_SILENCE_END = "8:00";
	public final static int DEFAULT_VALUE_ORDER = 0;
	public final static int DEFAULT_VALUE_HIDE = 7;
	
	enum Type {
		STATUS, INFO, OTHER// 0, 1, 2
	}

	enum ServerStatus {
		Disconnected, Connecting, Connected
	}

	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
	}

	public static Boolean checkNumber(String number) {
		String re1 = "([a-z][a-z][0-9]{8,9}[a-z][a-z])";
		Pattern p = Pattern.compile(re1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		if (p.matcher(number).matches())
			return true;

		return false;
	}

	public static boolean isTimeInRange(String silenceStart, String silenceEnd) {
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR);
		int minute = now.get(Calendar.MINUTE);

		Date date = parseDate(hour + ":" + minute, "HH:mm");
		Date dateBegin = parseDate(silenceStart, "HH:mm");
		Date dateEnd = parseDate(silenceEnd, "HH:mm");

		if (dateBegin.before(date) && dateEnd.after(date)) {
			// in range
			return true;
		}
		return false;
	}

	private static Date parseDate(String date, String inputFormat) {
		SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
		try {
			return inputParser.parse(date);
		} catch (java.text.ParseException e) {
			return new Date(0);
		}
	}

	// DATABASE IMPORT
	@SuppressWarnings("resource")
	public static boolean importDB() {

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			if (sd.canWrite()) {

				String currentDBPath = "//data//" + "com.example.kurmanosiuntinys" + "//databases//" + "kurManoSiuntinys.db";
				String backupDBPath = "//SiuntuSekimas//" + "kurManoSiuntinys.db"; // From SD directory.
				File backupDB = new File(data, currentDBPath);
				File currentDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				// Toast.makeText(getApplicationContext(), "Import Successful!",
				// Toast.LENGTH_SHORT).show();
				return true;
			}
		} catch (Exception e) {

			// Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
			// .show();
			return false;
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static boolean exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String targetFolder = "//SiuntuSekimas//";
				String currentDBPath = "//data//" + "com.example.kurmanosiuntinys" + "//databases//" + "kurManoSiuntinys.db";
				String backupDBPath = "//SiuntuSekimas//" + "kurManoSiuntinys.db";

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				File folder = new File(sd, targetFolder);
				boolean success = true;
				if (!folder.exists()) {
					success = folder.mkdir();
					backupDB.createNewFile();
				}
				if (!success) {
					Log.v("BACKUP", "new file error");
					Log.v("BACKUP", Environment.getExternalStorageState());
					return false;
				}

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();

				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				// Toast.makeText(getApplicationContext(), "Backup Successful!",
				// Toast.LENGTH_SHORT).show();
				return true;
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
			// .show();
			Log.v("BACKUP", "ex: " + e);
			return false;
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static Boolean exportNums(String numbers) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			String backupNUMPath = "//SiuntuSekimas//" + "kurManoSiuntinys.txt";
			File backupNUM = new File(sd, backupNUMPath);
			FileChannel numFile = new FileOutputStream(backupNUM).getChannel();
			if (numbers != "") {
				numFile.write(ByteBuffer.wrap(numbers.getBytes("utf-8")));
			}
			numFile.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@SuppressWarnings("resource")
	public static String importNums() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			String backupNUMPath = "//SiuntuSekimas//" + "kurManoSiuntinys.txt"; // From SD directory.
			File currentNUM = new File(sd, backupNUMPath);
			FileChannel numFile = new FileInputStream(currentNUM).getChannel();
			ByteBuffer buff = ByteBuffer.allocate(1024);
			numFile.read(buff);
			String result = new String(buff.array(), Charset.forName("UTF-8"));
			Log.v("RESTORE", result);
			numFile.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
