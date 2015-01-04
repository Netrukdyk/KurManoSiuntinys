package com.example.kurmanosiuntinys;

public class Item {
	public String	alias, number, date, place, explain;
	public Status	status;

	enum Status {
		NERA, VILNIUS, PASTE// 0, 1, 2
	}

	public Item() {
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

	public Status getStatus() {
		return status;
	}

}
