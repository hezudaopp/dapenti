package cn.edu.nju.dapenti.menu.handler;

import cn.edu.nju.dapenti.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MenuHandlerSettings implements MenuHandlerInterface {
	public static final int REQ_SYSTEM_SETTINGS = 1;
	
	public void handle(Context context) {
		((Activity) context).startActivityForResult(new Intent(context, Settings.class), REQ_SYSTEM_SETTINGS);  
	}

}
