package com.example.kurmanosiuntinys;

public class ItemInfo {
	private String	itemNumber, date, place, explain;

	public ItemInfo() {
	}
	public ItemInfo(String itemNumber, String date, String place, String explain) {
		this.itemNumber = itemNumber;
		this.date = date;
		this.place = place;
		this.explain = explain;
	}
	
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getItemNumber() {
		return itemNumber;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPlace() {
		return place;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getExplain() {
		return explain;
	}

}
