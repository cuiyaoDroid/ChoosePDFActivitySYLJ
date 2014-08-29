package com.xianzhi.tool.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class mailContentHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "app_db_order.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "mail_order";
	public final static String ID = "id";
	public final static String USERNAME = "userName";
	public final static String UID = "uid";
	public final static String SUBJECT = "subject";
	public final static String CONTENT = "content";
	public final static String EMAILTO = "emailTo";
	public final static String CC = "cc";
	public final static String BCC = "bcc";
	public final static String READFLAG = "readFlag";
	public final static String FROMUSER = "fromUser";
	public final static String SENTDATA = "sentDate";
	public final static String HASATTACHMENT = "hasAttachment";
	public final static String ACHMENTCOUNT = "achmentcount";

	public mailContentHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		synchronized (lock.Lock) {
			onCreate(this.getWritableDatabase());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " integer primary key autoincrement, " + USERNAME
				+ " VARCHAR, " + UID + " INTEGER, " + SUBJECT + " VARCHAR, "
				+ CONTENT + " VARCHAR, " + EMAILTO + " VARCHAR, " + CC
				+ " VARCHAR, " + BCC + " VARCHAR, " + READFLAG + " INTEGER, "
				+ FROMUSER + " VARCHAR, " + SENTDATA + " VARCHAR, "
				+ HASATTACHMENT + " INTEGER, " + ACHMENTCOUNT + " INTEGER"
				+ ");";

		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public long insert(mailContentHolder content) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(ID, content.getId());
			cv.put(USERNAME, content.getUserName());
			cv.put(UID, content.getUid());
			cv.put(SUBJECT, content.getSubject());
			cv.put(CONTENT, content.getContent());
			cv.put(EMAILTO, content.getEmailTo());
			cv.put(CC, content.getCc());
			cv.put(BCC, content.getBcc());
			cv.put(READFLAG, content.getReadFlag());
			cv.put(FROMUSER, content.getFromUser());
			cv.put(SENTDATA, content.getSentDate());
			cv.put(HASATTACHMENT, content.getHasAttachment());
			cv.put(ACHMENTCOUNT, content.getAchmentcount());
			long row = db.replace(TABLE_NAME, null, cv);
			return row;
		}
	}

	public ArrayList<mailContentHolder> selectDataforList(String username,
			int start, int size) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, USERNAME + "=?",
				new String[] { username }, null, null, UID + " desc limit "
						+ start + "," + size);
		ArrayList<mailContentHolder> holderlist = new ArrayList<mailContentHolder>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_column = cursor.getColumnIndex(ID);
			int uid_column = cursor.getColumnIndex(UID);
			int username_column = cursor.getColumnIndex(USERNAME);
			int subject_column = cursor.getColumnIndex(SUBJECT);
			int content_column = cursor.getColumnIndex(CONTENT);
			int fromUser_column = cursor.getColumnIndex(FROMUSER);
			int sentDate_column = cursor.getColumnIndex(SENTDATA);
			int hasAttachment_column = cursor.getColumnIndex(HASATTACHMENT);
			int cc_column = cursor.getColumnIndex(CC);
			int readFlag_column = cursor.getColumnIndex(READFLAG);

			int id = cursor.getInt(id_column);
			int uid = cursor.getInt(uid_column);
			String userName = cursor.getString(username_column);
			String subject = cursor.getString(subject_column);
			String content = cursor.getString(content_column);
			String fromUser = cursor.getString(fromUser_column);
			String sentDate = cursor.getString(sentDate_column);
			int hasAttachment = cursor.getInt(hasAttachment_column);
			String cc = cursor.getString(cc_column);
			int readFlag = cursor.getInt(readFlag_column);

			mailContentHolder contentHolder = new mailContentHolder(id,
					userName, uid, subject, content, "", cc, "", readFlag,
					fromUser, sentDate, hasAttachment, 0);
			holderlist.add(contentHolder);
		}
		cursor.close();
		return holderlist;
	}

	public int delete_id(int id) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			return db.delete(TABLE_NAME, ID + "=" + id, null);
		}
	}

	public int selectIdforNoti(String username) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { ID }, USERNAME
				+ "=?", new String[] { username }, null, null, UID
				+ " desc limit " + 0 + "," + 1);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return -1;
		}
		int id_column = cursor.getColumnIndex(ID);
		int id = cursor.getInt(id_column);
		cursor.close();
		return id;
	}

	public int updataReadFlag(int v_id, boolean ifread) {
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			String[] args = { String.valueOf(v_id) };
			ContentValues cv = new ContentValues();
			cv.put(READFLAG, ifread ? 1 : 0);
			return db.update(TABLE_NAME, cv, ID + "=?", args);
		}
	}

	public mailContentHolder selectData_id(int v_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(v_id) }, null, null, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		int id_column = cursor.getColumnIndex(ID);
		int uid_column = cursor.getColumnIndex(UID);
		int username_column = cursor.getColumnIndex(USERNAME);
		int subject_column = cursor.getColumnIndex(SUBJECT);
		int content_column = cursor.getColumnIndex(CONTENT);
		int fromUser_column = cursor.getColumnIndex(FROMUSER);
		int sentDate_column = cursor.getColumnIndex(SENTDATA);
		int hasAttachment_column = cursor.getColumnIndex(HASATTACHMENT);
		int emailto_column = cursor.getColumnIndex(EMAILTO);
		int cc_column = cursor.getColumnIndex(CC);
		int readFlag_column = cursor.getColumnIndex(READFLAG);

		int id = cursor.getInt(id_column);
		int uid = cursor.getInt(uid_column);
		String userName = cursor.getString(username_column);
		String subject = cursor.getString(subject_column);
		String content = cursor.getString(content_column);
		String fromUser = cursor.getString(fromUser_column);
		String sentDate = cursor.getString(sentDate_column);
		String emailto = cursor.getString(emailto_column);
		int hasAttachment = cursor.getInt(hasAttachment_column);
		String cc = cursor.getString(cc_column);
		int readFlag = cursor.getInt(readFlag_column);

		mailContentHolder contentHolder = new mailContentHolder(id, userName,
				uid, subject, content, emailto, cc, "", readFlag, fromUser,
				sentDate, hasAttachment, 0);
		cursor.close();
		return contentHolder;
	}
}
