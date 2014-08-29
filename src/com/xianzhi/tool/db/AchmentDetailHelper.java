package com.xianzhi.tool.db;

import java.io.File;
import java.util.ArrayList;

import com.xianzhi.office.appPDF;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AchmentDetailHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "app_db_order.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "attactment_order";
	public final static String ID = "id";
	public final static String NAME = "name";
	public final static String INBOXID = "inboxId";
	public final static String PATH = "path";
	public final static String SIZE = "size";
	public final static String DOWNLOADFLAG = "dowloadflag";
	public final static String EDITNAME = "editname";
	public AchmentDetailHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		synchronized (lock.Lock) {
			onCreate(this.getWritableDatabase());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " integer primary key autoincrement, " + INBOXID
				+ " INTEGER, " + PATH + " VARCHAR, " + SIZE + " LONG, " + NAME
				+ " VARCHAR, "+ EDITNAME+ " VARCHAR, " + DOWNLOADFLAG + " INTEGER" + ");";

		db.execSQL(sql);
	}

	public long insert(AchmentDetailHolder content) {
		boolean downloadflag = selectDownloadFlag(content.getId());
		if(downloadflag){
			return -1;
		}
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(ID, content.getId());
			cv.put(NAME, ifFilenameisUse(content.getName())?content.getId()+"__"+content.getName():content.getName());
			cv.put(INBOXID, content.getInboxId());
			cv.put(PATH, content.getPath());
			cv.put(SIZE, content.getSize());
			cv.put(DOWNLOADFLAG, downloadflag);
			cv.put(EDITNAME, content.getEditname());
			long row = db.replace(TABLE_NAME, null, cv);
			return row;
		}
	}

	public int updataDowloadFlag(int v_id, boolean ifdownload) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			String[] args = { String.valueOf(v_id) };
			ContentValues cv = new ContentValues();
			cv.put(DOWNLOADFLAG, ifdownload ? 1 : 0);
			return db.update(TABLE_NAME, cv, ID + "=?", args);
		}
	}
	public int updataEditName(int v_id, String editname) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			String[] args = { String.valueOf(v_id) };
			ContentValues cv = new ContentValues();
			cv.put(EDITNAME, editname);
			return db.update(TABLE_NAME, cv, ID + "=?", args);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	public Boolean ifFilenameisUse(String filename) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, NAME + "=?",
				new String[] { filename }, null, null, ID + " asc");
		if (!cursor.moveToFirst()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
	public String selectEditName(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(id) }, null, null, ID + " asc");
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		int editname_column = cursor.getColumnIndex(EDITNAME);
		String editname = cursor.getString(editname_column);
		cursor.close();
		return editname;
	}
	private boolean selectDownloadFlag(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(id) }, null, null, ID + " asc");
		if (!cursor.moveToFirst()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	public ArrayList<AchmentDetailHolder> selectData_inboxId(int v_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, INBOXID + "=?",
				new String[] { String.valueOf(v_id) }, null, null, ID + " asc");
		ArrayList<AchmentDetailHolder> holderlist = new ArrayList<AchmentDetailHolder>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_column = cursor.getColumnIndex(ID);
			int name_column = cursor.getColumnIndex(NAME);
			int inbox_column = cursor.getColumnIndex(INBOXID);
			int path_column = cursor.getColumnIndex(PATH);
			int size_column = cursor.getColumnIndex(SIZE);
			int downloadflag_column = cursor.getColumnIndex(DOWNLOADFLAG);
			int editname_column = cursor.getColumnIndex(EDITNAME);
			
			int id = cursor.getInt(id_column);
			String name = cursor.getString(name_column);
			int inboxId = cursor.getInt(inbox_column);
			String path = cursor.getString(path_column);
			Long size = cursor.getLong(size_column);
			int downloadflag = cursor.getInt(downloadflag_column);
			String editname = cursor.getString(editname_column);
			AchmentDetailHolder holder = new AchmentDetailHolder(id, name,
					inboxId, path, size, downloadflag,editname);
			holderlist.add(holder);
		}
		cursor.close();
		return holderlist;
	}
	public int delete_id(int id){
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			return db.delete(TABLE_NAME,  ID + "=" + id, null);
		}
	}
	public void delete_inboxid(int v_id){
		ArrayList<AchmentDetailHolder> holderlist=selectData_inboxId(v_id);
		for(AchmentDetailHolder holder:holderlist){
			File Achment=new File(appPDF.appPath+holder.getName());
			if(Achment.exists()){
				Achment.delete();
			}
			File EditAchment=new File(appPDF.appPath+holder.getEditname());
			if(EditAchment.exists()){
				EditAchment.delete();
			}
			delete_id(holder.getId());
		}
	}
	
	public AchmentDetailHolder selectData_Id(int v_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(v_id) }, null, null, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		int id_column = cursor.getColumnIndex(ID);
		int name_column = cursor.getColumnIndex(NAME);
		int inbox_column = cursor.getColumnIndex(INBOXID);
		int path_column = cursor.getColumnIndex(PATH);
		int size_column = cursor.getColumnIndex(SIZE);
		int downloadflag_column = cursor.getColumnIndex(DOWNLOADFLAG);
		int editname_column = cursor.getColumnIndex(EDITNAME);
		int id = cursor.getInt(id_column);
		String name = cursor.getString(name_column);
		int inboxId = cursor.getInt(inbox_column);
		String path = cursor.getString(path_column);
		Long size = cursor.getLong(size_column);
		int downloadflag = cursor.getInt(downloadflag_column);
		String editname = cursor.getString(editname_column);
		AchmentDetailHolder holder = new AchmentDetailHolder(id, name, inboxId,
				path, size, downloadflag,editname);
		cursor.close();
		return holder;
	}
}
