package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public class RSSItemAuthor extends RSSItemInterface {
	
	public RSSItemAuthor(RSSHandler h) {
		this.handler = h;
	}

	@Override
	public void setItem(RSSHandler handler, RSSItem item) {
		setHandler(handler);
		item.setAuthor(this);
		handler.setCurrentItem(null);
	}
}
