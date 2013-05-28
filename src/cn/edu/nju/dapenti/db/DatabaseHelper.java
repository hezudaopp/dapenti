package cn.edu.nju.dapenti.db;

import cn.edu.nju.dapenti.Settings;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "dapenti.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTERGER_TYPE = " INTEGER";
	private static final String DEFAULT = " DEFAULT";
	private static final String ZERO = " 0";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES[] = {
			"CREATE TABLE IF NOT EXISTS " + RSSFeedEntry.TABLE_NAME + " ("
					+ RSSFeedEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
					+ RSSFeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
					+ RSSFeedEntry.COLUMN_NAME_PUBDATE + TEXT_TYPE + COMMA_SEP
					+ RSSFeedEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP
					+ RSSFeedEntry.COLUMN_NAME_NAME + TEXT_TYPE + " );",
			"CREATE TABLE IF NOT EXISTS " + RSSItemEntry.TABLE_NAME + " ("
					+ RSSItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
					+ COMMA_SEP + RSSItemEntry.COLUMN_NAME_TITLE + TEXT_TYPE
					+ COMMA_SEP + RSSItemEntry.COLUMN_NAME_DESCRIPTION
					+ TEXT_TYPE + COMMA_SEP + RSSItemEntry.COLUMN_NAME_PUBDATE
					+ TEXT_TYPE + COMMA_SEP + RSSItemEntry.COLUMN_NAME_AUTHOR
					+ TEXT_TYPE + COMMA_SEP + RSSItemEntry.COLUMN_NAME_LINK
					+ TEXT_TYPE + COMMA_SEP + RSSItemEntry.COLUMN_NAME_FEED_ID
					+ INTERGER_TYPE + COMMA_SEP + RSSItemEntry.COLUMN_NAME_FAV
					+ INTERGER_TYPE + DEFAULT + ZERO + COMMA_SEP + RSSItemEntry.COLUMN_NAME_CONTENT
					+ TEXT_TYPE  + COMMA_SEP + RSSItemEntry.COLUMN_NAME_FAVTIME
					+ TEXT_TYPE + " );" };
	private static final String SQL_DELETE_ENTRIES[] = {
			"DROP TABLE IF EXISTS " + RSSFeedEntry.TABLE_NAME,
			"DROP TABLE IF EXISTS " + RSSItemEntry.TABLE_NAME };

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		for (String createSql : SQL_CREATE_ENTRIES) {
			db.execSQL(createSql);
		}
		ContentValues cv;
		for (Integer o : Settings.ID_URL_MAP.keySet()) {
			cv = new ContentValues();
			cv.put(RSSFeedEntry._ID, o.toString());
			cv.put(RSSFeedEntry.COLUMN_NAME_URL, Settings.ID_URL_MAP.get(o));
			cv.put(RSSFeedEntry.COLUMN_NAME_NAME, Settings.ID_NAME_MAP.get(o));
			db.insert(RSSFeedEntry.TABLE_NAME, null, cv);
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		for (String deleteSql : SQL_DELETE_ENTRIES) {
			db.execSQL(deleteSql);
		}
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	/*
	 * public SQLiteDatabase getReadableDatabase(SQLiteDatabase db) { if (db ==
	 * null || !db.isOpen()) return super.getReadableDatabase(); return db; }
	 * 
	 * public SQLiteDatabase getWritableDatabase(SQLiteDatabase db) { if (db ==
	 * null || !db.isOpen() || db.isReadOnly()) return
	 * super.getWritableDatabase(); return db; }
	 */
}
