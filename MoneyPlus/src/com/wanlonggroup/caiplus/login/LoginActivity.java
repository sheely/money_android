package com.wanlonggroup.caiplus.login;

import android.os.Bundle;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

}
