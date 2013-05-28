package cn.edu.nju.dapenti.rss;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSFeed;
import cn.edu.nju.dapenti.entity.RSSItemInterface;

public interface RSSHandler {
	// names of the XML tags
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ITEM = "item";
    static final String AUTHOR = "author";
    static final String CHANNEL = "channel";
    
	//public RSSFeed getFeed(String urlToRssFeed, int startItem, boolean isRefresh, String lastestRefreshDate, String moreDate, int feedid);
    public RSSFeed getFirstFeed (String urlToRssFeed, int feedid, DatabaseUtil du) throws XmlPullParserException, IOException;
	public RSSFeed getMoreFeed (String urlToRssFeed, String moreDate, int feedid, DatabaseUtil du) throws XmlPullParserException, IOException;
	public RSSFeed getNewFeed (String urlToRssFeed, String lastestRefreshDate, int feedid, DatabaseUtil du) throws XmlPullParserException, IOException;
	public void setCurrentItem(RSSItemInterface item);
}
