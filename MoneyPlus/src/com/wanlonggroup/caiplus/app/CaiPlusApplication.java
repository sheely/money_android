package com.wanlonggroup.caiplus.app;

import com.next.util.SHEnvironment;
import com.wanlonggroup.caiplus.model.AccountService;
import com.xdamon.app.DSApplication;
import com.xdamon.util.PreferencesUtils;

public class CaiPlusApplication extends DSApplication {
	
	private AccountService accountService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		acccountServie();
		PreferencesUtils.initSharedPreferenceName(getPackageName());
	}
	
	public AccountService acccountServie(){
		if(accountService == null){
			accountService = new AccountService(getApplicationContext());
			SHEnvironment.getInstance().setLoginId(accountService.name());
			SHEnvironment.getInstance().setPassword(accountService.password());
		}
		return accountService;
	}

}
