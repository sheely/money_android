package com.next.crash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.next.app.StandardApplication;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.MainUILooperDelegate;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
public class SHCrashHandler implements UncaughtExceptionHandler, ITaskListener {

	public static final String TAG = "CrashHandler";
	private String path = "/data/data/"+StandardApplication.getInstance().getPackageName()+"/crash/";
	private String fileName = "crash.txt";
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static final SHCrashHandler INSTANCE = new SHCrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	// private DateFormat formatter = new
	// SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private String mUrlHost = "http://202.91.229.145:80";
	public void setUrlHost(String host){
		mUrlHost = host;
	}
	/** 保证只有一个CrashHandler实例 */
	private SHCrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static SHCrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context,String host) {
		try {
			this.setUrlHost(host);
			mContext = context;
			// 获取系统默认的UncaughtException处理器
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			// 设置该CrashHandler为程序的默认处理器
			Thread.setDefaultUncaughtExceptionHandler(this);
			sendLog2Server();
		} catch (Throwable e) {
			// TODO: handle exception
		}

	}

	private SHPostTaskM getSenderTask(String exception) {

		SHPostTaskM task = new SHPostTaskM();
		task.setUrl(mUrlHost + "/openApiPlatform/Service/siemensService");
		task.setListener(this);
		return task;

	}

	private void sendLog2Server() {
		try {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileInputStream fos;
			File file = new File(path + fileName);
			if (!file.exists()) {
				return;
			}
			fos = new FileInputStream(file);
			if (fos != null && fos.available() > 0) {
				byte[] bytes = new byte[fos.available()];
				fos.read(bytes);
				fos.close();
				String log = new String(bytes);
				Log.i("", log);
				// System.out.println(log);
				if (log != null && log.length() > 0) {
					Toast.makeText(mContext, "发送crash日志", Toast.LENGTH_SHORT)
							.show();
					SHTask task = getSenderTask(log);
					task.setListener(this);
					task.start();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		boolean hasoom = false;
		try{
			hasoom =  hasOutOfMemoryError(ex);
			handleException(ex);
			
		}catch(Throwable e){
			
		}finally{
			if(hasoom){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}else if(mDefaultHandler != null && !MainUILooperDelegate.isInstalled()){
				mDefaultHandler.uncaughtException(thread, ex);
			}
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		try {

			if (ex == null) {
				return false;
			}
			// 使用Toast来显示异常信息
//			new Thread() {
//				@Override
//				public void run() {
//					Looper.prepare();
//					Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.",
//							Toast.LENGTH_LONG).show();
//					Looper.loop();
//				}
//			}.start();
			// 收集设备参数信息
			collectDeviceInfo(mContext);
			// 保存日志文件
			saveCrashInfo2File(ex);
		} catch (Throwable e) {
			// TODO: handle exception
		}
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {		
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName,
						true);
				fos.write(sb.toString().getBytes());
				fos.close();
			
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	@Override
	public void onTaskFinished(SHTask task) {
		// TODO Auto-generated method stub
		try {
			if (task.getResult() != null
					&& (task.getResult() instanceof SoapPrimitive)) {
				Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
				File file = new File(path + fileName);
				try {

					if (!file.exists()) {
						return;
					} else {
						file.delete();
						Toast.makeText(mContext, "crash日志清除",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskTry(SHTask task) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean hasOutOfMemoryError(Throwable ex){
		if(ex == null){
			return false;
		}
		Throwable next = ex.getCause();
		for(int i=0;i<0xF;i++){
			if(ex instanceof OutOfMemoryError){
				return true;
			}
			if(next == null || next == ex){
				return false;
			}
			ex = next;
			next = ex.getCause();
		}
		return false;
	}
}
