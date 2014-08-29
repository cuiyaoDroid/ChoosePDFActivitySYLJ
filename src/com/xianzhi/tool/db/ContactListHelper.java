package com.xianzhi.tool.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactListHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "app_db_order.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "contactlist_order";
	public final static String ID = "id";
	public final static String NAME = "name";
	public final static String ADDRESS = "address";

	public ContactListHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		synchronized (lock.Lock) {
			onCreate(this.getWritableDatabase());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " integer primary key autoincrement, " + NAME + " VARCHAR, "
				+ ADDRESS + " VARCHAR" + ");";

		db.execSQL(sql);
	}

	public long insert(ContactListHolder content) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(ID, content.getId());
			cv.put(NAME, content.getName());
			cv.put(ADDRESS, content.getAddress());
			long row = db.replace(TABLE_NAME, null, cv);
			return row;
		}
	}
	public long autoidinsert(ContactListHolder content) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(NAME, content.getName());
			cv.put(ADDRESS, content.getAddress());
			long row = db.replace(TABLE_NAME, null, cv);
			return row;
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public ArrayList<ContactListHolder> selectData(String content) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, NAME + " like '%" + content
				+ "%' OR " + ADDRESS + " like '%" + content + "%'", null, null,
				null, ID + " asc limit " + 0 + "," + 5);
		ArrayList<ContactListHolder> holderlist = new ArrayList<ContactListHolder>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_column = cursor.getColumnIndex(ID);
			int name_column = cursor.getColumnIndex(NAME);
			int address_column = cursor.getColumnIndex(ADDRESS);

			int id = cursor.getInt(id_column);
			String name = cursor.getString(name_column);
			String address = cursor.getString(address_column);
			ContactListHolder holder = new ContactListHolder(id, name, address);
			holderlist.add(holder);
		}
		cursor.close();
		return holderlist;
	}
}
