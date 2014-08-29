package com.xianzhi.tool.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactListStaticHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "app_db_order.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "contactstaticlist_order";
	public final static String ID = "id";
	public final static String NAME = "name";
	public final static String ADDRESS = "address";
	public final static String FLAG = "flag";
	public final static String CHILDIDS="childids";
	public final static String FATHERID="fatherid";
	
	public final static int provinceFLAG=0;
	public final static int departmentFLAG=1;
	public final static int EmployeeFLAG=2;
	public ContactListStaticHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		synchronized (lock.Lock) {
			onCreate(this.getWritableDatabase());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + 
				ID + " integer primary key autoincrement, " + 
				NAME + " VARCHAR, " + 
				ADDRESS + " VARCHAR, " +
				CHILDIDS + " VARCHAR, " +
				FATHERID + " INTEGER, " +
				FLAG + " INTEGER" +
				");";

		db.execSQL(sql);
	}

	public void clear() {
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getReadableDatabase();
			String dropsql = " DROP TABLE IF EXISTS " + TABLE_NAME;
			db.execSQL(dropsql);
			String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
					+ " integer primary key autoincrement, " + NAME
					+ " VARCHAR, " + ADDRESS + " VARCHAR, " + CHILDIDS
					+ " VARCHAR, " + FATHERID + " INTEGER, " + FLAG
					+ " INTEGER" + ");";

			db.execSQL(sql);
		}
	}
	public long insert(ContactListStaticHolder content) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(ID, content.getId());
			cv.put(NAME, content.getName());
			cv.put(ADDRESS, content.getAddress());
			cv.put(CHILDIDS, content.getChildids());
			cv.put(FATHERID, content.getFatherid());
			cv.put(FLAG, content.getFlag());
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
	
	public ContactListStaticHolder selectId(int vid) {
		synchronized (lock.Lock) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID +"=?", new String[]{String.valueOf(vid)}, null,
				null, ID + " desc");
		if (!cursor.moveToFirst()) {
			return null;
		}
		int id_column = cursor.getColumnIndex(ID);
		int name_column = cursor.getColumnIndex(NAME);
		int address_column = cursor.getColumnIndex(ADDRESS);
		int childids_column = cursor.getColumnIndex(CHILDIDS);
		int fatherid_column = cursor.getColumnIndex(FATHERID);
		int flag_column = cursor.getColumnIndex(FLAG);

		int id = cursor.getInt(id_column);
		String name = cursor.getString(name_column);
		String address = cursor.getString(address_column);
		String childids = cursor.getString(childids_column);
		int flag = cursor.getInt(flag_column);
		int fatherid = cursor.getInt(fatherid_column);
		ContactListStaticHolder holder = new ContactListStaticHolder(id, name,
				address, flag, childids, fatherid);
		cursor.close();
		
		return holder;
		}
	}
	public ArrayList<ContactListStaticHolder> selectflag(int vflag) {
		synchronized (lock.Lock) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, FLAG +"=?", new String[]{String.valueOf(vflag)}, null,
				null, ID + " desc");
		ArrayList<ContactListStaticHolder> holderlist = new ArrayList<ContactListStaticHolder>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_column = cursor.getColumnIndex(ID);
			int name_column = cursor.getColumnIndex(NAME);
			int address_column = cursor.getColumnIndex(ADDRESS);
			int childids_column = cursor.getColumnIndex(CHILDIDS);
			int fatherid_column = cursor.getColumnIndex(FATHERID);
			int flag_column = cursor.getColumnIndex(FLAG);
			
			int id = cursor.getInt(id_column);
			String name = cursor.getString(name_column);
			String address = cursor.getString(address_column);
			String childids = cursor.getString(childids_column);
			int flag = cursor.getInt(flag_column);
			int fatherid = cursor.getInt(fatherid_column);
			ContactListStaticHolder holder = new ContactListStaticHolder(id, name, address, flag, childids, fatherid);
			holderlist.add(holder);
		}
		cursor.close();
		return holderlist;
		}
	}
}
