package cn.edu.nju.dapenti.db;

import cn.edu.nju.dapenti.Settings;
import cn.edu.nju.dapenti.entity.RSSFeed;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.entity.RSSItemAuthor;
import cn.edu.nju.dapenti.entity.RSSItemDescription;
import cn.edu.nju.dapenti.entity.RSSItemInterface;
import cn.edu.nju.dapenti.entity.RSSItemLink;
import cn.edu.nju.dapenti.entity.RSSItemPubdate;
import cn.edu.nju.dapenti.entity.RSSItemTitle;
import cn.edu.nju.dapenti.rss.RSSItemInterfaceFactoryMethod;
import cn.edu.nju.dapenti.rss.RSSItemInterfaceFactoryXmlPull;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.nju.dapenti.utils.StringUtil;

public class DatabaseUtil {
	private static DatabaseHelper databaseHelper = null;
//	private static DatabaseHelper mInstance = null;
	// Define a projection that specifies which columns from the database
	// you will actually use after this query.
	private String[] feedProjection = {
	    RSSFeedEntry._ID,
	    RSSFeedEntry.COLUMN_NAME_TITLE,
	    RSSFeedEntry.COLUMN_NAME_PUBDATE,
	    RSSFeedEntry.COLUMN_NAME_URL,
	    RSSFeedEntry.COLUMN_NAME_NAME
	    };
	private String[] itemProjection = {
	    RSSItemEntry._ID,
	    RSSItemEntry.COLUMN_NAME_TITLE,
	    RSSItemEntry.COLUMN_NAME_DESCRIPTION,
	    RSSItemEntry.COLUMN_NAME_PUBDATE,
	    RSSItemEntry.COLUMN_NAME_AUTHOR,
	    RSSItemEntry.COLUMN_NAME_LINK,
	    RSSItemEntry.COLUMN_NAME_FEED_ID,
	    RSSItemEntry.COLUMN_NAME_FAV,
	    RSSItemEntry.COLUMN_NAME_CONTENT,
	    RSSItemEntry.COLUMN_NAME_FAVTIME
	    };
	
