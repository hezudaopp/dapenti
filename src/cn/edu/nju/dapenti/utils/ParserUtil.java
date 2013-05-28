package cn.edu.nju.dapenti.utils;

import cn.edu.nju.dapenti.entity.RSSFeed;
import android.os.Handler;
import android.os.Message;

public class ParserUtil {
	
	public static RSSFeed sendProgressMsg(int flag, Handler mProgressHandler, RSSFeed _feed) {
    	if (mProgressHandler != null) {
            Message progressMsg = new Message();
            if (_feed != null)
            	progressMsg.obj = _feed.getItemCount();
            progressMsg.what = flag;
            mProgressHandler.sendMessage(progressMsg);
    	}
    	return _feed;
	}
}
