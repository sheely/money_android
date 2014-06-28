package com.wanlonggroup.caiplus.app;

import com.damon.ds.app.DSApplication;
import com.next.util.SHEnvironment;
import com.wanlonggroup.caiplus.model.AccountService;

public class CaiPlusApplication extends DSApplication {
	
	private AccountService accountService;
	
	public AccountService acccountServie(){
		if(accountService == null){
			accountService = new AccountService(getApplicationContext());
			SHEnvironment.getInstance().setSession(accountService.sessionId());
		}
		return accountService;
	}

}