	public static DatabaseHelper getInstance(Context ctx) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(ctx.getApplicationContext());
		}
		return databaseHelper;
	}
	
	private DatabaseUtil(Context context){
//		this.databaseHelper = new DatabaseHelper(context);
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context.getApplicationContext());
		}
	}
	
	public static DatabaseUtil initDatabase (Context context) {
		return new DatabaseUtil(context);
	}
	
	private void closeDatabase (SQLiteDatabase db) {
		if(db != null && db.isOpen()) db.close();
	}
	
	private RSSFeed getFeedInfo (int feedid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSFeedEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(feedid) };
		Cursor c = db.query(
			    RSSFeedEntry.TABLE_NAME,  				  // The table to query
			    feedProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                 	  // The sort order
			    );
		RSSFeed feed = null;
		c.moveToFirst();
		if (!c.isAfterLast()) {
			feed = new RSSFeed();
			feed.setId(c.getInt(c.getColumnIndexOrThrow(RSSFeedEntry._ID))); 
			feed.setTitle(c.getString(c.getColumnIndexOrThrow(RSSFeedEntry.COLUMN_NAME_TITLE))); 
			feed.setPubdate(c.getString(c.getColumnIndexOrThrow(RSSFeedEntry.COLUMN_NAME_PUBDATE)));
			feed.setUrl(c.getString(c.getColumnIndexOrThrow(RSSFeedEntry.COLUMN_NAME_URL)));
			feed.setName(c.getString(c.getColumnIndexOrThrow(RSSFeedEntry.COLUMN_NAME_NAME)));
	    }
		c.close();
		closeDatabase(db);
		return feed;
	}
	
	public RSSFeed getFeed (int feedid) {
		return getFeed(0, Settings.MAXCOUNT, feedid);
	}
	
	public RSSFeed getFeed (int itemStart, int itemCount, int feedid) {
		RSSFeed feed = getFeedInfo(feedid);
		getItemInFeed(feed, itemStart, itemCount);
		return feed;
	}
	
	public RSSFeed getFavItems (int start, int itemCount) {
		RSSFeed feed = null;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FAV + " = ?";
		String[] selectionArgs = { "1" };
		String sortOrder = RSSItemEntry.COLUMN_NAME_FAVTIME + " desc";
		String limit = String.valueOf(start) + "," + String.valueOf(itemCount);
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate < ? order by _pubdate desc limit ?,?", new String[]{String.valueOf(feed.getId()), moreDate, String.valueOf(0), String.valueOf(itemCount)});
		if (c.getCount() > 0) {
			feed = new RSSFeed();
			feed.setId(Settings.FAV);
			c.moveToFirst();
			RSSItem item;
			while (!c.isAfterLast()) {
				item = new RSSItem();
				setItem(item, c);
				feed.addItem(item);
				c.moveToNext();
		    }
		}
		c.close();
		closeDatabase(db);
		return feed;
	}
	
	public RSSFeed getOldFavItems (String favtime, int itemCount) {
		RSSFeed feed = null;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FAV + " = ? and " + RSSItemEntry.COLUMN_NAME_FAVTIME + " < ?";
		String[] selectionArgs = { "1", favtime };
		String sortOrder = RSSItemEntry.COLUMN_NAME_FAVTIME + " desc";
		String limit = String.valueOf(itemCount);
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate < ? order by _pubdate desc limit ?,?", new String[]{String.valueOf(feed.getId()), moreDate, String.valueOf(0), String.valueOf(itemCount)});
		if (c.getCount() > 0) {
			feed = new RSSFeed();
			feed.setId(Settings.FAV);
			c.moveToFirst();
			RSSItem item;
			while (!c.isAfterLast()) {
				item = new RSSItem();
				setItem(item, c);
				feed.addItem(item);
				c.moveToNext();
		    }
		}
		c.close();
		closeDatabase(db);
		return feed;
	}
	
	public RSSFeed getNewFeedItems (String refreshDate, int itemCount, int feedid) {
		RSSFeed feed = getFeedInfo(feedid);
		getNewItemsInFeed(feed, refreshDate, itemCount);
		return feed;
	}
	
	public RSSFeed getOldFeedItems (String moreDate, int itemCount, int feedid) {
		RSSFeed feed = getFeedInfo(feedid);
		getOldItemsInFeed(feed, moreDate, itemCount);
		return feed;
	}
	
	private void getOldItemsInFeed (RSSFeed feed, String moreDate, int itemCount) {
		if (feed == null) return;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FEED_ID + " = ? and " + RSSItemEntry.COLUMN_NAME_PUBDATE + " < ?";
		String[] selectionArgs = { String.valueOf(feed.getId()), moreDate };
		String sortOrder = RSSItemEntry.COLUMN_NAME_PUBDATE + " desc";
		String limit = "0," + String.valueOf(itemCount);
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate < ? order by _pubdate desc limit ?,?", new String[]{String.valueOf(feed.getId()), moreDate, String.valueOf(0), String.valueOf(itemCount)});
		RSSItem item;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			item = new RSSItem();
			setItem(item, c);
			feed.addItem(item);
			c.moveToNext();
	    }
		c.close();
		closeDatabase(db);
		return;
	}
	
	private void getNewItemsInFeed (RSSFeed feed, String refreshDate, int itemCount) {
		if (feed == null) return;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FEED_ID + " = ? and " + RSSItemEntry.COLUMN_NAME_PUBDATE + " > ?";
		String[] selectionArgs = { String.valueOf(feed.getId()), refreshDate };
		String sortOrder = RSSItemEntry.COLUMN_NAME_PUBDATE + " desc";
		String limit = "0," + String.valueOf(itemCount);
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate > ? order by _pubdate desc limit ?,?", new String[]{String.valueOf(feed.getId()), refreshDate, String.valueOf(0), String.valueOf(itemCount)});
		RSSItem item;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			item = new RSSItem();
			setItem(item, c);
			feed.addItem(item);
			c.moveToNext();
	    }
		c.close();
		closeDatabase(db);
		return;
	}
	
	private void getItemInFeed (RSSFeed feed, int start, int itemCount) {
		if (feed == null) return;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FEED_ID + " = ?";
		String[] selectionArgs = { String.valueOf(feed.getId()) };
		String sortOrder = RSSItemEntry.COLUMN_NAME_PUBDATE + " desc";
		String limit = String.valueOf(start) + "," + String.valueOf(itemCount);
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? order by _pubdate desc limit ?,?", new String[]{String.valueOf(feed.getId()), String.valueOf(start), String.valueOf(itemCount)});
		RSSItem item;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			item = new RSSItem();
			setItem(item, c);
			feed.addItem(item);
			c.moveToNext();
	    }
		c.close();
		closeDatabase(db);
		return;
	}
	
	public RSSItem getItem (int itemid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(itemid) };
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                // The sort order
			    );
