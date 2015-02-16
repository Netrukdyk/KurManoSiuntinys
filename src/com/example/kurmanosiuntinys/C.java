package com.example.kurmanosiuntinys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.graphics.Color;

public class C {
	public static final String IP = "192.168.1.200";
	public static final int PORT = 7000;
	public static final String PREFS = "Settings";
	public static final int RED = Color.argb(88, 215, 88, 88);
	public static final int GREEN = Color.argb(88, 88, 215, 88);
	enum Type {
		STATUS, INFO, OTHER// 0, 1, 2
	}
	enum ServerStatus {
		Disconnected, Connecting, Connected
	}
	
	public static String getDate(){
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
	}
	
	public static Boolean checkNumber(String number){
	    String re1="([a-z][a-z][0-9]{8,9}[a-z][a-z])";
	    Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    
	    if (p.matcher(number).find())
	    	return true;
	    
	    return false;
	}
	
	
}
