package cn.edu.nju.dapenti.entity;

import java.util.List;
import java.util.Vector;

import cn.edu.nju.dapenti.entity.RSSItem;

public class RSSFeed
{
	private int _id = 0;
    private String _title = null;
    private String _pubdate = null;
    private String _description = null;
    private String _url = null;
    private String _name = null;
    private int _itemcount = 0;
    private List<RSSItem> _itemlist;
    
    
    public RSSFeed()
    {
        _itemlist = new Vector<RSSItem>(0); 
    }
    public int addItem(RSSItem item)
    {
        _itemlist.add(item);
        _itemcount++;
        return _itemcount;
    }
    public RSSItem getItem(int location)
    {
        return _itemlist.get(location);
    }
    public List<RSSItem> getAllItems()
    {
        return _itemlist;
    }
    public int getItemCount()
    {
        return _itemcount;
    }
    public void setTitle(String title)
    {
        _title = title;
    }
    public void setPubdate(String pubdate)
    {
        _pubdate = pubdate;
    }
    public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getTitle()
    {
        return _title;
    }
    public String getPubdate()
    {
        return _pubdate;
    }
	public String getDescription() {
		return _description;
	}
	public void setDescription(String _description) {
		this._description = _description;
	}
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public String getUrl() {
		return _url;
	}
	public void setUrl(String _url) {
		this._url = _url;
	}
	public void creaseItemCounte() {
		this._itemcount++;
	}
	
}
