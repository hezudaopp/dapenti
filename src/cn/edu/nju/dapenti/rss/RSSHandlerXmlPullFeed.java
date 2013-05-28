package cn.edu.nju.dapenti.rss;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import cn.edu.nju.dapenti.RSSReader;
import cn.edu.nju.dapenti.Settings;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSFeed;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.entity.RSSItemInterface;
import cn.edu.nju.dapenti.utils.ParserUtil;
import cn.edu.nju.dapenti.utils.StringUtil;

public class RSSHandlerXmlPullFeed implements RSSHandler {
	private static final String TAG = "XmlPullParser";
	private XmlPullParser _parser = Xml.newPullParser();
	private RSSItemInterfaceFactoryMethod itemFactory = new RSSItemInterfaceFactoryXmlPull();
	private boolean bFoundFeedTitle = false;
	private RSSFeed _feed;
	private RSSItem _item;
	private RSSItemInterface _currentItem;
	private Handler mProgressHandler = null;
	
	public RSSHandlerXmlPullFeed(Handler handler) {
		mProgressHandler = handler;
	}
	
	public RSSHandlerXmlPullFeed() {
	}

	public void setCurrentItem(RSSItemInterface item) {
		_currentItem = item;
	}
	
	
	
	public RSSFeed getNewFeed(String urlToRssFeed, String lastestRefreshDate,
			int feedid, DatabaseUtil databaseUtil) throws IOException, XmlPullParserException {
			int eventType = 0;
			boolean done = false;
	        boolean isNextItem = false;
			// auto-detect the encoding from the stream
			_parser.setInput(StringUtil.getInputStreamRemote(urlToRssFeed), null);
			eventType = _parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT && !done){
	            String name = null;
	            String content = null;
	            switch (eventType){
	                case XmlPullParser.START_DOCUMENT:
	                    _feed = new RSSFeed();
	                    break;
	                case XmlPullParser.START_TAG:
	                	name = _parser.getName();
	                	if (name.equalsIgnoreCase(ITEM)) {
	                		_item = new RSSItem();
	                		isNextItem = true;
	                	}
	                	if (!isNextItem) break;
	                    _currentItem = itemFactory.createRSSItem(name);
	                    if (_currentItem != null) {
	                    	content = _parser.nextText();
	                    	_currentItem.setContent(content);
	                    	_currentItem.setItem(this, _item);
	                    	Log.d(TAG, content);
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	if (!isNextItem) break;
	                    name = _parser.getName();
	                    if (name.equalsIgnoreCase(ITEM)){
	                    	if (_item != null && _item.getPubdate() != null && lastestRefreshDate.compareTo(_item.getPubdate().getContent()) < 0) {
	            	            // add our item to the list!
	            	        	databaseUtil.insertItem(_item, feedid);
	            	        	_feed.addItem(_item);
	            	        } else {
	            	        	//很奇怪，从数据库获取feed就不会出现ScrollOverListView has no id的错误。？？？
	            	        	if (_feed.getItemCount() > 0) {
	            	        		databaseUtil.updateFeedPubdate(_feed.getItem(0).getPubdate().getContent(), feedid);
	            	        	}
	            	        	_feed = databaseUtil.getNewFeedItems(lastestRefreshDate, Settings.MAXCOUNT, feedid);
	            	        	done = true;
	            	        }
	                    } else if (name.equalsIgnoreCase(CHANNEL)){
	                        done = true;
	                    }
	                    break;
	            }
            eventType = _parser.next();
        }
        return _feed;
	}

