package cn.edu.nju.dapenti.entity;

import cn.edu.nju.dapenti.rss.RSSHandler;

public abstract class RSSItemInterface {
	protected RSSItem item;
	protected RSSHandler handler;
	protected String content;
	
	public abstract void setItem(RSSHandler handler, RSSItem item);
	
	public void setHandler(RSSHandler handler) {
		this.handler = handler;
	}
	
	public void setContent (String content) {
		this.content = content;
	}
	
	public String getContent () {
		return content;
	}
}
