package com.next.dynamic;

import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.next.app.StandardApplication;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;

public class SHDynamicManager implements ITaskListener{

	private static SHDynamicManager __mInstance = new SHDynamicManager();
	private String mURL;
	
	public void setURL (String value){
		mURL = value;
	}
	/**
	 * 开始
	 */
	public void start(){
		SHPostTaskM  post = new SHPostTaskM();
		post.setUrl(mURL);
		SharedPreferences sp = StandardApplication.getInstance().getSharedPreferences("update", Context.MODE_PRIVATE);
		post.getTaskArgs().put("v",sp.getString(SHEnvironment.getInstance().getVersion().toString(), "0") );
		post.setListener(this);
		post.start();
	}
	public static SHDynamicManager getInstance()	{
		return __mInstance;
	}
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		JSONObject result =(JSONObject)task.getResult();
		String jsonS = result.getString("replace");
		String fileS = result.getString("file");
		byte[] fileB = Base64.decode(fileS, Base64.DEFAULT);
		//__u.jar
		String path = StandardApplication.getInstance().getFilesDir()
				.getAbsolutePath();
		File file = new File(path + "/__u.jar");
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(fileB);
		fos.close();
		//__r.btn
		file = new File(path + "/__r.bin");
		fos = new FileOutputStream(file);
		fos.write(jsonS.getBytes());
		fos.close();
		//刷新
		SharedPreferences sp = StandardApplication.getInstance().getSharedPreferences("update", Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString("v", result.getString("v"));
		ed.putString(SHEnvironment.getInstance().getVersion().toString(), result.getString("v"));
		ed.commit();
		SHClass.reFresh();
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
