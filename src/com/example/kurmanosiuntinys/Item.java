package com.example.kurmanosiuntinys;

import java.util.ArrayList;
import java.util.List;

public class Item {
	private String			alias, number, date;
	private Status			status;
	private List<ItemInfo>	itemInfo	= new ArrayList<ItemInfo>();

	enum Status {
		BLOGAS, NERA, VILNIUS, PASTE, PASIIMTA
	}

	public Item() {
	}

	public Item(String alias, String number, int status, String date) {
		this.alias = alias;
		this.number = number;
		this.status = Status.values()[status];
		this.date = date;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setStatus(int status) {
		this.status = Status.values()[status];
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	public int getStatusInt() {
		return status.ordinal();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setItemInfo(List<ItemInfo> itemInfo) {
		this.itemInfo = itemInfo;
	}

	public List<ItemInfo> getItemInfo() {
		return itemInfo;
	}

	public void addItemInfo(ItemInfo itemInfo) {
		this.itemInfo.add(itemInfo);
	}

	public ItemInfo getLastItemInfo() {
		return itemInfo != null && !itemInfo.isEmpty() ? itemInfo.get(itemInfo.size() - 1) : null;
	}

}
