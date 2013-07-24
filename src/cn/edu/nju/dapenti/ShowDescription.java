package cn.edu.nju.dapenti;

import cn.edu.nju.dapenti.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ViewFlipper;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.*;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.menu.handler.MenuHandlerInterface;
import cn.edu.nju.dapenti.menu.handler.MenuHandlerSimpleFactory;

public class ShowDescription extends Activity
{
	private ViewFlipper flipper;
	private RSSItem item = null;
	private int itemid = 0;
	private int feedid = 0;
	private ShowDescription descriptionActivity = this;
	private DatabaseUtil databaseUtil;
	private Settings settings;

	private static final int ADD = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case ADD:
					flipper.addView(new DescriptionWebView(descriptionActivity, flipper, item));
				}
			}
			super.handleMessage(msg);
		}
	};
	
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //this.databaseUtil = DatabaseUtil.initDatabase(this);
        //if (this.databaseUtil == null) return;
        requestWindowFeature(Window.FEATURE_PROGRESS);//让进度条显示在标题栏上
        setContentView(R.layout.show_description);
        databaseUtil = DatabaseUtil.initDatabase(descriptionActivity);
        if (this.databaseUtil == null) return;
        flipper = (ViewFlipper) descriptionActivity.findViewById(R.id.ViewFlipper);
        new SetWebViewTask().execute();
    }
	
	public void onDestroy () {
//		if (this.databaseUtil != null)
//    		this.databaseUtil.closeDatabase();
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {    
	    super.onConfigurationChanged(newConfig);
	    // 检测屏幕的方向：纵向或横向
	    if (this.getResources().getConfiguration().orientation 
	            == Configuration.ORIENTATION_LANDSCAPE) {
	        //当前为横屏， 在此处添加额外的处理代码
	    }
	    else if (this.getResources().getConfiguration().orientation 
	            == Configuration.ORIENTATION_PORTRAIT) {
	        //当前为竖屏， 在此处添加额外的处理代码
	    }
	    //检测实体键盘的状态：推出或者合上    
	    if (newConfig.hardKeyboardHidden 
	            == Configuration.HARDKEYBOARDHIDDEN_NO){ 
	        //实体键盘处于推出状态，在此处添加额外的处理代码
	    } 
	    else if (newConfig.hardKeyboardHidden
	            == Configuration.HARDKEYBOARDHIDDEN_YES){ 
	        //实体键盘处于合上状态，在此处添加额外的处理代码
	    }
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 DescriptionWebView descriptionWebView = new DescriptionWebView(this);
		 if ((keyCode == KeyEvent.KEYCODE_BACK) && descriptionWebView.canGoBack()) {
		     descriptionWebView.goBack();
		     return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
	
	private class SetWebViewTask extends AsyncTask<String, Void, RSSItem> {
		protected RSSItem doInBackground(String... urls) {
			Bundle b = null;
	        Intent startingIntent = getIntent();
	        if (startingIntent != null) {
	            b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
	            if (b != null) {
	            	itemid = b.getInt("itemid");
	            	feedid = b.getInt("feedid");
	            }
	        } else {
	        	return null;
	        }
	        item = databaseUtil.getItem(itemid);
	        item.setFeedid(feedid);
	        return item;
		}

		protected void onPostExecute(RSSItem item) {
			if (item == null) {
				descriptionActivity.setTitle("Information Not Found.");
			}
			sendMsg(ADD);
		}
	}
	
	@Override  
    public boolean onCreateOptionsMenu (Menu menu) {  
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_description, menu);
        return true;
    }
	
	@Override
	public boolean onMenuOpened (int i, Menu menu) {
		int curFav = item.getFav();
    	menu.getItem(0).setVisible((1-curFav)!=0);
    	menu.getItem(1).setVisible(curFav!=0);
		return super.onMenuOpened(i, menu);
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) {
		this.closeOptionsMenu();
    	MenuHandlerSimpleFactory factory = MenuHandlerSimpleFactory.newInstance();
    	MenuHandlerInterface menuHandler = factory.createMenuHandler(item.getItemId());
    	if (menuHandler != null) menuHandler.handle(this);
		//返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
    }  
	
	public ViewFlipper getFlipper () {
		return flipper;
	}
	
	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}
	
	public boolean refreshIsLoadPicture () {
		settings = new Settings(this);
		return settings.isLoadPicture();
	}
	
	public RSSItem getRSSItem () {
		return this.item;
	}
}
