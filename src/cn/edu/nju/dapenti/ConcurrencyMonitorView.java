package cn.edu.nju.dapenti;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * 显示页面停留时间，测试主界面阻塞情况
 * 
 * @author Luy
 * 
 */
public class ConcurrencyMonitorView extends TextView {

	private Timer visTimer;
	//private int tickCount = 0;
	private final Handler handler = new Handler();
	//private int visibility;

	public ConcurrencyMonitorView(Context context) {
		this(context, null);
	}

	/**
	 */
	public ConcurrencyMonitorView(Context context, AttributeSet attributeset) {
		super(context);

		if (visTimer == null) {
			visTimer = new Timer();
			visTimer.schedule(new TimerTask() {
				public void run() {
					//++tickCount;
					handler.post(new Runnable() {
						public void run() {
							//setText(tickCount + "");
						}
					});
				}
			}, 1000, 1000);
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int v) {
		//visibility = v;
		super.onWindowVisibilityChanged(v);

		// notice webview
		if (View.VISIBLE == v) {
		} else {
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		visTimer.cancel();
		visTimer = null;
		
		super.onDetachedFromWindow();
	}
}
