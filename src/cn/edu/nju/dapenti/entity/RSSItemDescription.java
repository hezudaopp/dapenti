package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public class RSSItemDescription extends RSSItemInterface {
	protected RSSHandler handler;
	
	public RSSItemDescription(RSSHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setItem(RSSHandler handler, RSSItem item) {
		setHandler(handler);
		item.setDescription(this);
		handler.setCurrentItem(null);
	}

}
