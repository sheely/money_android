package com.wanlonggroup.caiplus.app;

import java.util.HashMap;

import android.os.Bundle;
import android.text.TextUtils;

import com.damon.ds.app.DSFragment;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.util.SHEnvironment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanlonggroup.caiplus.model.AccountService;
import com.wanlonggroup.caiplus.model.AccountService.AccountListener;
import com.wanlonggroup.caiplus.util.ConfigSwitch;

public class BaseFragment extends DSFragment implements AccountListener {
	
	public static final String DEFAULT_API_URL = "http://cjcapp.nat123.net:21414/myStruts1/";
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected static DisplayImageOptions displayOptions = DisplayImageOptions.createSimple();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountService().addListener(this);
	}

	protected AccountService accountService() {
		return ((CaiPlusApplication) getActivity().getApplication()).acccountServie();
	}

	protected boolean isLogined() {
		return !TextUtils.isEmpty(accountService().id());
	}

	@Override
	public final void onAccountChanged(AccountService sender) {
		if (isLogined()) {
			SHEnvironment.getInstance().setSession(accountService().sessionId());
		} else {
			SHEnvironment.getInstance().setSession("");
		}

		onAccountChanged();
	}

	protected void onAccountChanged() {

	}

	private HashMap<String, SHPostTaskM> taskMap = new HashMap<String, SHPostTaskM>();

	public SHPostTaskM getTask(String url, ITaskListener listener) {
		SHPostTaskM oldTaks = findTask(url);
		if (oldTaks != null) {
			oldTaks.cancel(true);
		}
		SHPostTaskM task = new SHPostTaskM();
		task.setUrl(ConfigSwitch.instance().wrapDomain(url));
		task.setListener(listener);
		taskMap.put(url, task);
		return task;
	}

	public SHPostTaskM findTask(String url) {
		for (String taskUrl : taskMap.keySet()) {
			if (taskUrl.equals(url)) {
				return taskMap.get(taskUrl);
			}
		}
		return null;
	}

	@Override
	public void onDestroy() {
		accountService().removeListener(this);
		for (SHPostTaskM task : taskMap.values()) {
			task.cancel(true);
		}
		super.onDestroy();
	}

	public void finish() {
		if (canBack()) {
			if (!getFragmentManager().popBackStackImmediate()) {
				getActivity().finish();
			}
		}
	}

}