//		Cursor c= db.rawQuery("select * from item where " + RSSItemEntry._ID + " = ? ", new String[]{String.valueOf(itemid)});
		RSSItem item = new RSSItem();
		c.moveToFirst();
		if (!c.isAfterLast()) {
			setItem(item, c);
	    }
		c.close();
		closeDatabase(db);
		return item;
	}
	
	public RSSItem getNextItem(RSSItem item) {
		return item.getFeedid() == Settings.FAV ? getNextFavItem(item.getFavtime()) : getNextFeedItem(item.getPubdate().getContent(), item.getFeedid());
	}
	
	public RSSItem getPreItem(RSSItem item) {
		return item.getFeedid() == Settings.FAV ? getPreFavItem(item.getFavtime()) : getPreFeedItem(item.getPubdate().getContent(), item.getFeedid());
	}
	
	private RSSItem getNextFavItem(String favtime) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FAV + " = ? and " + RSSItemEntry.COLUMN_NAME_FAVTIME + " > ?";
		String[] selectionArgs = { "1", favtime };
		String sortOrder = RSSItemEntry.COLUMN_NAME_FAVTIME;
		String limit = "1";
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate > ? order by _pubdate limit 1", new String[]{String.valueOf(feedid), curPubdate});
		RSSItem item = new RSSItem();
		c.moveToFirst();
		if (!c.isAfterLast()) {
			setItem(item, c);
			item.setFeedid(Settings.FAV);
	    }
		c.close();
		closeDatabase(db);
		return item;
	}
	
	private RSSItem getNextFeedItem (String curPubdate, int feedid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FEED_ID + " = ? and " + RSSItemEntry.COLUMN_NAME_PUBDATE + " > ?";
		String[] selectionArgs = { String.valueOf(feedid), curPubdate };
		String sortOrder = RSSItemEntry.COLUMN_NAME_PUBDATE;
		String limit = "1";
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate > ? order by _pubdate limit 1", new String[]{String.valueOf(feedid), curPubdate});
		RSSItem item = new RSSItem();
		c.moveToFirst();
		if (!c.isAfterLast()) {
			setItem(item, c);
	    }
		c.close();
		closeDatabase(db);
		return item;
	}
	
	private RSSItem getPreFavItem(String favtime) {
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			String selection = RSSItemEntry.COLUMN_NAME_FAV + " = ? and " + RSSItemEntry.COLUMN_NAME_FAVTIME + " < ?";
			String[] selectionArgs = { "1", favtime };
			String sortOrder = RSSItemEntry.COLUMN_NAME_FAVTIME + " desc";
			String limit = "1";
			Cursor c = db.query(
					RSSItemEntry.TABLE_NAME,  				  // The table to query
				    itemProjection,                           // The columns to return
				    selection,                                // The columns for the WHERE clause
				    selectionArgs,                            // The values for the WHERE clause
				    null,                                     // don't group the rows
				    null,                                     // don't filter by row groups
				    sortOrder,                                // The sort order
				    limit									  // return rows
				    );
//			Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate > ? order by _pubdate limit 1", new String[]{String.valueOf(feedid), curPubdate});
			RSSItem item = new RSSItem();
			c.moveToFirst();
			if (!c.isAfterLast()) {
				setItem(item, c);
				item.setFeedid(Settings.FAV);
		    }
			c.close();
			closeDatabase(db);
			return item;
		}
	
	private RSSItem getPreFeedItem (String curPubdate, int feedid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry.COLUMN_NAME_FEED_ID + " = ? and " + RSSItemEntry.COLUMN_NAME_PUBDATE + " < ?";
		String[] selectionArgs = { String.valueOf(feedid), curPubdate };
		String sortOrder = RSSItemEntry.COLUMN_NAME_PUBDATE + " desc";
		String limit = "1";
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    itemProjection,                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder,                                // The sort order
			    limit									  // return rows
			    );
