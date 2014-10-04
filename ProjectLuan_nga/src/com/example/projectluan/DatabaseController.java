package com.example.projectluan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseController extends SQLiteOpenHelper {
	
	private static String DB_PATH = "data/data/com.example.projectluan/databases";
	private static String DB_NAME = "events.db";

	//-----------------------table events--------------------------------//
	
	protected static String TABLE_NAME = "MYEVENTS";
	protected static String ID_COL = "_id";
	protected static String NAME_EVENT_COL = "name_event";
	protected static String DATE_COL = "date";
	protected static String HOUR_COL = "hour";
	protected static String LOCATION_COL = "location";
	protected static String NOTE_COL = "note";
	protected static String LOOP_COL = "loop";
	protected static String REMINDERS_COL = "reminders";
	protected static String RINGTONE_COL = "ringtone";
	protected static String RINGTONE_PATH_COL = "ringtone_path";
	protected static String SILENCE_AFTER_COL = "silence";
	protected static String IDMAX_COL = "idmax";
	
	
	//------------------------------Table Alarm----------------------------------//
	protected static String TABLE_NAME_AL = "ALARM";
	protected static String ID_AL_COL = "_id";
	protected static String LABEL_AL_COL = "label";
	protected static String HOUR_AL_COL = "hour_alarm";
	protected static String MINUTE_AL_COL = "minute_alarm";
	protected static String REPEAT_AL_COL = "repeat";
	protected static String RINGTONE_AL_COL = "ringtone";
	protected static String RINGTONE_PATH_AL_COL = "ringtone_path";
	protected static String SNOOZE_AL_COL = "snooze";
	protected static String DISMISS_METHOD_AL_COL = "dismiss_method";
	protected static String ENABLE_AL_COL = "enable";
	protected static String STATUS_AL_COL = "status";
	protected static String IDMAX_AL_COL = "idmax";	
	
	
	private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "( " + ID_COL
			+ " INTEGER PRIMARY KEY autoincrement, "
			+ NAME_EVENT_COL + " text, "
			+ LOCATION_COL + " text, "
			+ NOTE_COL + " text, "
			+ LOOP_COL + " text, "
			+ REMINDERS_COL + " text, "
			+ RINGTONE_COL + " text, "
			+ RINGTONE_PATH_COL + " text, "
			+ SILENCE_AFTER_COL + " text, "
			+ HOUR_COL + " DateTime, "
			+ IDMAX_COL + " INTEGER, "
			+ DATE_COL + " DateTime );";

	
	private static String CREATE_TABLE_ALARM = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_AL + " ( " 
			+ ID_AL_COL + " INTEGER PRIMARY KEY autoincrement ,"
			+ LABEL_AL_COL + " Text ,"
			+ HOUR_AL_COL + " INTEGER ,"
			+ MINUTE_AL_COL + " INTEGER ,"
			+ REPEAT_AL_COL + " Text ,"
			+ RINGTONE_AL_COL + " Text , "
			+ RINGTONE_PATH_AL_COL + " Text , "
			+ SNOOZE_AL_COL + " Text ,"
			+ DISMISS_METHOD_AL_COL + " Text ,"
			+ ENABLE_AL_COL + " Text ,"
			+ STATUS_AL_COL + " INTEGER ,"
			+ IDMAX_AL_COL + " INTEGER );";
	
	private static SQLiteDatabase myDataBase;

	public DatabaseController(Context context) {
		super(context, DB_NAME, null, 1);
		Log.d("LuanDT","Constructor DatabaseController");
		myDataBase = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("LuanDT","onCreate");
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_TABLE_ALARM);
//		insertDefault(db);
		
	}

	private void insertDefault(SQLiteDatabase db) {
		if(db != null) {
			ContentValues values = new ContentValues();
			values.put(NAME_EVENT_COL, "Ve que");
			values.put(DATE_COL, "       Th 2 22-09-2014");
			values.put(HOUR_COL, "       14:38");
			values.put(LOCATION_COL, "Yen Bai");
			values.put(NOTE_COL, "lâu rồi không về nhà, về chơi và giải quyêt chút việc riêng");
			values.put(LOOP_COL, "Sự kiện diễn ra một lần");
			values.put(REMINDERS_COL, "10 phút trước sự kiện");
			values.put(RINGTONE_COL, "Bell Ring");
			values.put(SILENCE_AFTER_COL, "30 giây");
			db.insert(TABLE_NAME, null, values);
			
			Log.d("LuanDT","INSERT DEFAULT");
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d("LuanDT","onUpgrade");
	
	}
	
	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}
	
	private static void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		
		try {
			myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("LuanDT", "Exception while trying to open database!");
		}
	}
	
	public Cursor rawQuery(String  stSQL, String[] data){
		if(myDataBase == null) openDataBase();
		
		Cursor cursor = myDataBase.rawQuery(stSQL, data);
		return cursor;
	}
	
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy){
		
		if(myDataBase == null) openDataBase();
		
		Cursor cursor = myDataBase.query(table, columns, selection, selectionArgs, 
				groupBy, having, orderBy);
		return cursor;
	}

	public static long insert(String table, String nullColumnHack, ContentValues values) {
		if(myDataBase == null)  openDataBase();
		
		long insertRet = myDataBase.insert(table, nullColumnHack, values);
		if(insertRet == -1)
			Log.d("LuanDT", "insert db error");
		return insertRet;
		
	}
	
	public int delete(String whereClause, String[] whereArgs){
		if(myDataBase == null)
			openDataBase();
		int ret = myDataBase.delete(TABLE_NAME, whereClause, whereArgs);
		return ret;
		
	}
	
	public int deleteByID (String[] id){
		int ret = -1;
		String whereClause = ID_COL + " IN (";
		for (int i = 0; i < id.length; i++) {
			whereClause += "?,";
		}
		whereClause =  whereClause.substring(0, whereClause.length() - 1);
		whereClause += ") ";
		
		String[] whereArgs = id;
		
		ret = delete(whereClause, whereArgs);
		return ret;
	}
	
	
	
	public int update (String table, ContentValues values, String whereClause, String[] whereArgs){
		if(myDataBase == null)
			openDataBase();
		String whereClause1 = ID_COL + " IN (";
			whereClause1 += "?,";
		whereClause1 =  whereClause1.substring(0, whereClause1.length() - 1);
		whereClause1 += ") ";
		
		int ret = myDataBase.update(TABLE_NAME, values, whereClause1, whereArgs );
		return ret;
		
	}
	
	//--------------------------------------------//
	
	public int deleteAlarm(String whereClause, String[] whereArgs){
		if(myDataBase == null)
			openDataBase();
		int ret = myDataBase.delete(TABLE_NAME_AL, whereClause, whereArgs);
		
		return ret;
		
	}
	
	public int deleteAlarmByID (String[] id){
		int ret = -1;
		String whereClause = ID_AL_COL + " IN (";
		for (int i = 0; i < id.length; i++) {
			whereClause += "?,";
		}
		whereClause =  whereClause.substring(0, whereClause.length() - 1);
		whereClause += ") ";
		
		String[] whereArgs = id;
		
		ret = deleteAlarm(whereClause, whereArgs);
		return ret;
	}
	
	
	
	public static int updateAlarm (String table, ContentValues values, String whereClause, String[] whereArgs){
		if(myDataBase == null)
			openDataBase();
		String whereClause1 = ID_AL_COL + " IN (";
			whereClause1 += "?,";
		whereClause1 =  whereClause1.substring(0, whereClause1.length() - 1);
		whereClause1 += ") ";
		
		int ret = myDataBase.update(TABLE_NAME_AL, values, whereClause1, whereArgs );
		return ret;
		
	}

}
