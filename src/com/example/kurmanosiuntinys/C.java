package com.example.kurmanosiuntinys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class C {
	public static final String IP = "192.168.1.200";
	public static final int PORT = 7000;
	enum Type {
		STATUS, INFO, OTHER// 0, 1, 2
	}
	enum ServerStatus {
		Disconnected, Connecting, Connected
	}
	
	public static String getDate(){
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
	}
	
}
