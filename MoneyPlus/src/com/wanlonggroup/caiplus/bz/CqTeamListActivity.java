package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;

public class CqTeamListActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container_activity);
		Fragment fragment = Fragment.instantiate(this, CqTeamListFragment.class.getName(), getIntent().getExtras());

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.root_view, fragment, "cq_team_query_list");
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();
	}

}
