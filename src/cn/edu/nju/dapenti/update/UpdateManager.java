package cn.edu.nju.dapenti.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.nju.dapenti.R;
import cn.edu.nju.dapenti.Settings;
import cn.edu.nju.dapenti.VerticalTabHost;
import cn.edu.nju.dapenti.utils.NetworkUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class UpdateManager {
	//更新模块
	public static final String UPDATE_SERVER = "http://dapenti.com/blog/app/Dapenti.apk";
	public static final String UPDATE_VERJSON = "http://dapenti.com/blog/app/version.json";
	public static final String UPDATE_SAVENAME = "Dapenti.apk";
	private static final String TAG = "UpdateManager";
	
	private int verCode = 0;
	private String verName = "";
	private int newVerCode = 0;
	private String newVerName = "";
	Intent mainIntent;
	Context context;

	private final static int CREATE = 0;
	private final static int FETCHING = 1;
	private final static int DONE = 2;
	private final static int CANCEL = 3;
	private int fileSize;
	private int downLoadFileSize;
	private TextView tv;
	private ProgressBar pBar;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case CREATE:
					tv.setVisibility(View.VISIBLE);
					pBar.setVisibility(View.VISIBLE);
					pBar.setMax(fileSize);
				case FETCHING:
					pBar.setProgress(downLoadFileSize);
					int result = downLoadFileSize * 100 / fileSize;
					tv.setText(result + "%");
					break;
				case DONE:
					tv.setVisibility(View.GONE);
					pBar.setVisibility(View.GONE);
					Toast.makeText(context, "文件下载完成", Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					try {
						intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(),
								UPDATE_SAVENAME)),
								"application/vnd.android.package-archive");
					} catch (NullPointerException e) {
						Toast.makeText(context, "转换文件失败。", Toast.LENGTH_SHORT)
								.show();
						notNewVersionShow();
					}
					context.startActivity(intent);//进入安装Activity
					((Activity) context).finish();
					break;
				case CANCEL:
					tv.setVisibility(View.GONE);
					pBar.setVisibility(View.GONE);
					String error = msg.getData().getString("error");
					Toast.makeText(context, error, Toast.LENGTH_LONG).show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public UpdateManager(Context context) {
		this.context = context;
		this.mainIntent = new Intent(context, VerticalTabHost.class);
		try {
			this.verCode = context.getPackageManager().getPackageInfo(
					"cn.edu.nju.dapenti", 0).versionCode;
			this.verName = context.getPackageManager().getPackageInfo(
					"cn.edu.nju.dapenti", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(context, "获取版本信息失败。", Toast.LENGTH_SHORT).show();
		}
	}

	public int getNewVerCode() {
		return this.newVerCode;
	}

	public String getNewVerName() {
		return this.newVerName;
	}

	public int getVerCode() {
		return this.verCode;
	}

	public String getVerName() {
		return this.verName;
	}

	private class RetreiveNewVerTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			try {
				String verjson = NetworkUtil.getContent(urls[0]);
				JSONArray array = new JSONArray(verjson);
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					try {
						newVerName = obj.getString("version_name");
					} catch (Exception e) {
						newVerName = "";
					}
				}
			} catch (Exception e) {
				return "";
			}
			return newVerName;
		}

		protected void onPostExecute(String string) {
			doUpdate();
		}
	}

	public void update() {
		new RetreiveNewVerTask().execute(UPDATE_VERJSON);
	}

	private void doUpdate() {
		if (newVerName.compareTo(verName) > 0) {
			doNewVersionUpdate();
		} else {
			notNewVersionShow();
		}
	}

	public void doNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(this.verName);
		sb.append(", 发现新版本:");
		sb.append(this.newVerName);
		sb.append(", 是否更新?");
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("软件更新");
		builder.setMessage(sb.toString());
		// 设置内容
		builder.setPositiveButton("更新",// 设置确定按钮
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						downFile(UPDATE_SERVER);
					}
				});
		builder.setNegativeButton("暂不更新",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"取消"按钮之后进入主界面
						notNewVersionShow();
					}
				});
		Dialog dialog = builder.create();// 创建
		dialog.show(); // 显示对话框
	}

	public void notNewVersionShow() {
		context.startActivity(mainIntent);
		((Activity) context).finish();
	}

	private boolean downFile(final String urlStr) {
		pBar = (ProgressBar) ((Activity) context).findViewById(R.id.down_pb);
		tv = (TextView) ((Activity) context).findViewById(R.id.tv);
		new Thread() {
			public void run() {
				try {
					// 获取文件名
					URL myURL = new URL(urlStr);
					URLConnection conn = myURL.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					fileSize = conn.getContentLength();// 根据响应获取文件大小
					if (fileSize <= 0)
						throw new RuntimeException("无法获知文件大小 ");
					if (is == null)
						throw new RuntimeException("stream is null");
					File file = new File(
							Environment.getExternalStorageDirectory(),
							UPDATE_SAVENAME);
					FileOutputStream fos = new FileOutputStream(file);
					// 把数据存入路径+文件名
					byte buf[] = new byte[1024];
					downLoadFileSize = 0;
					sendMsg(CREATE);
					do {
						// 循环读取
						int numread = is.read(buf);
						if (numread == -1)
							break;
						fos.write(buf, 0, numread);
						downLoadFileSize += numread;
						sendMsg(FETCHING);// 更新进度条
					} while (true);
					sendMsg(DONE);// 通知下载完成
					try {
						is.close();
					} catch (Exception ex) {
						Log.e(TAG, "error: " + ex.getMessage(), ex);
					}
				} catch (ClientProtocolException e) {
					sendMsg(CANCEL);
					e.printStackTrace();
				} catch (IOException e) {
					sendMsg(CANCEL);
					e.printStackTrace();
				}
			}
		}.start();
		return true;
	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}
}
