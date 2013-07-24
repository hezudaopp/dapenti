package cn.edu.nju.dapenti.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.edu.nju.dapenti.Settings;

import android.util.Log;

public class StringUtil { 
	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String PUBDATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
	private static final String TAG = "StringUtil";
	
	public static String replaceNewLine (StringBuffer strBuffer) { 
	   Pattern p = Pattern.compile("\\n*"); 
	   Matcher m = p.matcher(strBuffer); 
	   return m.replaceAll("");
	}
	
	public static InputStream getInputStreamRemote (String urlToRssFeed) throws IOException {
		URL url = new URL(urlToRssFeed);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(1000);
		return con.getInputStream();
	}
	
	public static String strToDateString(String strDate) {
		strDate = strDate.replace("Wes", "Wed");
		DateFormat formatter = new SimpleDateFormat(PUBDATEFORMAT, Locale.US);
		Date date;
		try {
			date = formatter.parse(strDate);
		} catch (ParseException e) {
			Log.d(TAG, "时间转化出错了");
			return strDate;
		}
		return new SimpleDateFormat(TIMEFORMAT).format(date).toString();
	}
	
	public static String getNowDate() {
	    Calendar calendar= Calendar.getInstance();
	    calendar.setTime(new Date());
	    return new SimpleDateFormat(TIMEFORMAT).format(calendar.getTime());
	}
	
	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr.replaceAll("&nbsp;", " ");// 返回文本字符串
	}

	/*
	public static String getNextDate(String curPubdate) {
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {
			date = (new SimpleDateFormat(TIMEFORMAT)).parse(curPubdate);
		} catch (ParseException e) {
			Log.e(Settings.TAG, "时间格式错误。");
			e.printStackTrace();
		}
		calendar.setTime(date);
		calendar.add(Calendar.DATE,1);
		return new SimpleDateFormat(DATEFORMAT).format(calendar.getTime());
	}
	
	public static String getPreDate(String curPubdate) {
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {
			date = (new SimpleDateFormat(TIMEFORMAT)).parse(curPubdate);
		} catch (ParseException e) {
			Log.e(Settings.TAG, "时间格式错误。");
			e.printStackTrace();
		}
		calendar.setTime(date);
		calendar.add(Calendar.DATE,-1);
		return new SimpleDateFormat(DATEFORMAT).format(calendar.getTime());
	}
	*/
	
	/**
	   * 下载文件到本地
	   *
	   * @param urlString
	   *          被下载的文件地址
	   * @param filename
	   *          本地文件名
	   * @throws Exception
	   *           各种异常
	   */
	/*
	private static void download(Context context, String urlString) {
	    // 构造URL
	    URL url;
		try {
			url = new URL(urlString);
			// 打开连接
		    URLConnection con = url.openConnection();
		    //con.setConnectTimeout(1000);
		    // 输入流
		    InputStream is = con.getInputStream();
		    // 1K的数据缓冲
		    byte[] bs = new byte[1024*100];
		    // 读取到的数据长度
		    int len;
		    // 输出的文件流
		    FileOutputStream fos = context.openFileOutput(RSSReader.FILENAME, Context.MODE_PRIVATE);
		    // 开始读取
		    while ((len = is.read(bs)) != -1) {
		    	fos.write(bs, 0, len);
		    }
		    // 完毕，关闭所有链接
		    fos.close();
		    is.close();
		} catch (Exception e) {
			Log.d(RSSReader.TAG, "download url exception");
			e.printStackTrace();
		}
	}   
	*/
}