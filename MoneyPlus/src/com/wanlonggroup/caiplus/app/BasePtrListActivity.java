package com.wanlonggroup.caiplus.app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanlonggroup.caiplus.R;

public class BasePtrListActivity extends BaseActivity implements OnItemClickListener {

	protected PullToRefreshListView listView;
	private FrameLayout emptyView;
	private TextView emptyTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onSetContentView();
		listView = (PullToRefreshListView) findViewById(R.id.list);
		if (listView == null) {
			listView = (PullToRefreshListView) findViewById(android.R.id.list);
		}
		listView.getRefreshableView().setFastScrollEnabled(true);
		listView.getRefreshableView().setCacheColorHint(Color.argb(0, 0, 0, 0));
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(BasePtrListActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				listView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				onPullToRefresh();
			}
		});
		
		emptyView = (FrameLayout) findViewById(R.id.empty);
		emptyTextView = (TextView) findViewById(R.id.empty_textview);
	}

	protected void setEmpty(String emptyStr) {
		if (emptyView != null) {
			listView.setEmptyView(emptyView);
		}
		if (emptyTextView != null) {
			emptyTextView.setText(emptyStr);
		}
	}
	
	protected void setEmptyView(View view) {
		if (emptyView != null) {
			listView.setEmptyView(emptyView);
		}
		if(view != null){
			emptyView.removeAllViews();
			emptyView.addView(view);
		}
	}

	protected void onPullToRefresh() {

	}

	protected void onSetContentView() {
		setContentView(R.layout.base_ptr_list_frame);
	}

	@Override
	public void onDestroy() {
		emptyView = null;
		emptyTextView = null;
		listView = null;
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

}
