package com.wanlonggroup.caiplus;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;

import com.wanlonggroup.caiplus.app.BaseActivity;

public class SplashScreenActivity extends BaseActivity {

	private final Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				startActivity("cp://login");
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

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

}
