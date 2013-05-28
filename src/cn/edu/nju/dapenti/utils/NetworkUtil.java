package cn.edu.nju.dapenti.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtil {
	public static boolean isNetworkAvailable (Context context) {
		ConnectivityManager cwjManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = cwjManager.getActiveNetworkInfo(); 
		if (info != null && info.isAvailable()) return true;
		else return false;
	}
	
	public static boolean isWifiAvailable (Context context) {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED || wifi==State.CONNECTING)
	        return true;
		else return false;
		
	}
	
	public static boolean isMobileAvailable (Context context)
    {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (mobile == State.CONNECTED || mobile==State.CONNECTING)
        	return true;
        else return false;
    }
	
	/**
     * 获取网址内容
     * @param url
     * @return
     * @throws Exception
     */
     public static String getContent(String url) throws Exception{
         StringBuilder sb = new StringBuilder();
         
         HttpClient client = new DefaultHttpClient();
         HttpParams httpParams = client.getParams();
         //设置网络超时参数
         HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
         HttpConnectionParams.setSoTimeout(httpParams, 2000);
         HttpResponse response = client.execute(new HttpGet(url));
         HttpEntity entity = response.getEntity();
         if (entity != null) {
             BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
             String line = null;
             while ((line = reader.readLine())!= null){
                 sb.append(line + "\n");
             }
             reader.close();
         }
         return sb.toString();
     } 
}
