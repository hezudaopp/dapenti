package cn.edu.nju.dapenti.menu.handler;

import android.content.Context;
import cn.edu.nju.dapenti.RSSReader;
import cn.edu.nju.dapenti.db.DatabaseUtil;

public class MenuHandlerClear implements MenuHandlerInterface {
	public void handle(Context context) {
		DatabaseUtil databaseUtil = DatabaseUtil.initDatabase(context);
		if (databaseUtil == null) return;
		databaseUtil.deleteItems(((RSSReader)context).getFeedid());
		((RSSReader)context).onClear();
	}

}
