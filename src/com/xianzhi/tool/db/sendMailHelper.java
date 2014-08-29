package com.xianzhi.tool.db;

import java.io.File;
import java.util.ArrayList;

import com.xianzhi.office.appPDF;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sendMailHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "app_db_order.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "sendmail_order";
	public final static String ID = "id";
	public final static String USERNAME = "userName";
	public final static String SUBJECT = "subject";
	public final static String CONTENT = "content";
	public final static String EMAILTO = "emailTo";
	public final static String CC = "cc";
	public final static String BCC = "bcc";
	public final static String SENTDATA = "sentDate";
	public final static String ACHMENT = "achment";

	public sendMailHelper(Context context) {
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
				+ " VARCHAR, " + SUBJECT + " VARCHAR, " + CONTENT
				+ " VARCHAR, " + EMAILTO + " VARCHAR, " + CC + " VARCHAR, "
				+ BCC + " VARCHAR, " + SENTDATA + " VARCHAR, " + ACHMENT
				+ " VARCHAR" + ");";

		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	public int delete_id(int id){
		sendMailHolder holder = selectData_id(id);
		String[] achments=holder.getAchment().split(" ");
		for(String achment:achments){
			new File(achment).delete();
		}
		synchronized (lock.Lock) {
			SQLiteDatabase db = getWritableDatabase();
			return db.delete(TABLE_NAME,  ID + "=" + id, null);
		}
	}
	public long insert(sendMailHolder content) {
		String[] achments=content.getAchment().split(" ");
		String achm="";
		for(String achment:achments){
			if(!getExtensionName(achment).equals("amr"))
				achm+=achment+" ";
		}
		synchronized (lock.Lock) {	
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(USERNAME, content.getUserName());
			cv.put(SUBJECT, content.getSubject());
			cv.put(CONTENT, content.getContent());
			cv.put(EMAILTO, content.getEmailTo());
			cv.put(CC, content.getCc());
			cv.put(BCC, content.getBcc());
			cv.put(SENTDATA, content.getSentDate());
			cv.put(ACHMENT, achm);
			long row = db.replace(TABLE_NAME, null, cv);
			return row;
		}
	}
	public String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return filename;   
    }
	public ArrayList<sendMailHolder> selectDataforList(String username,
			int start, int end) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, USERNAME + "=?",
				new String[] { username }, null, null, ID + " desc limit "
						+ start + "," + end);
		ArrayList<sendMailHolder> holderlist = new ArrayList<sendMailHolder>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_column = cursor.getColumnIndex(ID);
			int username_column = cursor.getColumnIndex(USERNAME);
			int subject_column = cursor.getColumnIndex(SUBJECT);
			int content_column = cursor.getColumnIndex(CONTENT);
			int sentDate_column = cursor.getColumnIndex(SENTDATA);
			int Attachment_column = cursor.getColumnIndex(ACHMENT);
			int cc_column = cursor.getColumnIndex(CC);
			int bcc_column = cursor.getColumnIndex(BCC);
			int emailto_column = cursor.getColumnIndex(EMAILTO);
			int id = cursor.getInt(id_column);
			String userName = cursor.getString(username_column);
			String subject = cursor.getString(subject_column);
			String content = cursor.getString(content_column);
			String sentDate = cursor.getString(sentDate_column);
			String achment = cursor.getString(Attachment_column);
			String cc = cursor.getString(cc_column);
			String bcc = cursor.getString(bcc_column);
			String emailto = cursor.getString(emailto_column);
			sendMailHolder contentHolder = new sendMailHolder(id, userName, subject
					, content, emailto, cc, bcc, sentDate, achment);
			holderlist.add(contentHolder);
		}
		cursor.close();
		return holderlist;
	}

	public sendMailHolder selectData_id(int v_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(v_id) }, null, null, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		int id_column = cursor.getColumnIndex(ID);
		int username_column = cursor.getColumnIndex(USERNAME);
		int subject_column = cursor.getColumnIndex(SUBJECT);
		int content_column = cursor.getColumnIndex(CONTENT);
		int sentDate_column = cursor.getColumnIndex(SENTDATA);
		int Attachment_column = cursor.getColumnIndex(ACHMENT);
		int cc_column = cursor.getColumnIndex(CC);
		int bcc_column = cursor.getColumnIndex(BCC);
		int emailto_column = cursor.getColumnIndex(EMAILTO);
		int id = cursor.getInt(id_column);
		String userName = cursor.getString(username_column);
		String subject = cursor.getString(subject_column);
		String content = cursor.getString(content_column);
		String sentDate = cursor.getString(sentDate_column);
		String achment = cursor.getString(Attachment_column);
		String cc = cursor.getString(cc_column);
		String bcc = cursor.getString(bcc_column);
		String emailto = cursor.getString(emailto_column);
		sendMailHolder contentHolder = new sendMailHolder(id, userName, subject
				, content, emailto, cc, bcc, sentDate, achment);
		cursor.close();
		return contentHolder;
	}
}
