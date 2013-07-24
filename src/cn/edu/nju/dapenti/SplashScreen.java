package cn.edu.nju.dapenti;

import cn.edu.nju.dapenti.R;
import cn.edu.nju.dapenti.exceptions.MyUncaughtExceptionHandler;
import cn.edu.nju.dapenti.update.UpdateManager;
import cn.edu.nju.dapenti.utils.NetworkUtil;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreen extends Activity {
    /**
     * Called when the activity is first created.
     */
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.splash_screen);
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler.getInstance(this));
        new Handler().postDelayed(new Runnable() {
            public void run() {
            	//等待界面的同时进行更新操作
            	UpdateManager updateManager = new UpdateManager(SplashScreen.this);
            	if (NetworkUtil.isNetworkAvailable(SplashScreen.this)) {
            		updateManager.update();
            	} else {
            		updateManager.notNewVersionShow();
            	}
            	/*
                if (NetworkUtil.isNetworkAvailable(SplashScreen.this) && updateManager.getServerVer()) {
                	String verName = updateManager.getVerName();
                	String newVerName = updateManager.getNewVerName();
                	if (newVerName.compareTo(verName) > 0) {
                		updateManager.doNewVersionUpdate(SplashScreen.this);
                	} else {
                		updateManager.notNewVersionShow(SplashScreen.this);
                	}
                } else {
                	updateManager.notNewVersionShow(SplashScreen.this);
                }
                /*
            	Intent mainIntent = new Intent(SplashScreen.this, VerticalTabHost.class);
            	SplashScreen.this.startActivity(mainIntent);
        		SplashScreen.this.finish();
        		*/
            }
        }, 100); //how many milliseconds for release
    }
}

