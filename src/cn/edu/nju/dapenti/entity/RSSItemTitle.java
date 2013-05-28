package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public class RSSItemTitle extends RSSItemInterface {
	protected RSSHandler handler;

	public RSSItemTitle(RSSHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setItem(RSSHandler handler, RSSItem item) {
		setHandler(handler);
		item.setTitle(this);
		handler.setCurrentItem(null);
	}
	
}
