package cn.edu.nju.dapenti.menu.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cn.edu.nju.dapenti.ShowDescription;
import cn.edu.nju.dapenti.RSSReader;
import cn.edu.nju.dapenti.db.DatabaseUtil;
import cn.edu.nju.dapenti.entity.RSSItem;

public class MenuHandlerFav implements MenuHandlerInterface{
	private final String TAG = "FavHandler";
	
	private Context context; 

	public void handle(Context context) {
		this.context = context;
		RSSItem item = null;
		if (this.context instanceof ShowDescription)
			item = ((ShowDescription)this.context).getRSSItem();
		else if (this.context instanceof RSSReader)
			item = ((RSSReader)this.context).getSelectedItem();
		if (item != null) new SetFavTask().execute(item);
	}
	
	/**
     * The actual AsyncTask that will asynchronously update the database and download image
     */
    private class SetFavTask extends AsyncTask<RSSItem, Void, RSSItem> {
    	private RSSItem item;

        /**
         * Actual set item as fav item.
         */
        @Override
        protected RSSItem doInBackground(RSSItem... params) {
        	DatabaseUtil databaseUtil = DatabaseUtil.initDatabase(context);
    		if (databaseUtil == null) {
    			Log.w(TAG, "database init failed");
    			return item;
    		}
        	item = params[0];
    		item.setFav(databaseUtil.changeFav(item));
            return item;
        }

        /**
         * Once the item is faved or unFaved, what should be done
         */
        @Override
        protected void onPostExecute(RSSItem item) {
            Log.d(TAG, "item fav changed");
            String toast = ((item.getFav()) == 1) ? "添加收藏成功" : "取消收藏成功";
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
    }

}
