package com.wanlonggroup.caiplus.app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanlonggroup.caiplus.R;

public class BasePtrListFragment extends BaseFragment implements OnItemClickListener {

	protected PullToRefreshListView listView;
	protected FrameLayout emptyView;
	protected TextView emptyTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = onSetView(inflater, container);
		listView = (PullToRefreshListView) view.findViewById(R.id.list);
		if(listView == null){
			listView = (PullToRefreshListView) view.findViewById(android.R.id.list);
		}
		listView.getRefreshableView().setFastScrollEnabled(true);
		listView.getRefreshableView().setCacheColorHint(Color.argb(0, 0, 0, 0));
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(BasePtrListFragment.this.getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				listView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				onPullToRefresh();
			}
		});
		
		emptyView = (FrameLayout) view.findViewById(R.id.empty);
		emptyTextView = (TextView) view.findViewById(R.id.empty_textview);
		return view;
	}

	protected void setEmpty(String emptyStr) {
		if(emptyView != null){
			listView.setEmptyView(emptyView);
		}
		if(emptyTextView != null){
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
	
	@Override
	public View onSetView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.base_ptr_list_frame, container, false);
	}

	@Override
	public void onDestroyView() {
		emptyView = null;
		emptyTextView = null;
		listView = null;
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

}
