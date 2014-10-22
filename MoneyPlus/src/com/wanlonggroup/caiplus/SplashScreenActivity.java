package com.wanlonggroup.caiplus;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;

import com.wanlonggroup.caiplus.app.BaseActivity;

public class SplashScreenActivity extends BaseActivity {

	private final Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (!isLogined()) {
					startActivity("cp://login");
				} else {
					startActivity("cp://home");
				}
				finish();
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2000);
		findViewById(R.id.screen).startAnimation(animation);

		handler.sendEmptyMessageDelayed(1, 2400);
	}

	public ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return true;
	}

}
