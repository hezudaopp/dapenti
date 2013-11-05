package cn.edu.nju.dapenti.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;

public class DemoActivity extends Activity {
	final protected String TAG = "Baidu SDK Demo";
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
        	Log.w(TAG, "ORIENTATION_LANDSCAPE");
        }else{
        	Log.w(TAG, "Configuration.ORIENTATION_PORTRAIT");
        }
    }
}