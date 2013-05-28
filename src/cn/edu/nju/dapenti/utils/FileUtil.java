package cn.edu.nju.dapenti.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class FileUtil {
	private static final String CACHE_DATA_FILE = "webviewCache.db";
	private static final String WEB_VIEW_CACHE_PATH = "webviewCache";

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static InputStream getInputStreamFromDatabase(Context context,
			String url) {
		SQLiteDatabase mDatabase = context.openOrCreateDatabase(
				CACHE_DATA_FILE, 0, null);
		if (mDatabase != null) {
			Cursor c = mDatabase.rawQuery("SELECT filepath FROM cache",
//					+ " WHERE lastmodify LIKE '%GMT' AND url = '" + url + "'",
					null);
			if (c != null) {
				if (c.moveToFirst()) {
					String fileName = c.getString(0);
					FileInputStream fis = null;
					if (fileName != null && fileName.trim().length() > 0) {
						try {
							String filePath = context.getCacheDir()
									+ File.separator + WEB_VIEW_CACHE_PATH
									+ File.separator + fileName;
							fis = new FileInputStream(filePath);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					return fis;
				}
				c.close();
			}
			mDatabase.close();
		}
		return null;
	}
}
