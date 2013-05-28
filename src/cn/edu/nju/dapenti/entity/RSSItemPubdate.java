package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public class RSSItemPubdate extends RSSItemInterface {
	protected RSSHandler handler;
	
	public RSSItemPubdate(RSSHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setItem(RSSHandler handler, RSSItem item) {
		setHandler(handler);
		item.setPubdateFromRSS(this);
		handler.setCurrentItem(null);
	}

}
