package com.wanlonggroup.caiplus.bz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.next.config.SHConfigManager;
import com.next.config.SHConfigState;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseFragmentTabActivity;
import com.wanlonggroup.caiplus.bz.im.IMessage;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.xdamon.widget.BadgeView;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseFragmentTabActivity {

    private final String CAIXIN = "财信";
    private final String CAIYOU = "财友";
    private final String CAIQUAN = "财圈";
    private final String ME = "我";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SHConfigManager.CORE_NOTIFICATION_CONFIG_STATUS_CHANGED.equals(intent.getAction())
                    && SHConfigManager.getInstance().getState() == SHConfigState.Done) {
                SHConfigManager.getInstance().show(MainActivity.this);
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTabWidgetBackground(R.color.tab_widget_color);

        addTab(CAIXIN, R.drawable.ic_tab_cx, 0, CxHomeFragment.class, null);
        addTab(CAIYOU, R.drawable.ic_tab_cy, 0, CyHomeFragment.class, null);
        addTab(CAIQUAN, R.drawable.ic_tab_cq, 0, CqHomeFragment.class, null);
        addTab(ME, R.drawable.ic_tab_me, 0, UserFragment.class, null);

        registerReceiver(receiver, new IntentFilter(
                SHConfigManager.CORE_NOTIFICATION_CONFIG_STATUS_CHANGED));
        SHConfigManager.getInstance().setURL(
            ConfigSwitch.instance().wrapDomain(DEFAULT_API_URL + "getConfig"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (messageView == null) {
            messageView = new BadgeView(this, mTabHost.getTabWidget(), 1);
            messageView.setText(" ");
        }
        try {
            EventBus.getDefault().registerSticky(this);
        } catch (Exception e) {
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.fragment_tabs_bottom);
    }

    protected ActionBarType actionBarType() {
        return ActionBarType.NONE;
    }

    BadgeView messageView;

    void toggleBadgeView(boolean isShow) {
        if (messageView == null) {
            return;
        }
        if (isShow) {
            messageView.show();
        } else {
            messageView.hide();
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        if (CAIYOU.equals(tabId)) {
            toggleBadgeView(false);
        }
    }

    public void onEventMainThread(IMessage message) {
        toggleBadgeView(!CAIYOU.equals(mTabHost.getCurrentTabTag()));
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
