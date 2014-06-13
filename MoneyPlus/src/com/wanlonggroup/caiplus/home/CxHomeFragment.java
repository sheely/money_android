package com.wanlonggroup.caiplus.home;

import android.os.Bundle;
import android.view.View;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseTabPagerFragment;
import com.wanlonggroup.caiplus.cx.AllCxListFragment;
import com.wanlonggroup.caiplus.cx.BidCxListFragment;
import com.wanlonggroup.caiplus.cx.JoinCxListFragment;
import com.wanlonggroup.caiplus.cx.PublishCxListFragment;

public class CxHomeFragment extends BaseTabPagerFragment {
	
	private final String TAB1 = "全部";
	private final String TAB2 = "已参与";
	private final String TAB3 = "已中标";
	private final String TAB4 = "已发布";
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("财信列表");
		
		addTab(TAB1, R.layout.common_tab_indicator, AllCxListFragment.class, null);
		addTab(TAB2, R.layout.common_tab_indicator, JoinCxListFragment.class, null);
		addTab(TAB3, R.layout.common_tab_indicator, BidCxListFragment.class, null);
		addTab(TAB4, R.layout.common_tab_indicator, PublishCxListFragment.class, null);
	}
	
	@Override
	protected boolean hasActionBar() {
		return true;
	}

}
