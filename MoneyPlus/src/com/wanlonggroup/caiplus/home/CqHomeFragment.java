package com.wanlonggroup.caiplus.home;

import android.os.Bundle;
import android.view.View;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseTabPagerFragment;
import com.wanlonggroup.caiplus.cq.CompanyCqListFragment;
import com.wanlonggroup.caiplus.cq.TeamCqListFragment;

public class CqHomeFragment extends BaseTabPagerFragment {
	
	private final String TAB1 = "公司";
	private final String TAB2 = "团队";
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("财圈");
		
		addTab(TAB1, R.layout.common_tab_indicator, CompanyCqListFragment.class, null);
		addTab(TAB2, R.layout.common_tab_indicator, TeamCqListFragment.class, null);
	}
	
	@Override
	protected boolean hasActionBar() {
		return true;
	}

}
