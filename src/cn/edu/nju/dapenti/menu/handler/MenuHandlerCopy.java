package cn.edu.nju.dapenti.menu.handler;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.Log;
import cn.edu.nju.dapenti.RSSReader;
import cn.edu.nju.dapenti.entity.RSSItem;
import cn.edu.nju.dapenti.utils.StringUtil;

public class MenuHandlerCopy implements MenuHandlerInterface{
	private final String TAG = "CopyHandler";
	
	private Context context; 

	public void handle(Context context) {
		if (context instanceof RSSReader) {
			this.context = context;
			RSSItem item =((RSSReader)this.context).getSelectedItem();
			ClipboardManager clip = (ClipboardManager)this.context.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(StringUtil.Html2Text(item.getDescription().getContent())); // 复制
//			ClipData clipdata =ClipData.newPlainText("simple text",StringUtil.Html2Text(item.getDescription().getContent()));
//			clip.setPrimaryClip(clipdata);
			Log.d(TAG, StringUtil.Html2Text(item.getDescription().getContent()) + " copied");
		}
	}
}
