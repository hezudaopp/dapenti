package cn.edu.nju.dapenti.menu.handler;

import cn.edu.nju.dapenti.ShowDescription;
import cn.edu.nju.dapenti.entity.RSSItem;
import android.content.Context;
import android.content.Intent;

public class MenuHandlerShare implements MenuHandlerInterface {
	
	public void handle(Context context) {
		if (context instanceof ShowDescription) {
			RSSItem item = ((ShowDescription)context).getRSSItem();
			Intent intent=new Intent(Intent.ACTION_SEND);   
			intent.setType("text/plain");   
			intent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle().getContent());   
			intent.putExtra(Intent.EXTRA_TEXT, item.getTitle().getContent() + "\n" + item.getLink().getContent());    
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
			context.startActivity(Intent.createChooser(intent, "可以选择下列方式分享"));
		}
	}

}
