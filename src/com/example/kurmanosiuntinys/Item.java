package com.example.kurmanosiuntinys;

public class Item {
	public String alias;
	public String number;
	public String date;
	public String place;
	public String explain;
	
	public Item(){
		
	}
	
	public Item(String alias, String number, String date, String place,
			String explain) {
		this.alias = alias;
		this.number = number;
		this.date = date;
		this.place = place;
		this.explain = explain;
	}

	public String getAlias() {
		return alias;
	}

	public String getNumber() {
		return number;
	}

	public String getDate() {
		return date;
	}

	public String getPlace() {
		return place;
	}

	public String getExplain() {
		return explain;
	}
	
	
}
