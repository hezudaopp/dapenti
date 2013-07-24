package cn.edu.nju.dapenti.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private static MyUncaughtExceptionHandler mInstance;
	private Context mContext;

	private MyUncaughtExceptionHandler(Context context) {
		this.mContext = context;
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static MyUncaughtExceptionHandler getInstance(Context context) {
		if (mInstance == null)
			mInstance = new MyUncaughtExceptionHandler(context);
		return mInstance;
	}

	public void init(Context context) {
		
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		StringBuilder sb = new StringBuilder();
		sb.append("Dapenti version name is: ");
		PackageManager pm = mContext.getPackageManager();  
        PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			sb.append(pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		sb.append("Version code is ");
		sb.append(Build.VERSION.SDK_INT + "\n");// 设备的Android版本号
		sb.append("Model is ");
		sb.append(Build.MODEL + "\n");// 设备型号
		sb.append(sw.toString());

		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		sendIntent.setData(Uri.parse("mailto:hezudaopp@163.com"));// 发送邮件
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "发送错误信息给开发者，让我们更好的为您服务");// 邮件主题
		sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());// 堆栈信息
		mContext.startActivity(sendIntent);
		((Activity) mContext).finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		//System.exit(10);
	}
}
