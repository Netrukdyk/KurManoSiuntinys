package com.example.kurmanosiuntinys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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

		if (p.matcher(number).find())
			return true;

		return false;
	}

	// DATABASE IMPORT
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
					Log.v("BACKUP","new file error");
					Log.v("BACKUP",Environment.getExternalStorageState());
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
			Log.v("BACKUP","ex: "+e);
			return false;
		}
		return false;
	}

}
