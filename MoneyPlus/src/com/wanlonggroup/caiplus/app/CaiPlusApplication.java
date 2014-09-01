package com.wanlonggroup.caiplus.app;

import com.next.util.SHEnvironment;
import com.wanlonggroup.caiplus.bz.im.ChatMessageService;
import com.wanlonggroup.caiplus.model.AccountService;
import com.wanlonggroup.caiplus.util.Environment;
import com.xdamon.app.DSApplication;
import com.xdamon.util.PreferencesUtils;
import com.xdamon.util.SafeLooper;

public class CaiPlusApplication extends DSApplication {
	
	private AccountService accountService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		acccountServie();
		PreferencesUtils.initSharedPreferenceName(getPackageName());
		
		if (!Environment.isDebug()) {
		    SafeLooper.install();
		}
	}
	
	public AccountService acccountServie(){
		if(accountService == null){
			accountService = new AccountService(getApplicationContext());
			SHEnvironment.getInstance().setLoginId(accountService.id());
			SHEnvironment.getInstance().setPassword(accountService.password());
		}
		return accountService;
	}
	
	@Override
	public void onApplicationResume() {
		super.onApplicationResume();
		ChatMessageService.start(this);
	}
	
	@Override
	public void onApplicationStop() {
		super.onApplicationStop();
//		ChatMessageService.stop(this);
	}

}
