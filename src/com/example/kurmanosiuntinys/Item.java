package com.example.kurmanosiuntinys;

import java.util.ArrayList;
import java.util.List;

public class Item {
	private String			alias, number;
	private Status			status;
	private List<ItemInfo>	itemInfo	= new ArrayList<ItemInfo>();

	enum Status {
		BLOGAS, NERA, VILNIUS, PASTE, PASIIMTA
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

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
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
