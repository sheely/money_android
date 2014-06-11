package com.wanlonggroup.caiplus.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;

public class LoginActivity extends BaseActivity {

	private Button loginBtn;
	private EditText userNameText, passwordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		userNameText =(EditText) findViewById(R.id.username);
		passwordText =(EditText) findViewById(R.id.password);
		
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}
		});
	}

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}
	
	void login(){
		String userName = userNameText.getText().toString();
		String password = passwordText.getText().toString();
		if(TextUtils.isEmpty(userName)){
			showAlert("请输入用户名");
			userNameText.requestFocus();
			return;
		}
		if(TextUtils.isEmpty(password)){
			showAlert("请输入密码");
			passwordText.requestFocus();
			return;
		}
		startActivity("cp://home");
		finish();
	}

}
