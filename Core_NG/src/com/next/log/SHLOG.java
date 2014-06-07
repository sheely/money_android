package com.next.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.serialization.SoapPrimitive;

import com.next.app.StandardApplication;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

public class SHLOG implements ITaskListener {

	private static final SHLOG  mInstance = new SHLOG();
	private FileOutputStream fos;
	private String mPath = "/data/data/"+StandardApplication.getInstance().getPackageName()+"/log/";
	private String mPathAbsolut = mPath + "Log.txt";
	private String mUrlHost = "http://202.91.240.88:8080";
	public void setUrlHost(String host){
		mUrlHost = host;
	}
	public static final SHLOG getInstance(){
		return mInstance;
	}
	public void addLog(String type,String action,String other,Boolean im){
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sf.format(Calendar.getInstance().getTime());
			fos.write(("{[UID="+com.next.util.SHEnvironment.getInstance().getClientID()+"]"+"[TYP="+type+"][ACT="+action+"][OTH="+other+"][DAT="+date+"]}\n").getBytes());
			fos.flush();
			if(im){
				mSender.weakUp();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addLog(String type,String action,String other){
		this.addLog(type, action, other,false);
	}
	private SenderLogTask mSender = new SenderLogTask();
	
	private class SenderLogTask extends AsyncTask<Void, Void, Void>  {

		Thread thread ;
		public void weakUp(){
			if(thread != null){
				thread.interrupt();
			}
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while (true) {

				try {
					sendeMessage();
					thread = Thread.currentThread();
					Thread.sleep(300000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private synchronized void sendeMessage(){
		FileInputStream fos;
		File file = new File(mPathAbsolut);
		try {
		if (file.exists()) {
			fos = new FileInputStream(file);
			if (fos != null && fos.available() > 0) {
				byte[] bytes = new byte[fos.available()];
				fos.read(bytes);
				fos.close();
				String log = new String(bytes);
				Log.i("", log);
				// System.out.println(log);
				if (log != null && log.length() > 0) {
					SHTask task = getSenderTask(log);
					task.setListener(SHLOG.this);
					task.start();
				}
			}
			
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private SHPostTaskM getSenderTask(String exception) {

		SHPostTaskM task = new SHPostTaskM();
		task.setUrl(mUrlHost + "/openApiPlatform/Service/siemensService");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		task.getTaskArgs().put("", "");
		task.setListener(this);
		return task;

	}

	public  SHLOG() {
		super();
		mSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			File dir = new File(mPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			try {
				fos = new FileOutputStream(mPathAbsolut,true);
				fos.write("".getBytes());
				fos.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	@Override
	public void onTaskFinished(SHTask task) {
		// TODO Auto-generated method stub
		if (task.getResult() != null
				&& (task.getResult() instanceof SoapPrimitive)) {
			if (task.getResult() != null
					&& task.getResult().toString().equalsIgnoreCase("1")) {
				File dir = new File(mPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				try {
					if (fos != null) {
						fos.close();
					}
					fos = new FileOutputStream(mPathAbsolut,false);
					fos.write("".getBytes());
					fos.flush();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTaskTry(SHTask task) {
		// TODO Auto-generated method stub
		
	}

}
