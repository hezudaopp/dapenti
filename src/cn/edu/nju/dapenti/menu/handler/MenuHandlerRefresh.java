package cn.edu.nju.dapenti.menu.handler;

import cn.edu.nju.dapenti.DescriptionWebView;
import cn.edu.nju.dapenti.RSSReader;
import cn.edu.nju.dapenti.ShowDescription;
import android.content.Context;
import android.webkit.WebSettings;

public class MenuHandlerRefresh implements MenuHandlerInterface {

	public void handle(Context context) {
		if (context instanceof ShowDescription) {
			ShowDescription showDescription = (ShowDescription) context;
			DescriptionWebView webView = (DescriptionWebView) ((ShowDescription)context).getFlipper().getCurrentView();
			WebSettings webSettings = webView.getSettings();
			webSettings.setBlockNetworkImage(!showDescription.refreshIsLoadPicture());
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			webView.reload();
			webView.loadUrl(webView.getItem().getDescription().getContent());
		} else if (context instanceof RSSReader) {
			((RSSReader) context).onRefresh();
		}
	}
	

}
