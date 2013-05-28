package cn.edu.nju.dapenti.menu.handler;

import java.util.Hashtable;

import cn.edu.nju.dapenti.R;

public class MenuHandlerSimpleFactory {
	
	static Hashtable<Integer, MenuHandlerInterface> ht = new Hashtable<Integer, MenuHandlerInterface>();
	
	protected static void initHashtable () {
		ht.put(R.id.menu_refresh, new MenuHandlerRefresh());
		ht.put(R.id.menu_settings, new MenuHandlerSettings());
		ht.put(R.id.menu_quit, new MenuHandlerQuit());
		ht.put(R.id.menu_clear, new MenuHandlerClear());
		ht.put(R.id.menu_share, new MenuHandlerShare());
		ht.put(R.id.menu_fav, new MenuHandlerFav());
		ht.put(R.id.menu_cancel_fav, new MenuHandlerFav());
		ht.put(R.id.menu_copy, new MenuHandlerCopy());
	}
	
	public static MenuHandlerSimpleFactory newInstance () {
		initHashtable();
		return new MenuHandlerSimpleFactory();
	}
	
	public MenuHandlerInterface createMenuHandler (int menuId) {
		return ht.get(menuId);
	}
}
