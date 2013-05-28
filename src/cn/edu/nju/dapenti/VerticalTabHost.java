package cn.edu.nju.dapenti;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import cn.edu.nju.dapenti.db.DatabaseUtil;

public class VerticalTabHost extends TabActivity{
	private TabHost tabHost;
    private TabHost.TabSpec spec;
    private DatabaseUtil databaseUtil = null;
    //private VerticalTabHost verticalTabHost = this;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_widget);
        tabHost = getTabHost();
        this.databaseUtil = DatabaseUtil.initDatabase(this);
        if (this.databaseUtil == null) return;
        Intent intentSpec;
        Bundle bundle;
        for (Integer i : Settings.ID_NAME_MAP.keySet()) {
        	intentSpec = new Intent(this,RSSReader.class);
        	bundle = new Bundle();
        	bundle.putInt("id", i);
            intentSpec.putExtra("android.intent.extra.INTENT", bundle);
            spec=tabHost.newTabSpec(i.toString()).setIndicator(Settings.ID_NAME_MAP.get(i)).setContent(intentSpec);
            tabHost.addTab(spec);
        }
        tabHost.setCurrentTab((new Settings(this)).getChannel()-1);
        
        final TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
        	final TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
        	tv.setTextSize(20);
        	//设置文字高度
        	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0); //取消文字底边对齐
        	params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); //设置文字居中对齐
        }
        
        /** 
         * 当点击tab选项卡的时候的事件
         */
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {  
            public void onTabChanged(String tabId) {  
            	int favTab = Settings.FAV-1; 
                if (tabHost.getCurrentTab() == favTab) {  
                	RSSReader FavRSSReader = (RSSReader) getLocalActivityManager().getActivity(String.valueOf(Settings.FAV));
                	FavRSSReader.onRefresh();
                }
            }  
        });
    }

}
