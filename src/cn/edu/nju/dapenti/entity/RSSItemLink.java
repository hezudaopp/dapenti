package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public class RSSItemLink extends RSSItemInterface {
	protected RSSHandler handler;
	
	public RSSItemLink(RSSHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setItem(RSSHandler handler, RSSItem item) {
		setHandler(handler);
		item.setLink(this);
		handler.setCurrentItem(null);
	}

}
