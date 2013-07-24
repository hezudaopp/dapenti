package cn.edu.nju.dapenti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.dapenti.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import cn.edu.nju.dapenti.PullDownView.OnPullDownListener;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSFeed;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.exceptions.MyUncaughtExceptionHandler;
import cn.edu.nju.dapenti.menu.handler.MenuHandlerInterface;
import cn.edu.nju.dapenti.menu.handler.MenuHandlerSimpleFactory;
//import cn.edu.nju.dapenti.PullToRefreshListView;
//import cn.edu.nju.dapenti.PullToRefreshListView.OnRefreshListener;
import cn.edu.nju.dapenti.rss.RSSHandler;
import cn.edu.nju.dapenti.rss.RSSHandlerXmlPullFeed;
import cn.edu.nju.dapenti.utils.NetworkUtil;
import cn.edu.nju.dapenti.utils.ParserUtil;
import cn.edu.nju.dapenti.utils.StringUtil;

public class RSSReader extends Activity implements OnItemClickListener, 
OnPullDownListener, OnItemLongClickListener, OnCreateContextMenuListener {
	public static final int REQ_SHOW_DESCRIPTION = 0;
	private static final String TAG = "RSSReader";
	
	private DatabaseUtil databaseUtil = null;
	private RSSFeed _feed = null;
	private int _feedid = 0;
	private String _urlToRssFeed;
	private String _feedPubdate = "1970-01-01 00:00:00";
	private String _moreDate = StringUtil.getNowDate();
	private String _currentFavtime = StringUtil.getNowDate();
	private RSSHandler _theRssHandlerFeed;
	private RSSItem _selectedItem;
	
	private ContextMenu mContextMenu;
	private ListView mListView;
	private ArrayAdapter<RSSItem> mAdapter;
	private PullDownView mPullDownView;
	private List<RSSItem> mItems = new ArrayList<RSSItem>();
	
	private RSSReader rssreader = this;
	private static final int WHAT_DID_LOAD_DATA = 0;
    private static final int WHAT_DID_REFRESH = 1;
    private static final int WHAT_DID_MORE = 2;
    private static final int WHAT_DID_CLEAR = 3;
    
    private final int PROGRESS_DIALOG = 1;  
    public static final int INCREASE = 0;
    public static final int DONE = 1;
    private ProgressDialog progressDialog = null;
    
    /*private int mLastMotionX, mLastMotionY;  
    //是否移动了  
    private boolean isMoved;  
    //是否释放了  
    private boolean isReleased;  
    //计数器，防止多次点击导致最后一次形成longpress的时间变短  
    private int mCounter;  
    //长按的runnable  
    private Runnable mLongPressRunnable;  
    //移动的阈值  
    private static final int TOUCH_SLOP = 20;*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_feed);
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler.getInstance(this));
        Bundle b = null;
        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
            if (b == null) {
                return;
            } else {
            	_feedid = b.getInt("id");
            	_urlToRssFeed = Settings.ID_URL_MAP.get(_feedid);
            }
        } else {
            this.setTitle("Information Not Found.");
        }
        
        this.databaseUtil = DatabaseUtil.initDatabase(this);
        if (this.databaseUtil == null) return;
        setRSSHandlerFeed(new RSSHandlerXmlPullFeed(mProgressHandler));
        loadData();

		UpdateDisplay();
    }
    
    public void onDestroy () {
//    	if (this.databaseUtil != null)
//    		this.databaseUtil.closeDatabase();
    	super.onDestroy();
    }
    
    private void UpdateDisplay() {
    	showDialog(PROGRESS_DIALOG);
        //feedtitle.setText("亲，这是你的第一次吧？居然没能获取到数据。\n 你确定你的网络能连上喷嚏网吗？");
        /*
         * 1.使用PullDownView
         * 2.设置OnPullDownListener
         * 3.从mPullDownView里面获取ListView
         */
        mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
        mPullDownView.setOnPullDownListener(this);
        mListView = mPullDownView.getListView();
        mListView.setOnItemClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        mListView.setOnItemLongClickListener(this);
        mAdapter = new ArrayAdapter<RSSItem>(this, R.layout.item_title, mItems);
        mListView.setAdapter(mAdapter);
        
      //浏览到底部自动刷新
        mPullDownView.enableAutoFetchMore(true, 2);
    }
    

    @Override  
    public Dialog onCreateDialog(int id) {  
        switch(id) {  
            case PROGRESS_DIALOG:     
                //this表示该对话框是针对当前Activity的  
                progressDialog = new ProgressDialog(this);  
                //设置最大值为100  
                progressDialog.setMax(100);  
                //设置进度条风格STYLE_HORIZONTAL  
                progressDialog.setProgressStyle(  
                        ProgressDialog.STYLE_HORIZONTAL);  
                progressDialog.setTitle("打个喷嚏咋这费劲呢？");  
                break;  
        }  
        return progressDialog;  
    }  
    
    
    private void loadData(){
        new Thread(new Runnable() {
            public void run() {
            	Looper.prepare();
                Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
                if (_urlToRssFeed != null) {
                	_feed = getFirstFeed(_urlToRssFeed);
                }
                if (_feed != null) {
                	msg.obj = _feed;
                }
                msg.sendToTarget();
                Looper.loop();
            }
        }).start();
    }
    
    public void onRefresh() {
        new Thread(new Runnable() {
            public void run() {
            	Looper.prepare();
            	Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
            	_feed = getNewFeed(_urlToRssFeed);
//            	if (_urlToRssFeed != null) {
//            		_feed = getNewFeed(_urlToRssFeed);
//                } else { //fav feed
//                	_feed = getFirstFavFeed();
//                }
            	if (_feed != null) { 
                    msg.obj = _feed;
            	}
            	msg.sendToTarget();
            	Looper.loop();
            }
        }).start();
    }

    public void onMore() {
        new Thread(new Runnable() {
            public void run() {
            	Looper.prepare();
            	Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
            	if (_urlToRssFeed != null) {
	            	if (mItems.isEmpty()) {
	            		_feed = getFirstFeed(_urlToRssFeed);
	            	} else {
	            		_feed = getMoreFeed(_urlToRssFeed);
	            	}
            	} else {
            		_feed = getMoreFavFeed();
            	}
                if (_feed != null) {
	                msg.obj = _feed;
                }
                msg.sendToTarget();
                Looper.loop();
            }
        }).start();
    }
    
    public void onClear() {
        new Thread(new Runnable() {
            public void run() {
            	Message msg = mUIHandler.obtainMessage(WHAT_DID_CLEAR);
                msg.sendToTarget();
            }
        }).start();
    }
    
    private Handler mProgressHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch(msg.what) {  
                case INCREASE:
                	if(msg.obj != null){
						int count = (Integer) msg.obj;
						Log.d(TAG, String.valueOf(count));
						progressDialog.setProgress(count*100/Settings.MAXCOUNT);
                    }
                    if(progressDialog.getProgress() >= 100) {  
                    	progressDialog.dismiss();  
                    }  
                    break; 
                case DONE:
	                progressDialog.dismiss(); 
	                break;
            }  
        }  
    };

	private Handler mUIHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DID_LOAD_DATA:{
                    if(msg.obj != null){
                        RSSFeed feed = (RSSFeed) msg.obj;
						List<RSSItem> items = feed.getAllItems();
                        if(!items.isEmpty()){
                        	_feedid = feed.getId();
                        	updateMoreDateAndCurrentFavtime(feed);
            				_feedPubdate = feed.getPubdate();
                        	mItems.addAll(items);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                    	ParserUtil.sendProgressMsg(DONE, mProgressHandler, null);
                    	//Toast.makeText(mainActivity, "亲，你的网络不给力.", Toast.LENGTH_SHORT).show();
                    }
                    mPullDownView.notifyDidLoad();
                    // 告诉它数据加载完毕;
                    break;
                }
                case WHAT_DID_REFRESH :{
                	if(msg.obj != null){
                		RSSFeed feed = (RSSFeed) msg.obj;
	                	List<RSSItem> items = feed.getAllItems();
	                	if(!items.isEmpty()){
	                		if (feed.getId() == Settings.FAV)
	                			_currentFavtime = feed.getItem(feed.getItemCount()-1).getFavtime(); 
	                		_feedPubdate = feed.getItem(0).getPubdate().getContent();
	                		if (_urlToRssFeed == null) {
	                			mItems.clear();
	                		}
	                		mItems.addAll(0, items);
		                    mAdapter.notifyDataSetChanged();
	                	} else {
	                		Toast.makeText(rssreader, "已经是最新了，亲。", Toast.LENGTH_SHORT).show();
	                	}
                	} else {
                		
                	}
                	mPullDownView.notifyDidRefresh();
                    // 告诉它更新完毕
                    break;
                }
 
                case WHAT_DID_MORE:{
                	if (msg.obj != null) {
                		RSSFeed feed = (RSSFeed) msg.obj;
	                	List<RSSItem> items = feed.getAllItems();
	                	if(!items.isEmpty()){
	                		_feedid = feed.getId();
	                		updateMoreDateAndCurrentFavtime(feed);
            				_feedPubdate = feed.getPubdate();
		                	mItems.addAll(items);
		                    mAdapter.notifyDataSetChanged();
	                	} else {
	                		Toast.makeText(rssreader, "呜呜，没有更多了。", Toast.LENGTH_SHORT).show();
	                	}
                	}
                	mPullDownView.notifyDidMore();
                    // 告诉它获取更多完毕
                    break;
                }
                
                case WHAT_DID_CLEAR:{
                	mItems.clear();
                	_moreDate = StringUtil.getNowDate();
                	_currentFavtime = StringUtil.getNowDate();
                	mAdapter.notifyDataSetChanged();
                }
            }
 
        }
 
    };
    
    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
    	if (_feedid != Settings.DUANZI) {
	    	Log.d(TAG,"item clicked! [" + ((RSSItem) (mListView.getItemAtPosition(position))).getTitle().getContent() + "]");
	    	Intent itemintent = new Intent(this,ShowDescription.class);
	    	Bundle bundle = new Bundle();
	    	bundle.putInt("itemid", ((RSSItem) (mListView.getItemAtPosition(position))).getId());
	    	bundle.putInt("feedid", _feedid);
	    	itemintent.putExtra("android.intent.extra.INTENT", bundle);
	        startActivityForResult(itemintent,REQ_SHOW_DESCRIPTION);
    	}
    }
    
    @Override  
    public boolean onCreateOptionsMenu (Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        /* 
         * 这里必须返回true. 
         * 如果返回false,则点Menu按键将不起任何作用. 
         */
        return true;  
    }  
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	this.closeOptionsMenu();
    	MenuHandlerSimpleFactory factory = MenuHandlerSimpleFactory.newInstance();
    	MenuHandlerInterface menuHandler = factory.createMenuHandler(item.getItemId());
    	if (menuHandler != null) menuHandler.handle(this);
		//返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
    }
    
    public void onResume() {
    	if (this._urlToRssFeed == null) {
    		this.onRefresh();
    	}
    	super.onResume();
    }
    
    protected RSSFeed getFirstFavFeed () {
    	RSSFeed feed = this.databaseUtil.getFavItems(0, Settings.MAXCOUNT);
    	return ParserUtil.sendProgressMsg(DONE, mProgressHandler, feed);
    }
    
    protected RSSFeed getMoreFavFeed() {
    	return this.databaseUtil.getOldFavItems(_currentFavtime, Settings.MAXCOUNT);
    }
    
    protected RSSFeed getFirstFeed (String urlToRssFeed) {
    	RSSFeed feed = this.databaseUtil.getFeed(0, Settings.MAXCOUNT, _feedid);
    	if (feed != null && feed.getItemCount() > 0) return ParserUtil.sendProgressMsg(DONE, mProgressHandler, feed);
    	// instantiate our handler
    	if (!NetworkUtil.isNetworkAvailable(this)) {
    		return ParserUtil.sendProgressMsg(DONE, mProgressHandler, feed);
    	}
    	try {
			return _theRssHandlerFeed.getFirstFeed(urlToRssFeed, _feedid, databaseUtil);
		} catch (XmlPullParserException e) {
			Toast.makeText(this, "转化数据格式出了问题。", Toast.LENGTH_SHORT).show();
			return null;
		} catch (IOException e) {
			Toast.makeText(this, "网络不给力，获取数据失败了。", Toast.LENGTH_SHORT).show();
			return null;
		}
    }
    
    //fix fav fc bug using synchronized
    synchronized protected RSSFeed getNewFeed (String urlToRssFeed) {
    	if (urlToRssFeed != null) {
	    	// instantiate our handler
	    	if (!NetworkUtil.isNetworkAvailable(this)) {
	    		Toast.makeText(this, "无可用网络，还是看看本地数据吧。", Toast.LENGTH_SHORT).show();
	    		return null;
	    	}
	    	try {
	    		return _theRssHandlerFeed.getNewFeed(urlToRssFeed, _feedPubdate, _feedid, databaseUtil);
			} catch (XmlPullParserException e) {
				Toast.makeText(this, "转化数据格式出了问题。", Toast.LENGTH_SHORT).show();
				return null;
			} catch (IOException e) {
				Toast.makeText(this, "网络不给力，获取数据失败了。", Toast.LENGTH_SHORT).show();
				return null;
			}
    	} else {
    		return getFirstFavFeed();
    	}
    }
    
    protected RSSFeed getMoreFeed(String urlToRssFeed)
    {
    	_feed = this.databaseUtil.getOldFeedItems(_moreDate, Settings.MAXCOUNT, _feedid);
    	if (_feed != null && _feed.getItemCount() > 0) return _feed;
    	// instantiate our handler
    	if (!NetworkUtil.isNetworkAvailable(this)) {
    		Toast.makeText(this, "无可用网络，还是看看本地数据吧。", Toast.LENGTH_SHORT).show();
    		return null;
    	}
    	try {
    		return _theRssHandlerFeed.getMoreFeed(urlToRssFeed, _moreDate, _feedid, databaseUtil);
		} catch (XmlPullParserException e) {
			Toast.makeText(this, "转化数据格式出了问题。", Toast.LENGTH_SHORT).show();
			return null;
		} catch (IOException e) {
			Toast.makeText(this, "网络不给力，获取数据失败了。", Toast.LENGTH_SHORT).show();
			return null;
		}
    }
    
    public void setRSSHandlerFeed (RSSHandler handlerFeed) {
    	_theRssHandlerFeed = handlerFeed;
    }
  
    public void setFeed (RSSFeed feed) {
    	this._feed = feed;
    }
    
    public int getFeedid () {
    	return this._feedid;
    }
    
    public RSSItem getSelectedItem() {
    	return this._selectedItem;
    }
    
    public void setSelectedItem(RSSItem selectedItem ) {
    	this._selectedItem = selectedItem;
    }
    
    private void updateMoreDateAndCurrentFavtime(RSSFeed feed) {
    	if (feed != null && feed.getItemCount() > 0) {
    		if (feed.getId() == Settings.FAV)
    			_currentFavtime = feed.getItem(feed.getItemCount()-1).getFavtime();
    		else 
    			_moreDate = feed.getItem(feed.getItemCount()-1).getPubdate().getContent();
			
    	}
    }
    
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		new SetOnItemLongClickTask().execute(String.valueOf(position));
		return false; //let the window create menu
    }
    

    private class SetOnItemLongClickTask extends AsyncTask<String, Void, RSSItem> {
		protected RSSItem doInBackground(String... args) {
			setSelectedItem((RSSItem) (mListView.getItemAtPosition(Integer.parseInt(args[0]))));
			return _selectedItem;
		}

		protected void onPostExecute(RSSItem item) {
			int curFav = _selectedItem.getFav();
			mContextMenu.getItem(0).setVisible((1-curFav)!=0);
			mContextMenu.getItem(1).setVisible(curFav!=0);;
		}
	}
    
    public void onCreateContextMenu(ContextMenu conMenu, View view , ContextMenuInfo info) {       
//      conMenu.setHeaderTitle("ContextMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_context, conMenu);
		mContextMenu = conMenu;
	}
	
	public boolean onContextItemSelected(MenuItem aItem) {       
		MenuHandlerSimpleFactory factory = MenuHandlerSimpleFactory.newInstance();
		MenuHandlerInterface menuHandler = factory.createMenuHandler(aItem.getItemId());
		if (menuHandler != null) menuHandler.handle(this);
		return super.onContextItemSelected(aItem);      
	}
}
