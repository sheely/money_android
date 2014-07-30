package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.damon.ds.widget.DSActionBar;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseTabPagerFragment;

public class CqHomeFragment extends BaseTabPagerFragment implements View.OnClickListener {

	private final String TAB1 = "公司";
	private final String TAB2 = "团队";

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("财圈");
		invalidateActionBar();

		addTab(TAB1, R.layout.common_tab_indicator, CqCompanyListFragment.class, null);
		addTab(TAB2, R.layout.common_tab_indicator, CqTeamListFragment.class, null);
	}

	@Override
	public View onSetView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.tabs_pager_fragment_copy, container, false);
	}

	@Override
	public void initView(View containerView) {
		mTabHost = (TabHost) containerView.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) containerView.findViewById(R.id.pager1);
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
	}

	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		actionBar.addAction(R.drawable.ic_search, "ic_search", this);
	}

	@Override
	public void onClick(View v) {
		if(mTabHost.getCurrentTab() == 0){
			startActivity("cp://querycq");
		}
	}

	@Override
	protected boolean hasActionBar() {
		return true;
	}

}
