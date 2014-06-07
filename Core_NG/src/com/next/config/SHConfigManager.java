package com.next.config;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.next.app.StandardApplication;
import com.next.base.EventListener;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


public class SHConfigManager implements ITaskListener, EventListener {
	//broadcast
	public final static String CORE_NOTIFICATION_CONFIG_STATUS_CHANGED = "core_notification_config_status_changed";
	private final static SHConfigManager __instance = new SHConfigManager();
	private SHVersion mMinVersion;// 最小
	private SHVersion mNewVersion;// 最新
	private String mUpdateContent;// 升级提示
	private String mUpdateDate;// 更新时间
	private ArrayList<String> mUpdateapkUrllist;//apk省级包
	private Boolean mIsMaintenanceMode = false;
	private String mPushNotice = "";
	private Boolean mHasPushNotice = false;
	private int mMaxDistance = 2000;
	private int mMaxAccuracy = 500; 
	private int mCheckinCount = 3;//间隔次数
	private int mCheckinTime = 120;//间隔时间：分钟
	private boolean mIsValidateCheckin = true; //是否限制签到
	private boolean isPush = false;
	private SHConfigState mState = SHConfigState.Done;
	private SHPostTaskM  mTask ;
	private String mUrl;
	/**
	 * URL
	 * @param value
	 */
	public void setURL (String value){
		this.mUrl = value;
		this.reFresh();
	}
	/**
	 * 重试
	 */
	public void reFresh(){
		mTask = new SHPostTaskM();
		mTask.setUrl(mUrl);
		mTask.setListener(this);
		mTask.start();
	}
	public SHConfigState getState()
	{
		return mState;
		
	}
	public int getCheckInTime(){
		return mCheckinTime;
	}
	public int getCheckInCount(){
		return mCheckinCount;
	}
	public boolean isValidateCheckin(){
		return mIsValidateCheckin;
	}
	public boolean getIsPush(){
		return isPush;
	}
	public int getMaxAccuracy()
	{
		return mMaxAccuracy;
	}
	public int getMaxDistance()
	{
		return mMaxDistance;
	}
	public SHConfigManager() {
		//初始化
		this.initial();
		//加载本地数据
		//http请求
	}
	
	/**
	 * 开始
	 */
	private void initial() {
		mMinVersion = new SHVersion("0.0.0");
		mNewVersion = new SHVersion("0.0.0");
		mUpdateDate = "2012-10-29";
		mUpdateapkUrllist = new ArrayList<String>();
	}

	public ArrayList<String> getUpdateUrl() {
		return mUpdateapkUrllist;
	}
	/**
	 * 升级提示
	 * @return
	 */
	public String getUpdateContent() {
		return mUpdateContent;
	}
	/**
	 * 是否强制升级
	 * @return
	 */
	public Boolean getIsMaintenanceMode() {
		return mIsMaintenanceMode;
	}
	/**
	 * 是否有push
	 * @return
	 */
	public Boolean getHasPushNotice() {
		return mHasPushNotice;
	}
	public String getPushNotice() {
		return mPushNotice;
	}
	public SHVersion getMinVersion() {
		return mMinVersion;
	}
	public SHVersion getNewVersion() {
		return mNewVersion;
	}

	public String getUpdateDate() {
		return mUpdateDate;
	}

	public static SHConfigManager getInstance() {
		return __instance;
	}

