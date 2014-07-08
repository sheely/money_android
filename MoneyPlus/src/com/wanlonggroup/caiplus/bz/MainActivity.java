package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseFragmentTabActivity;

public class MainActivity extends BaseFragmentTabActivity {

	private final String CAIXIN = "财信";
	private final String CAIYOU = "财友";
	private final String CAIQUAN = "财圈";
	private final String ME = "我";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTabWidgetBackground(R.color.tab_widget_color);

		addTab(CAIXIN, R.drawable.ic_tab_cx, 0, CxHomeFragment.class, null);
		addTab(CAIYOU, R.drawable.ic_tab_cy, 0, CyHomeFragment.class, null);
		addTab(CAIQUAN, R.drawable.ic_tab_cq, 0, CqHomeFragment.class, null);
		addTab(ME, R.drawable.ic_tab_me, 0, UserFragment.class, null);
	}

	protected void onSetContent() {
		setContentView(R.layout.fragment_tabs_bottom);
	}

	protected ActionBarType actionBarType() {
		return ActionBarType.NONE;
	}

	private long lastQuitTime;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		lastQuitTime = 0;
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long time = System.currentTimeMillis();
			if (time - lastQuitTime < 2 * 1000) {
				finish();
			} else {
				showShortToast("再按一次退出程序");
				lastQuitTime = time;
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
