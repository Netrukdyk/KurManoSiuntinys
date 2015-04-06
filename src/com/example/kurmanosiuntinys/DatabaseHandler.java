package com.example.kurmanosiuntinys;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int	DATABASE_VERSION	= 7;						// Database Version
	private static final String	DATABASE_NAME		= "kurManoSiuntinys.db";	// Database Name
	private static final String	TABLE_ITEMS			= "items";					// Items table name
	private static final String	TABLE_ITEMS_INFO	= "itemsInfo";				// Items table name

	// Items Table Columns names
	private static final String	KEY_ID				= "id";
	private static final String	KEY_ALIAS			= "alias";
	private static final String	KEY_NUMBER			= "number";
	private static final String	KEY_STATUS			= "status";
	private static final String	KEY_DATE			= "date";

	// ItemsInfo Table Columns names
	private static final String	KEY2_ID				= "id";
	private static final String	KEY2_ITEM_NUMBER	= "item_number";
	private static final String	KEY2_DATE			= "date";
	private static final String	KEY2_PLACE			= "place";
	private static final String	KEY2_EXPLAIN		= "explain";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_ALIAS + " TEXT," + KEY_NUMBER
				+ " TEXT UNIQUE," + KEY_STATUS + " INTEGER," + KEY_DATE + " TEXT" + ")";
		db.execSQL(CREATE_ITEMS_TABLE);

		String CREATE_ITEMS_INFO_TABLE = "CREATE TABLE " + TABLE_ITEMS_INFO + "(" + KEY2_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY2_ITEM_NUMBER + " INTEGER,"
				+ KEY2_DATE + " TEXT," + KEY2_PLACE + " TEXT," + KEY2_EXPLAIN + " TEXT" + ")";
		db.execSQL(CREATE_ITEMS_INFO_TABLE);
		
		String CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX unique_index ON "+TABLE_ITEMS_INFO+" ("+KEY2_DATE+","+KEY2_PLACE+","+KEY2_EXPLAIN+")";
		db.execSQL(CREATE_UNIQUE_INDEX);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS); // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_INFO); // Drop older table if existed
		onCreate(db); // Create tables again
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Getting single item
	public Item getItem(String number) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ALIAS, KEY_NUMBER, KEY_STATUS, KEY_DATE }, KEY_NUMBER + "=?", new String[] { number }, null, null, null,
				null);
		if (cursor != null)
			cursor.moveToFirst();
		else return null;
		Item item = new Item(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3));
		item.setItemInfo(getItemInfo(number));
		return item;
	}

	// Getting All Items
	public List<Item> getAllItems(Boolean excludeTaken, Boolean reverse, Boolean excludeOld) {
		List<Item> itemList = new ArrayList<Item>();
		String condition = (excludeTaken) ? " WHERE "+KEY_STATUS+"!=4" : "";
		
		condition += (!excludeTaken && excludeOld) ? " WHERE "+KEY_DATE+"> date('now','-30 day')" : "";		
		condition += (reverse) ? " ORDER BY "+KEY_ID+" DESC" : "";

		// Select All Query
		String selectQuery = "SELECT *, date('now','-30 day') AS xxx FROM " + TABLE_ITEMS + condition;
		Log.v("Database", selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do { // looping through all rows and adding to list
				Log.v("Database", cursor.getInt(0)+" "+cursor.getString(4) + " "+ cursor.getString(5));
				Item item = new Item();
				item.setAlias(cursor.getString(1));
				item.setNumber(cursor.getString(2));
				item.setStatus(Integer.parseInt(cursor.getString(3)));
				item.setDate(cursor.getString(4));
				item.setItemInfo(getItemInfo(item.getNumber()));

				itemList.add(item); // Adding contact to list
			} while (cursor.moveToNext());
		}
		return itemList;
	}
	
	// Get only all number
	public String getAllItemLite(){
		String result = "";
		// Select All Query
		String selectQuery = "SELECT "+KEY_NUMBER+", "+KEY_ALIAS+" FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do { // looping through all rows and adding to list
				result += cursor.getString(0)+"|"+cursor.getString(1)+"\n";
			} while (cursor.moveToNext());
		}		
		return result;
	}

	// Adding new item
	void addItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ALIAS, item.getAlias());
		values.put(KEY_NUMBER, item.getNumber());
		values.put(KEY_STATUS, item.getStatusInt());
		values.put(KEY_DATE, C.getDate());

		db.insert(TABLE_ITEMS, null, values); // Inserting Row
		//db.close(); // Closing database connection
	}

	// Updating single item
	public int updateItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		int x = this.getItemInfo(item.getNumber()).size();
		Log.v("DB BUVO",x+"");
		int y = item.getItemInfo().size();
		Log.v("DB YRA",y+"");
		ContentValues values = new ContentValues();
		values.put(KEY_ALIAS, item.getAlias());
		values.put(KEY_NUMBER, item.getNumber());
		values.put(KEY_STATUS, item.getStatusInt());		
		addItemsInfo(item.getItemInfo());
		// updating row
		db.update(TABLE_ITEMS, values, KEY_NUMBER + " = ?", new String[] { String.valueOf(item.getNumber()) });
		return y-x;
	}

	// Update all list
	public List<Item> updateItems(List<Item> items) {
		List<Item> updatedItems = new ArrayList<Item>();
		for (Item item : items) {
			if(updateItem(item) > 0)
				updatedItems.add(item);
		}
		return updatedItems;
	}

	// Deleting single item
	public void deleteItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_NUMBER + " = ?", new String[] { String.valueOf(item.getNumber()) });
		db.close();
	}

	// Getting items Count
	public int getItemsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	public void removeAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, null, null);
		db.delete(TABLE_ITEMS_INFO, null, null);
	}

	// --- ITEM INFO metodai-------------------------------------------------------
	public List<ItemInfo> getItemInfo(String itemNumber) {
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ITEMS_INFO, new String[] { KEY2_ITEM_NUMBER, KEY2_DATE, KEY2_PLACE, KEY2_EXPLAIN }, KEY2_ITEM_NUMBER + "=?",
				new String[] { String.valueOf(itemNumber) }, null, null,  KEY2_DATE+" DESC", null);
		if (cursor.moveToFirst()) {
			do { // looping through all rows and adding to list
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setItemNumber(cursor.getString(0));
				itemInfo.setDate(cursor.getString(1));
				itemInfo.setPlace(cursor.getString(2));
				itemInfo.setExplain(cursor.getString(3));

				itemInfoList.add(itemInfo); // Adding itemInfo to list
			} while (cursor.moveToNext());
		}
		return itemInfoList;
	}

	public void addItemsInfo(List<ItemInfo> itemsInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(ItemInfo itemInfo : itemsInfo){		
			ContentValues values = new ContentValues();
			values.put(KEY2_ITEM_NUMBER, itemInfo.getItemNumber());
			values.put(KEY2_DATE, itemInfo.getDate());
			values.put(KEY2_PLACE, itemInfo.getPlace());
			values.put(KEY2_EXPLAIN, itemInfo.getExplain());

			//db.insert(TABLE_ITEMS_INFO, null, values); // Inserting Row		
			db.insertWithOnConflict(TABLE_ITEMS_INFO, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
		//db.close(); // Closing database connection
	}

	public void updateItemInfo(int item_id) {
		

	}

	public void deleteItemInfo(int item_id) {

	}

}