//		Cursor c= db.rawQuery("select * from item where _feedid = ? and _pubdate < ? order by _pubdate desc limit 1", new String[]{String.valueOf(feedid), curPubdate});
		RSSItem item = new RSSItem();
		c.moveToFirst();
		if (!c.isAfterLast()) {
			setItem(item, c);
	    }
		c.close();
		closeDatabase(db);
		return item;
	}
	
	
	
	private void setItem (RSSItem item, Cursor c) {
		RSSItemInterface itemInterface;
		RSSItemInterfaceFactoryMethod itemFactory = new RSSItemInterfaceFactoryXmlPull();
		
		item.setId(c.getInt(c.getColumnIndexOrThrow(RSSItemEntry._ID)));
		itemInterface = itemFactory.createRSSItem("title");
		itemInterface.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_TITLE)));
		item.setTitle((RSSItemTitle) itemInterface);
		itemInterface = itemFactory.createRSSItem("description");
		itemInterface.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_DESCRIPTION)));
		item.setDescription((RSSItemDescription) itemInterface);
		itemInterface = itemFactory.createRSSItem("pubDate");
		itemInterface.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_PUBDATE)));
		item.setPubdate((RSSItemPubdate) itemInterface);
		itemInterface = itemFactory.createRSSItem("author");
		itemInterface.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_AUTHOR)));
		item.setAuthor((RSSItemAuthor) itemInterface);
		itemInterface = itemFactory.createRSSItem("link");
		itemInterface.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_LINK)));
		item.setLink((RSSItemLink) itemInterface);
		item.setFeedid(c.getInt(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_FEED_ID)));
		item.setFav(c.getInt(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_FAV)));
		item.setContent(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_CONTENT)));
		item.setFavtime(c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_FAVTIME)));
	}
	
	public void insertItem(RSSItem item, int feedid) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		insertItem(db, item, feedid);
    	closeDatabase(db);
	}
	
	private long insertItem(SQLiteDatabase db, RSSItem item, int feedid) {
		ContentValues cv=new ContentValues();
    	if (item.getTitle() != null) cv.put(RSSItemEntry.COLUMN_NAME_TITLE, item.getTitle().getContent());
    	if (item.getDescription() != null) cv.put(RSSItemEntry.COLUMN_NAME_DESCRIPTION, item.getDescription().getContent());
    	if (item.getPubdate() != null) cv.put(RSSItemEntry.COLUMN_NAME_PUBDATE, item.getPubdate().getContent());
    	if (item.getAuthor() != null) cv.put(RSSItemEntry.COLUMN_NAME_AUTHOR, item.getAuthor().getContent());
    	if (item.getLink() != null) cv.put(RSSItemEntry.COLUMN_NAME_LINK, item.getLink().getContent());
    	if (feedid != 0) cv.put(RSSItemEntry.COLUMN_NAME_FEED_ID, feedid);
    	return db.insert(RSSItemEntry.TABLE_NAME, null, cv);
	}
	
	public void updateFeedPubdate(String pubdate, int feedid) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues cv=new ContentValues();
    	cv.put(RSSItemEntry.COLUMN_NAME_PUBDATE, pubdate);
    	db.update(RSSFeedEntry.TABLE_NAME, cv, RSSFeedEntry._ID + " = ?", new String[]{String.valueOf(feedid)});
    	closeDatabase(db);
	}
	
	public int updateFeed (String title, String pubdate, int feedid) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(RSSItemEntry.COLUMN_NAME_TITLE, title);
		cv.put(RSSItemEntry.COLUMN_NAME_PUBDATE, pubdate);
		int temp = db.update(RSSFeedEntry.TABLE_NAME, cv, RSSFeedEntry._ID + " = ?", new String[]{String.valueOf(feedid)});
		closeDatabase(db);
		return temp;
	}
	
	public int updateFeedTitle (String title, int feedid) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(RSSItemEntry.COLUMN_NAME_TITLE, title);
		int temp = db.update(RSSFeedEntry.TABLE_NAME, cv, RSSFeedEntry._ID + " = ?", new String[]{String.valueOf(feedid)});
		closeDatabase(db);
		return temp;
	}
	
	public int deleteItems (int feedid) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int res = db.delete(RSSItemEntry.TABLE_NAME, RSSItemEntry.COLUMN_NAME_FEED_ID + " = ?", new String[]{String.valueOf(feedid)});
		closeDatabase(db);
		return res;
	}
	
	private int getFav (SQLiteDatabase db, int itemid) {
		String selection = RSSItemEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(itemid)};
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
			    new String[]{RSSItemEntry.COLUMN_NAME_FAV},                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null,                                // The sort order
			    null									  // return rows
			    );
		int fav = 0;
		c.moveToFirst();
		if (!c.isAfterLast()) {
			fav = c.getInt(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_FAV));
	    }
		c.close();
		return fav;
	}
	
	public int changeFav (RSSItem item) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int isFaved = item.getFav();
		db.beginTransaction();
		try {
			ContentValues cv=new ContentValues();
			cv.put(RSSItemEntry.COLUMN_NAME_FAV, 1 - item.getFav());
			cv.put(RSSItemEntry.COLUMN_NAME_FAVTIME, StringUtil.getNowDate());
			db.update(RSSItemEntry.TABLE_NAME, cv, RSSItemEntry._ID + " = ?", new String[]{String.valueOf(item.getId())});
			isFaved = getFav(db, item.getId());
			db.setTransactionSuccessful();
		} finally {
		    db.endTransaction();
		    closeDatabase(db);
		}
		return isFaved;
	}
	
	/*
	private String getDescription (int itemid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = RSSItemEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(itemid)};
		Cursor c = db.query(
				RSSItemEntry.TABLE_NAME,  				  // The table to query
				new String[]{RSSItemEntry.COLUMN_NAME_DESCRIPTION},                           // The columns to return
			    selection,                                // The columns for the WHERE clause
			    selectionArgs,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null,                                // The sort order
			    null									  // return rows
			    );
		String description = null;
		c.moveToFirst();
		if (!c.isAfterLast()) {
			description = c.getString(c.getColumnIndexOrThrow(RSSItemEntry.COLUMN_NAME_DESCRIPTION));
	    }
		c.close();
		closeDatabase(db);
		return description;
	}
	*/
	
	public int updateContent (int itemid, String content) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(RSSItemEntry.COLUMN_NAME_CONTENT, content);
		int rows = db.update(RSSItemEntry.TABLE_NAME, cv, RSSItemEntry._ID + " = ?", new String[]{String.valueOf(itemid)});
		closeDatabase(db);
		return rows;
	}
}
