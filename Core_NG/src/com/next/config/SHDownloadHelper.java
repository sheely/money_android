package com.next.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.next.app.StandardApplication;
import com.next.base.BaseHelper;
import com.next.util.SHTools;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

public class SHDownloadHelper extends BaseHelper {
	private final static int TAG_CONNECT_START = 0;
	private final static int TAG_CONNECT_INDEX = 1;
	private final static int TAG_DOWNLOAD_PERCENT = 2;
	private final static int TAG_CONNECT_SUCCESS = 3;
	private final static String KEY_PERCENT = "KEY_PERCENT";
	private final static String KEY_URL_INDEX = "KEY_URL_INDEX";
	private static final String TAG = "download";
	private String fileEx = "";
	private String fileNa = "";
	private ArrayList<String> updateUrls;
	private ProgressDialog mProgressdialog;
	private UpdateTask updateTask;
	private Context mContext;
	
	private class UpdateTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPostExecute(Boolean v) {	
			mProgressdialog.cancel();
			mProgressdialog.dismiss();
			final String strURL = SHDownloadHelper.this.updateUrls.get(0);
			if (v) {
				SHDownloadHelper.this.onEventHandler();
			} else {
				new Builder(SHDownloadHelper.this.mContext)
						.setTitle("提示")
						.setMessage("您的系统版本未能成功下载app包，是否直接打开浏览器直接下载?")
						.setNeutralButton("返回",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										SHDownloadHelper.this.onEventHandler();
									}
								})
						.setNegativeButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Uri uri = Uri.parse(strURL);  
										Intent it = new Intent(Intent.ACTION_VIEW, uri);  
										SHDownloadHelper.this.mContext.startActivity(it);
										SHDownloadHelper.this.onEventHandler();
									}
								}).show();
			}
		}
		
		protected void onPreExecute(){
			super.onPreExecute();
			handler.sendEmptyMessage(TAG_CONNECT_START);
		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				return doDownFile();
			} catch (Exception e) {;
			}
			return false;
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case TAG_DOWNLOAD_PERCENT:
				int index = msg.getData().getInt(KEY_PERCENT);
				if (index >= 100) {
					mProgressdialog.cancel();
				}
				mProgressdialog.setProgress(index);
				break;
			case TAG_CONNECT_INDEX:
				String urlIndex = msg.getData().getString(KEY_URL_INDEX);
				mProgressdialog.setMessage("正在尝试链接第" + urlIndex + "个下载地址");
				mProgressdialog.setIndeterminate(true);
				break;
			case TAG_CONNECT_SUCCESS:
				mProgressdialog.setMessage("开始下载，请耐心等待...");
				mProgressdialog.setIndeterminate(false);
				break;
			case TAG_CONNECT_START:
				showWaitDialog();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public  SHDownloadHelper(Context context){
		super();
		this.mContext = context;
	}
	
	public void start(String url){
		this.updateUrls = new ArrayList<String>();
		this.updateUrls.add(url);
		this.proDownFile();
	}
	public void start(ArrayList<String> urls){
		this.updateUrls = urls;
		this.proDownFile();
	}
	private void showWaitDialog() {
		this.mProgressdialog = new ProgressDialog(mContext);
		this.mProgressdialog.setMessage("正在尝试链接服务器");
		this.mProgressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.mProgressdialog.setIndeterminate(true);
		this.mProgressdialog.setMax(100);
		this.mProgressdialog.show();
	}
	// 下载文件
	private void proDownFile() {
		if (this.updateTask == null) {
			this.updateTask = new UpdateTask();
		}
		this.updateTask.execute();
	}

	private Boolean doDownOneFile(String strURL) {
		InputStream is = null;
		try {
			URL myURL = new URL(strURL);
			URLConnection conn = myURL.openConnection();
			conn.connect();
			is = conn.getInputStream();
			if (is == null) {
				throw new RuntimeException("stream is null");
			}
			this.handler.sendEmptyMessage(TAG_CONNECT_SUCCESS);//链接成功
			File myTempFile = null;
			if(android.os.Build.VERSION.SDK_INT > 15){//4.0.3
				myTempFile =  new File(Environment.getExternalStorageDirectory(), fileNa+"."+fileEx);   
			}else{
				myTempFile =  File.createTempFile(fileNa, "." + fileEx);
			}
			if(!myTempFile.exists()){
				myTempFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(myTempFile);
			int contentLength = conn.getContentLength();
			int offset = 0;
			int percent = 0;
			byte buf[] = new byte[128];
			do {
				int numread = is.read(buf);
				offset += numread;
				int index = (int) (offset * 100.0 / contentLength);
				if (index != percent) {
					percent = index;
					Message msgPercent = new Message();
					msgPercent.what = TAG_DOWNLOAD_PERCENT;
					Bundle bundlePercent = new Bundle();
					bundlePercent.putInt(KEY_PERCENT, percent);
					msgPercent.setData(bundlePercent);
					handler.sendMessage(msgPercent);//更新百分比
				}
				if (numread <= 0) {
					break;
				}
				fos.write(buf, 0, numread);
			} while (true);
			Log.i(TAG, "getDataSource() Download  ok...");
			this.mProgressdialog.cancel();
			this.mProgressdialog.dismiss();
			fos.flush();
			this.openFile(myTempFile);
			return true;// 升级成功，退出
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
				Log.e(TAG, "getDataSource() error: " + ex.getMessage(), ex);
				return false;
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean doDownFile() throws Exception {
		Log.i(TAG, "getDataSource()");
		String strURL;
		int i = 0;
		for (; i < this.updateUrls.size(); i++) {
			strURL = this.updateUrls.get(i);
			this.fileEx = strURL.substring(strURL.lastIndexOf(".") + 1,
					strURL.length()).toLowerCase();
			this.fileNa = strURL.substring(strURL.lastIndexOf("/") + 1,
					strURL.lastIndexOf("."));
			if (!URLUtil.isNetworkUrl(strURL)) {
				Log.i(TAG, "getDataSource() It's a wrong URL!");
			} else {
				Message msg = new Message();
				msg.what = TAG_CONNECT_INDEX;
				Bundle bundle = new Bundle();
				bundle.putString(KEY_URL_INDEX, (i + 1) + "");
				msg.setData(bundle);
				this.handler.sendMessage(msg);
				if(doDownOneFile(strURL)){
					break;
				}
			}
		}
		if (i < this.updateUrls.size()) {
			return true;
		} else {
			return false;
		}
	}

	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = SHTools.getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		StandardApplication.getInstance().startActivity(intent);
	}

	
}