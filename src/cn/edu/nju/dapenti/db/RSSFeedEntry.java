package cn.edu.nju.dapenti.db;

public abstract class RSSFeedEntry {
	public static final String TABLE_NAME = "feed";
	public static final String _ID = "_id";
    public static final String COLUMN_NAME_TITLE = "_title";
    public static final String COLUMN_NAME_PUBDATE = "_pubdate";
    public static final String COLUMN_NAME_URL = "_url";
    public static final String COLUMN_NAME_NAME = "_name";

    private RSSFeedEntry() {}

}