	public RSSFeed getFirstFeed(String urlToRssFeed, int feedid, DatabaseUtil databaseUtil) throws XmlPullParserException, IOException {
		_parser.setInput(StringUtil.getInputStreamRemote(urlToRssFeed), null);
	
        int eventType = _parser.getEventType();
        boolean done = false;
        bFoundFeedTitle = false;
        while (eventType != XmlPullParser.END_DOCUMENT && !done){
            String name = null;
            String content = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    _feed = new RSSFeed();
                    _item = new RSSItem();
                    break;
                case XmlPullParser.START_TAG:
                	name = _parser.getName();
                	if (!bFoundFeedTitle && name.equals(TITLE)) {
                        // record our feed data - you temporarily stored it in the item :)
                        _feed.setTitle(_parser.nextText());
                        bFoundFeedTitle = true;
                        databaseUtil.updateFeedTitle(_feed.getTitle(), feedid);
                        break;
                    }
                	if (name.equalsIgnoreCase(ITEM)){
                		_item = new RSSItem();
                    }
                    _currentItem = itemFactory.createRSSItem(name);
                    if (_currentItem != null) {
                    	content = _parser.nextText();
                    	_currentItem.setContent(content);
                    	_currentItem.setItem(this, _item);
                    	Log.d(TAG, content);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = _parser.getName();
                    if (name.equalsIgnoreCase(ITEM)){
                    	if (_feed.getItemCount() < Settings.MAXCOUNT) {
            	            // add our item to the list!
                    		databaseUtil.insertItem(_item, feedid);
            	        	_feed.addItem(_item);
            	        	ParserUtil.sendProgressMsg(RSSReader.INCREASE, mProgressHandler, _feed);
            	        } else {
            	        	//很奇怪，从数据库获取feed就不会出现ScrollOverListView has no id的错误。？？？
            	        	if (_feed.getItemCount() > 0) {
            	        		databaseUtil.updateFeedPubdate(_feed.getItem(0).getPubdate().getContent(), feedid);
            	        	}
            	        	_feed = databaseUtil.getFeed(feedid);
            	        	done = true;
            	        }
                    } else if (name.equalsIgnoreCase(CHANNEL)){
                        done = true;
                    }
                    break;
            }
            eventType = _parser.next();
        }
		return ParserUtil.sendProgressMsg(RSSReader.DONE, mProgressHandler, _feed);
	}

	public RSSFeed getMoreFeed(String urlToRssFeed, String moreDate, int feedid, DatabaseUtil databaseUtil) throws XmlPullParserException, IOException {
		_parser.setInput(StringUtil.getInputStreamRemote(urlToRssFeed), null);
	
        int eventType = _parser.getEventType();
        boolean done = false;
        boolean start = false;
        boolean isNextItem = false;
        while (eventType != XmlPullParser.END_DOCUMENT && !done){
            String name = null;
            String content = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    _feed = new RSSFeed();
                    break;
                case XmlPullParser.START_TAG:
                	name = _parser.getName();
                	String pubdate = null;
                	if (name.equalsIgnoreCase(PUB_DATE)) {
                		pubdate = _parser.nextText();
                		if (StringUtil.strToDateString(pubdate).compareTo(moreDate) <= 0)
                			start = true;
                    }
                	if (!start) break;
                	if (name.equalsIgnoreCase(ITEM)){
                		_item = new RSSItem();
                		isNextItem = true;
                    }
                	if (!isNextItem) break;
                    _currentItem = itemFactory.createRSSItem(name);
                    if (_currentItem != null) {
                    	if (name.equalsIgnoreCase(PUB_DATE))
                    		content = pubdate;
                    	else
                    		content = _parser.nextText();
                    	_currentItem.setContent(content);
                    	_currentItem.setItem(this, _item);
                    	Log.d(TAG, content);
                    }
                    break;
                case XmlPullParser.END_TAG:
                	if (!isNextItem) break;
                    name = _parser.getName();
                    if (name.equalsIgnoreCase(ITEM)){
                    	if (_feed.getItemCount() < Settings.MAXCOUNT) {
            	            // add our item to the list!
                    		databaseUtil.insertItem(_item, feedid);
            	        	_feed.addItem(_item);
            	        } else {
            	        	//很奇怪，从数据库获取feed就不会出现ScrollOverListView has no id的错误。？？？
            	        	_feed = databaseUtil.getOldFeedItems(moreDate, Settings.MAXCOUNT, feedid);
            	        	done = true;
            	        }
                    } else if (name.equalsIgnoreCase(CHANNEL)){
                        done = true;
                    }
                    break;
            }
            eventType = _parser.next();
        }
		return _feed;
	}
}
