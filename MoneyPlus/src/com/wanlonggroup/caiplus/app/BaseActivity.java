package com.wanlonggroup.caiplus.app;

import java.util.HashMap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.damon.ds.app.DSActivity;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanlonggroup.caiplus.model.AccountService;
import com.wanlonggroup.caiplus.model.AccountService.AccountListener;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.wanlonggroup.caiplus.util.Environment;

public class BaseActivity extends DSActivity implements AccountListener,ITaskListener{
	
	public static final String DEFAULT_API_URL = "http://cjcapp.nat123.net:21414/myStruts1/";
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected static DisplayImageOptions displayOptions = DisplayImageOptions.createSimple();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountService().addListener(this);
	}
	
	protected AccountService accountService() {
		return ((CaiPlusApplication) getApplication()).acccountServie();
	}

	protected boolean isLogined() {
		return !TextUtils.isEmpty(accountService().sessionId());
	}

	@Override
	public final void onAccountChanged(AccountService sender) {
		if(isLogined()){
			SHEnvironment.getInstance().setSession(accountService().sessionId());
		}else{
			SHEnvironment.getInstance().setSession("");
		}
		onAccountChanged();
	}
	
	protected void onAccountChanged(){
		
	}
	
	private HashMap<String, SHPostTaskM> taskMap = new HashMap<String, SHPostTaskM>();
	public SHPostTaskM getTask(String url,ITaskListener listener){
		SHPostTaskM oldTaks = findTask(url);
		if(oldTaks != null){
			oldTaks.cancel(true);
		}
		SHPostTaskM task = new SHPostTaskM();
		task.setUrl(ConfigSwitch.instance().wrapDomain(url));
		task.setListener(listener);
		taskMap.put(url, task);
		return task;
	}
	
	public SHPostTaskM findTask(String url){
		for(String taskUrl : taskMap.keySet()){
			if(taskUrl.equals(url)){
				return taskMap.get(taskUrl);
			}
		}
		return null;
	}
	
	@Override
	protected void onDestroy() {
		accountService().removeListener(this);
		for(SHPostTaskM task : taskMap.values()){
			task.cancel(true);
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Environment.isDebug()) {
			menu.addSubMenu(0, 0, 0, "debug");
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			startActivity("cp://debug");
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		task.getRespInfo().show(this);
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		
	}

	@Override
	public void onTaskTry(SHTask task) {
		
	}

}
