package cn.edu.nju.dapenti.db;

import android.provider.BaseColumns;

public abstract class RSSItemEntry implements BaseColumns {
	public static final String TABLE_NAME = "item";
    public static final String COLUMN_NAME_TITLE = "_title";
    public static final String COLUMN_NAME_DESCRIPTION = "_description";
    public static final String COLUMN_NAME_PUBDATE = "_pubdate";
    public static final String COLUMN_NAME_AUTHOR = "_author";
    public static final String COLUMN_NAME_LINK = "_link";
    public static final String COLUMN_NAME_FEED_ID = "_feedid";
    public static final String COLUMN_NAME_FAV = "_fav";
    public static final String COLUMN_NAME_CONTENT = "_content";
    public static final String COLUMN_NAME_FAVTIME = "_favtime";
    
    private RSSItemEntry() {}
}
