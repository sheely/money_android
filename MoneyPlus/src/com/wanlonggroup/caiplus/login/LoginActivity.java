package com.wanlonggroup.caiplus.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;

public class LoginActivity extends BaseActivity {

	private Button loginBtn;
	private EditText userNameText, passwordText;
	private ImageView deleteUsername, deletePwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userNameText = (EditText) findViewById(R.id.username);
		userNameText.addTextChangedListener(textWatcher);
		userNameText.setOnFocusChangeListener(focusChangeListener);
		deleteUsername = (ImageView) findViewById(R.id.username_delete);
		deleteUsername.setOnClickListener(onClickListener);

		passwordText = (EditText) findViewById(R.id.password);
		passwordText.addTextChangedListener(textWatcher);
		passwordText.setOnFocusChangeListener(focusChangeListener);
		deletePwd = (ImageView) findViewById(R.id.pwd_delete);
		deletePwd.setOnClickListener(onClickListener);

		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(onClickListener);

	}

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

	SHPostTaskM loginTask;

	void login() {
		String userName = userNameText.getText().toString();
		String password = passwordText.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			showAlert("请输入用户名");
			userNameText.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showAlert("请输入密码");
			passwordText.requestFocus();
			return;
		}
		SHEnvironment.getInstance().setLoginId(userNameText.getText().toString());
		SHEnvironment.getInstance().setPassword(passwordText.getText().toString());

		loginTask = getTask(DEFAULT_API_URL + "milogin.do", this);
		loginTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		DSObject dsUser = DSObjectFactory.create(CPModeName.USER, task.getResult());
		accountService().update(dsUser);
		startActivity("cp://home");
		finish();
	}


	final OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (deleteUsername == v) {
				userNameText.setText(null);
				userNameText.requestFocus();
			} else if (deletePwd == v) {
				passwordText.setText(null);
				passwordText.requestFocus();
			} else if (loginBtn == v) {
				login();
			}
		}
	};

	final TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			deleteUsername.setVisibility((userNameText.hasFocus() && !TextUtils.isEmpty(userNameText.getText())) ? View.VISIBLE
					: View.INVISIBLE);
			deletePwd.setVisibility((passwordText.hasFocus() && !TextUtils.isEmpty(passwordText.getText())) ? View.VISIBLE
					: View.INVISIBLE);
		}
	};

	final OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			deleteUsername.setVisibility((userNameText.hasFocus() && !TextUtils.isEmpty(userNameText.getText())) ? View.VISIBLE
					: View.INVISIBLE);
			deletePwd.setVisibility((passwordText.hasFocus() && !TextUtils.isEmpty(passwordText.getText())) ? View.VISIBLE
					: View.INVISIBLE);

		}
	};
}
