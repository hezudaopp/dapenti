package cn.edu.nju.dapenti;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.utils.NetworkUtil;

public class DescriptionWebView extends WebView {
	private static final String TAG = "DescriptionWebView";
	public static boolean hasAlertFlipper = false;
	
	DatabaseUtil databaseUtil;
	
	float downXValue;
    long downTime;
    private ViewFlipper flipper;

    private float lastTouchX, lastTouchY;
    private boolean hasMoved = false;
    
	private boolean isLoadPicture;
	private Context context;
	private RSSItem item;
	private String title;
	
	
	public DescriptionWebView(Context context) {
		super(context);
	}
	
	/* An instance of this class will be registered as a JavaScript interface */
//	private class MyJavaScriptInterface
//	{
//	    @SuppressWarnings("unused")
//		public void processHTML(String html)
//	    {
//	        // process the html as needed by the app
//	    	Log.d(TAG, html);
//	    	item.getDescription().setContent(html);
//	    }
//	}
	
	public void setWebView() {
		// use cache
		//this.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		WebSettings webSettings = this.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setBlockNetworkImage(!this.isLoadPicture);
        //单列模式，使图片显示适合页宽
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        //支持缩放
		webSettings.setSupportZoom(true);
        //支持内建缩放
		webSettings.setBuiltInZoomControls(true); 
        //descriptionWebView.setInitialScale(70);
        //缩放排版
		//descriptionWebView.getSettings().setUseWideViewPort(true);  
		//descriptionWebView.getSettings().setLoadWithOverviewMode(true);
		//使用缓存
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); 默认不使用缓存！
        //allow to use js
//        webSettings.setJavaScriptEnabled(true);
        //Register a new JavaScript interface called HTMLOUT
//        this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        //在当前Activity中打开连接
        this.setWebViewClient(new WebViewClient () {
        	@Override
        	public void onPageFinished(WebView view,String url) {
        		title = item.getTitle().getContent();
                ((Activity) context).setTitle(title);
                /* This call inject JavaScript into the page which just finished loading. */
//                loadUrl("javascript:window.HTMLOUT.processHTML(document.documentElement.innerHTML);");
            }
        });
		this.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activity和Webview根据加载程度决定进度条的进度大小
				// 当加载到100%的时候 进度条自动消失
				((Activity) context).setProgress(progress * 100);
			}
		});
        //不显示滚动条
        this.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //不显示水平滚动条
        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalScrollbarOverlay(true);
        //根据屏幕密度自动适配页面: 擦，bug居然在这里
        /*
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scale = dm.densityDpi;
        Hashtable<Integer, ZoomDensity> hashtable = new Hashtable<Integer, ZoomDensity>();
        hashtable.put(240, ZoomDensity.FAR);
        hashtable.put(160, ZoomDensity.MEDIUM);
        hashtable.put(120, ZoomDensity.CLOSE);
        this.getSettings().setDefaultZoom(hashtable.get(scale));
        */
	}
	
    public DescriptionWebView(final Context context, ViewFlipper flipper, final RSSItem item) {
            super(context);
            this.databaseUtil = DatabaseUtil.initDatabase(context);
            if (this.databaseUtil == null) return;
            this.context = context;
            this.flipper = flipper;
            this.item = item;
            this.isLoadPicture = isLoadPicture();
            
	        while (this.item == null || this.item.getTitle() == null || this.item.getDescription() == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }

	        this.setWebView();
	        String content = item.getContent();
	        if (content == null) {
	        	new SetOnLoadUrlTask().execute(item.getDescription().getContent());
	        } else {
	        	this.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
	        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
    	if (item.getFeedid() != Settings.TUGUA) {	//意图允许滑屏切页
	    	if (!hasAlertFlipper) {
	    		Toast.makeText(context, "快速左右滑动可以切屏", Toast.LENGTH_SHORT).show();
	    		hasAlertFlipper = true;
	    	}
	        boolean consumed = super.onTouchEvent(evt);
	        if (isClickable()) {
	            switch (evt.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                lastTouchX = evt.getX();
	                lastTouchY = evt.getY();
	                downXValue = evt.getX();
	                downTime = evt.getEventTime();
	                hasMoved = false;
	                break;
	            case MotionEvent.ACTION_MOVE:
	                hasMoved = moved(evt);
	                break;
	            case MotionEvent.ACTION_UP:
	                float currentX = evt.getX();
	                long currentTime = evt.getEventTime();
	                float difference = Math.abs(downXValue - currentX);
	                long time = currentTime - downTime;
	                Log.d("Touch Event:", "Distance: " + difference + "px Time: "+ time + "ms");
	                /** X轴滑动距离大于100，并且时间小于300ms,并且向X轴右方向滑动   && (time < 300) */
	                if ((downXValue < currentX) && (difference > 100 && (time < 300))) {
	                	if (this.flipper.getChildAt(0) == this.flipper.getCurrentView()) {
		                	RSSItem nextItem = this.databaseUtil.getNextItem(this.item); 
		                	if (nextItem.getId() == 0) {
		                		Toast.makeText(context, "沒有前一篇了。", Toast.LENGTH_SHORT).show();
		                		break;
		                	}
		                	this.flipper.addView(new DescriptionWebView(this.context, this.flipper, nextItem), 0);
		                	this.flipper.setDisplayedChild(this.flipper.getDisplayedChild());
		                	((Activity)this.context).setTitle(((DescriptionWebView)flipper.getChildAt(flipper.getDisplayedChild())).getTitle());
	                	}
	                    /** 跳到上一页 */
	                    this.flipper.setInAnimation(AnimationUtils.loadAnimation(
	                            this.getContext(), R.layout.push_right_in));
	                    this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
	                            this.getContext(), R.layout.push_right_out));
	                    if (this.flipper.getDisplayedChild() != 0) {
		                	this.flipper.showPrevious();
		                	((Activity)this.context).setTitle(((DescriptionWebView)flipper.getChildAt(flipper.getDisplayedChild())).getTitle());
	                    }
	                    Log.d(TAG, "flipper有多少个view：" + String.valueOf(flipper.getChildCount()) + ",当前第" + String.valueOf(flipper.getDisplayedChild()));
	                }
	                /** X轴滑动距离大于100，并且时间小于300ms,并且向X轴左方向滑动 */
	                if ((downXValue > currentX) && (difference > 100) && (time < 300)) {
	                	if (this.flipper.getChildAt(this.flipper.getChildCount()-1) == this.flipper.getCurrentView()) {
	                		RSSItem preItem = this.databaseUtil.getPreItem(this.item);
		                	if (preItem.getId() == 0) {
		                		Toast.makeText(context, "沒有后一篇了。", Toast.LENGTH_SHORT).show();
		                		break;
		                	}
		                	this.flipper.addView(new DescriptionWebView(this.context, this.flipper, preItem), -1);
	                	}
	                    /** 跳到下一页 */
	                    this.flipper.setInAnimation(AnimationUtils.loadAnimation(
	                            this.getContext(), R.layout.push_left_in));
	                    this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
	                            this.getContext(), R.layout.push_left_out));
	                    this.flipper.showNext();
	                    ((Activity)this.context).setTitle(((DescriptionWebView)flipper.getChildAt(flipper.getDisplayedChild())).getTitle());
	                    Log.d(TAG, "flipper有多少个view：" + String.valueOf(flipper.getChildCount()) + ",当前第" + String.valueOf(flipper.getDisplayedChild()));
	                }
	                break;
	            }
	        }
	        return consumed || isClickable();
    	} else {
    		return super.onTouchEvent(evt);
    	}
    }
    
    private boolean moved(MotionEvent evt) {
        return hasMoved || Math.abs(evt.getX() - lastTouchX) > 10.0
                || Math.abs(evt.getY() - lastTouchY) > 10.0;
    }
	 
    public String getTitle () {
    	return this.title;
    }
    
    public RSSItem getItem () {
    	return this.item;
    }
    
    public boolean isLoadPicture() {
    	return (new Settings(this.context)).isLoadPicture();
    }
    
    /**
     * 某个item被查看之后将其内容保留到数据库中，提高用户体验，弊端是服务器内容如果更新，客户端不会察觉
     * @author Jawinton
     *
     */
    private class SetOnLoadUrlTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String content = args[0];
			try {
				content = NetworkUtil.getContent(args[0]);
				if (databaseUtil.updateContent(item.getId(), content) <= 0) {
					content = args[0];
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return content;
		}

		protected void onPostExecute(String html) {
			if (html != null && html.startsWith("http"))
				loadUrl(html);
			else
				loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		}
	}
}
