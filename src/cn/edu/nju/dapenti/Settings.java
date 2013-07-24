package cn.edu.nju.dapenti;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import cn.edu.nju.dapenti.utils.NetworkUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity implements OnPreferenceChangeListener{  
	public static final int TUGUA = 1;
	public static final int DUANZI = 2;
	public static final int TUPIAN = 3;
	public static final int FAV = 4;
	/*
	public static final int XILEI = 4;
	public static final int AGILE = 5;
	public static final int CAIJING = 6;
	public static final int KIDS = 7;
	public static final int DAPENTI = 8;
	public static final int MUSICS = 9;
	*/
	public static final Map<Integer, String> ID_URL_MAP = createUrlMap();
	public static final Map<Integer, String> ID_NAME_MAP = createNameMap();
    private static Map<Integer, String> createNameMap() {
        Map<Integer, String> result = new TreeMap<Integer, String>();
        result.put(TUGUA, "图卦");
        result.put(DUANZI, "段子");
        result.put(TUPIAN, "意图");
        result.put(FAV, "收藏");
        /*
        result.put(XILEI, "锟斤拷锟斤拷");
        result.put(AGILE, "锟街伙拷");
        result.put(CAIJING, "锟狡撅拷");
        result.put(KIDS, "锟斤拷锟斤拷");
        result.put(DAPENTI, "锟斤拷锟斤拷");
        result.put(MUSICS, "锟斤拷锟斤拷");
        */
        return Collections.unmodifiableMap(result);
    }
    private static Map<Integer, String> createUrlMap() {
        Map<Integer, String> result = new TreeMap<Integer, String>();
        result.put(TUGUA, "http://dapenti.com/blog/tuguaapp.asp");
        result.put(DUANZI, "http://dapenti.com/blog/duanziapp.asp");
        result.put(TUPIAN, "http://dapenti.com/blog/rssapp.asp?name=tupian");
        result.put(FAV, null);
        /*
        result.put(XILEI, "http://dapenti.com/blog/rss2.asp?name=xilei");
        result.put(AGILE, "http://dapenti.com/blog/rss2.asp?name=agile");
        result.put(CAIJING, "http://dapenti.com/blog/rss2.asp?name=caijing");
        result.put(KIDS, "http://dapenti.com/blog/rss2.asp?name=kids");
        result.put(DAPENTI, "http://dapenti.com/blog/rss2.asp?name=dapenti");
        result.put(MUSICS, "http://dapenti.com/blog/rss2.asp?name=musics");
        */
        return Collections.unmodifiableMap(result);
    }
	
	public static final int MAXCOUNT = 8;
	private static final String TAG = "Settings";
	
	private SharedPreferences settings;
	private Context context;
	//锟斤拷取锟斤拷锟斤拷Preference
    private String loadPictureWifiKey;
    private String loadPictureWhetherKey;
    private String channelKey;
    private CheckBoxPreference loadPictureWifiCheckPref;
    private CheckBoxPreference loadPictureWhetherCheckPref;
    private ListPreference channelListPref; 
    private Boolean isOnlyLoadPictureWifiValue;  
    private Boolean isLoadPictureValue;
	private int channel;
	
	public Settings () {
		super();
	}
    
    public Settings(Context context) {
    	this.context = context;
    	//取锟斤拷锟斤拷锟斤拷锟斤拷锟接︼拷贸锟斤拷锟斤拷SharedPreferences  
        settings = PreferenceManager.getDefaultSharedPreferences(context);  
        //为锟斤拷锟斤拷Preference注锟斤拷锟斤拷锟接匡拷  
        loadPictureWhetherKey = context.getResources().getString(R.string.load_picture_whether_key);
		loadPictureWifiKey = context.getResources().getString(R.string.load_picture_wifi_key);
		channelKey = context.getResources().getString(R.string.channel_key);
        isOnlyLoadPictureWifiValue = settings.getBoolean(loadPictureWifiKey, true);
        isLoadPictureValue = settings.getBoolean(loadPictureWhetherKey, true);
        channel = Integer.valueOf(settings.getString(channelKey, "1"));
	}
    
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        //锟斤拷xml锟侥硷拷锟斤拷锟斤拷锟絇reference锟斤拷  
        addPreferencesFromResource(R.xml.shared_preference);
        
        loadPictureWhetherKey = getResources().getString(R.string.load_picture_whether_key);
		loadPictureWifiKey = getResources().getString(R.string.load_picture_wifi_key);
		channelKey = getResources().getString(R.string.channel_key);
        loadPictureWifiCheckPref = (CheckBoxPreference)findPreference(loadPictureWifiKey);
        loadPictureWhetherCheckPref = (CheckBoxPreference)findPreference(loadPictureWhetherKey);
        channelListPref = (ListPreference)findPreference(channelKey);
        loadPictureWifiCheckPref.setOnPreferenceChangeListener(this);  
        loadPictureWhetherCheckPref.setOnPreferenceChangeListener(this);
        channelListPref.setOnPreferenceChangeListener(this);
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {  
		String preKey = preference.getKey();
        //锟叫讹拷锟斤拷锟侥革拷Preference锟侥憋拷锟斤拷  
        if (preKey.equals(loadPictureWifiKey) || preKey.equals(loadPictureWhetherKey)) {  
            Log.d(TAG, "picture checkbox preference is changed");
            if (preKey.equals(loadPictureWifiKey)) 
            	isOnlyLoadPictureWifiValue = (Boolean) newValue;
            if (preKey.equals(loadPictureWhetherKey)) 
            	isLoadPictureValue = (Boolean) newValue;
        } else if (preKey.equals(channelKey)){  
        	channel = Integer.parseInt((String) newValue);
        	Log.d(TAG, "channel listView preference is changed");
        }  else {
        	return false;
        }
        //锟斤拷锟斤拷true锟斤拷示锟斤拷锟斤拷谋锟? 
        return true;  
    }  
	
	public boolean isLoadPicture () {
        if (isLoadPictureValue) {
        	if (!isOnlyLoadPictureWifiValue) return true;
        	if (isOnlyLoadPictureWifiValue && NetworkUtil.isWifiAvailable(context)) {
        		return true;
        	}
        }
        return false;
	}
	
	public int getChannel () {
		return channel;
	}
	
	/*
    CheckBoxPreference loadPictureWifiCheckPref;
    CheckBoxPreference loadPictureWhetherCheckPref;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        //锟斤拷xml锟侥硷拷锟斤拷锟斤拷锟絇reference锟斤拷  
        addPreferencesFromResource(R.xml.shared_preference);  
        //锟斤拷取锟斤拷锟斤拷Preference  
        loadPictureWifiKey = getResources().getString(R.string.load_picture_wifi_key);
        loadPictureWhetherKey = getResources().getString(R.string.load_picture_whether_key);
        //为锟斤拷锟斤拷Preference注锟斤拷锟斤拷锟接匡拷  
        loadPictureWifiCheckPref = (CheckBoxPreference)findPreference(loadPictureWifiKey);  
        loadPictureWhetherCheckPref = (CheckBoxPreference)findPreference(loadPictureWhetherKey);
        loadPictureWifiCheckPref.setOnPreferenceChangeListener(this);  
        loadPictureWifiCheckPref.setOnPreferenceClickListener(this);  
        loadPictureWhetherCheckPref.setOnPreferenceChangeListener(this);  
        loadPictureWhetherCheckPref.setOnPreferenceClickListener(this);         
    }  
    
    public boolean onPreferenceChange(Preference preference, Object newValue) {  
        Log.d("Key_SystemSetting", preference.getKey());  
        //锟叫讹拷锟斤拷锟侥革拷Preference锟侥憋拷锟斤拷  
        if(preference.getKey().equals(loadPictureWifiKey)) {
            Log.d("SystemSetting", "loadPictureWifiKey preference is changed");  
        } else {
            //锟斤拷锟斤拷false锟斤拷示锟斤拷锟斤拷锟?锟侥憋拷  
            return false;  
        }  
        //锟斤拷锟斤拷true锟斤拷示锟斤拷锟斤拷谋锟? 
        return true;
    }  
    
    public boolean onPreferenceClick(Preference preference) {  
        Log.d("Key_SystemSetting", preference.getKey());
        //锟叫讹拷锟斤拷锟侥革拷Preference锟斤拷锟斤拷锟斤拷锟? 
        if(preference.getKey().equals(loadPictureWhetherKey)) {
            Log.d("SystemSetting", "loadPictureWhetherKey preference is clicked");  
        } else {
            return false;
        }
        return true;
    }*/
}  


