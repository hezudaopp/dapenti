package cn.edu.nju.dapenti.entity;

import java.util.Hashtable;

import cn.edu.nju.dapenti.Settings;
import cn.edu.nju.dapenti.utils.StringUtil;

public class RSSItem {
	private int id;
	private RSSItemTitle title;
	private RSSItemDescription description;
	private RSSItemPubdate pubdate;
	private RSSItemAuthor author;
	private RSSItemLink link;
	private int feedid;
	private int fav;
	private String content;
	private String favtime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public RSSItemDescription getDescription() {
		return description;
	}
	public void setDescription(RSSItemDescription description) {
		this.description = description;
	}
	public RSSItemPubdate getPubdate() {
		return pubdate;
	}
	public void setPubdate(RSSItemPubdate pubdate) {
		this.pubdate = pubdate;
	}
	public void setPubdateFromRSS (RSSItemPubdate pubdate) {
		pubdate.setContent(StringUtil.strToDateString(pubdate.getContent()));
		this.pubdate = pubdate;
	}
	public RSSItemAuthor getAuthor() {
		return author;
	}
	public void setAuthor(RSSItemAuthor category) {
		this.author = category;
	}
	public RSSItemLink getLink() {
		return link;
	}
	public void setLink(RSSItemLink link) {
		this.link = link;
	}
	public RSSItemTitle getTitle() {
		return title;
	}
	public void setTitle(RSSItemTitle title) {
		this.title = title;
	}
	/*public void setTitleFromRSS(RSSItemTitle title) {
		title.setContent(title.getContent().substring(0, 14) + "\n" + title.getContent().substring(14));
		this.title = title;
	}*/
	public String toString() {
		Hashtable<Integer, String> ht = new Hashtable<Integer, String>();
		ht.put(Settings.TUGUA, this.getTitle().getContent());
		ht.put(Settings.DUANZI, StringUtil.Html2Text(this.getDescription().getContent()));
		ht.put(Settings.TUPIAN, this.getTitle().getContent());
		return ht.get(this.feedid);
	}
	public int getFeedid() {
		return feedid;
	}
	public void setFeedid(int feedid) {
		this.feedid = feedid;
	}
	public int getFav() {
		return fav;
	}
	public void setFav(int fav) {
		this.fav = fav;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFavtime() {
		return favtime;
	}
	public void setFavtime(String favtime) {
		this.favtime = favtime;
	}
}
