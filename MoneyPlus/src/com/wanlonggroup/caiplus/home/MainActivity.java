package com.wanlonggroup.caiplus.home;

import android.os.Bundle;
import android.view.KeyEvent;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseFragmentTabActivity;

public class MainActivity extends BaseFragmentTabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	protected void onSetContent() {
		setContentView(R.layout.fragment_tabs_bottom);
	}

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

	
	private long lastQuitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long time = System.currentTimeMillis();
			if(lastQuitTime == 0){
				showShortToast("再按一次退出程序");
				lastQuitTime = time;
				return false;
			}
			if(time - lastQuitTime < 2 * 1000){
				lastQuitTime = time;
				finish();
			}else{
				showShortToast("再按一次退出程序");
				lastQuitTime = time;
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
