package cn.edu.nju.dapenti.rss;

import java.util.Hashtable;

import cn.edu.nju.dapenti.entity.RSSItemAuthor;
import cn.edu.nju.dapenti.entity.RSSItemDescription;
import cn.edu.nju.dapenti.entity.RSSItemInterface;
import cn.edu.nju.dapenti.entity.RSSItemLink;
import cn.edu.nju.dapenti.entity.RSSItemPubdate;
import cn.edu.nju.dapenti.entity.RSSItemTitle;

public abstract class RSSItemInterfaceFactoryMethod {
	
	Hashtable<String, RSSItemInterface> ht = new Hashtable<String, RSSItemInterface>();
	RSSHandler handler;
	
	public  RSSItemInterface createRSSItem (String localName) {
		setRSSHandler();
		initHashtable();
		return ht.get(localName);
	}
	
	protected abstract void setRSSHandler ();
	
	protected void initHashtable () {
		ht.put("title", new RSSItemTitle(handler));
		ht.put("link", new RSSItemLink(handler));
		ht.put("description", new RSSItemDescription(handler));
		ht.put("author", new RSSItemAuthor(handler));
		ht.put("pubDate", new RSSItemPubdate(handler));
	}
}