	public void show(final Context t){
		if((this.mIsMaintenanceMode && (this.mNewVersion.isNewer(SHEnvironment.getInstance().getVersion())))
				|| this.mMinVersion.isNewer(SHEnvironment.getInstance().getVersion())){
			
			new Builder(t).setTitle("提示").setMessage(this.mUpdateContent)
			.setNeutralButton("退出", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					System.exit(0);
				}
			})
			.setNegativeButton("更新", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					SHDownloadHelper dp = new SHDownloadHelper(t);
					dp.start(mUpdateapkUrllist);
					dp.addListener(SHConfigManager.this);
				}
			})
			.show();
		}else if(this.mNewVersion.isNewer(SHEnvironment.getInstance().getVersion()) ){
			new Builder(t).setTitle("提示").setMessage(this.mUpdateContent)
			.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.dismiss();
				}
			})
			.setNegativeButton("更新", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SHDownloadHelper dp = new SHDownloadHelper(t);
					dp.start(mUpdateapkUrllist);
					dp.addListener(SHConfigManager.this);
				}
			})
			.show();
		}
	}
	@Override
	public void onListenerRespond(Object sender, Object args) {
		// TODO Auto-generated method stub
		if((this.mIsMaintenanceMode && (this.mNewVersion.isNewer(SHEnvironment.getInstance().getVersion())))
				|| this.mMinVersion.isNewer(SHEnvironment.getInstance().getVersion())){
			System.exit(0);
		}
	}
	/**
	 * 读取配置
	 */
	private void doConfig(JSONObject xml) throws JSONException {
		if (xml.length() > 0) {
			// 解析
			if (!xml.isNull("maxdistance")) {
				mMaxDistance = xml.getInt("maxdistance");
			}
			if (!xml.isNull("accuracy")) {
				mMaxAccuracy = xml.getInt("accuracy");
			}
			if (!xml.isNull("checkincount")) {
				mCheckinCount = xml.getInt("checkincount");
			}
			if (!xml.isNull("checkintime")) {
				mCheckinTime = xml.getInt("checkintime");
			}
			if (!xml.isNull("push")) {
				isPush = xml.getBoolean("push");
			}
			if (!xml.isNull("validateCheckin")) {
				mIsValidateCheckin = xml.getBoolean("validateCheckin");
			}
		}
	}

	/**
	 * 读升级信息
	 * 
	 * @param xml
	 * @throws JSONException
	 */
	private void doUpdate(JSONObject xml) throws JSONException {
		if (xml.length() > 0) {
			if (!xml.isNull("newVersion")) { 
				mNewVersion = new SHVersion(xml.getString("newVersion").trim());
			}
			if (!xml.isNull("minVersion")) {
				mMinVersion = new SHVersion(xml.getString("minVersion"));
			}
			if (!xml.isNull("content")) {
				mUpdateContent = xml.getString("content");
			}
			if (!xml.isNull("updateDate")) {
				mUpdateDate = xml.getString("updateDate");
			}
			if (xml.getJSONArray("updateURL") != null) {
				JSONArray updateUrls = xml.getJSONArray("updateURL");
				mUpdateapkUrllist.clear();
				for (int j = 0; j < updateUrls.length(); j++) {
					String url = updateUrls.getString(j);
					if (url != null && url.trim().length() > 0) {
						mUpdateapkUrllist.add(url.trim());
					}
				}
			}
			if (!xml.isNull("hasPushNotice")) {
				this.mHasPushNotice = xml.getBoolean("hasPushNotice");
			}
			if (!xml.isNull("pushNotice")) {
				this.mPushNotice = xml.getString("pushNotice");
			}
			if (!xml.isNull("isMaintenanceMode")) {
				this.mIsMaintenanceMode = xml.getBoolean("isMaintenanceMode");
			}
		}
	}
	
	/**
	 * Task返回
	 * @param task
	 */
	@Override
	public void onTaskFinished(SHTask task) {
		// TODO Auto-generated method stub
		try {
			JSONObject update = ((JSONObject)task.getResult()).getJSONObject("update");
			JSONObject config = ((JSONObject)task.getResult()).getJSONObject("config");
			this.doUpdate(update);
			this.doConfig(config);
			mState = SHConfigState.Done;
			Intent intent = new Intent(CORE_NOTIFICATION_CONFIG_STATUS_CHANGED);
			StandardApplication.getInstance().sendBroadcast(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		mState = SHConfigState.Fail;
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
