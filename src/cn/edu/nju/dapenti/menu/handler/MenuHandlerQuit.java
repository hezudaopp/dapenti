package cn.edu.nju.dapenti.menu.handler;

import cn.edu.nju.dapenti.RSSReader;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

public class MenuHandlerQuit implements MenuHandlerInterface {
	
	public void handle(Context context) {
		if (!(context instanceof RSSReader)) {
			Intent intent = new Intent(Intent.ACTION_MAIN);  
            intent.addCategory(Intent.CATEGORY_HOME);  
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            context.startActivity(intent); 
		}
		Process.killProcess(Process.myPid());
	}

}
