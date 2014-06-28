package com.wanlonggroup.caiplus.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.damon.ds.adapter.BasicAdapter;
import com.damon.ds.app.DSObject;
import com.damon.ds.widget.LoadingErrorView;
import com.wanlonggroup.caiplus.model.CPObject;

public abstract class BasicCPAdapter extends BasicAdapter {
	protected ArrayList<CPObject> dsList;
	protected boolean isEnd;
	protected String errorMsg;
	protected int nextStartIndex;
	protected int recordCount;
	protected int startIndex;

	public BasicCPAdapter() {
		dsList = new ArrayList<CPObject>();
	}

	@Override
	public int getCount() {
		if (!isEnd || !TextUtils.isEmpty(errorMsg)) {
			return dsList.size() + 1;
		}
		if (isEnd && dsList.size() == 0) {
			return 1;
		}
		return dsList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < dsList.size()) {
			return dsList.get(position);
		}
		if (isEnd && dsList.size() == 0) {
			return EMPTY;
		}
		return !TextUtils.isEmpty(errorMsg) ? ERROR : LOADING;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void reset() {
		dsList.clear();
		isEnd = false;
		errorMsg = null;
		startIndex = 0;
		nextStartIndex = 0;
		recordCount = 0;
		notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		Object object = getItem(position);
		if (object == ERROR) {
			return 0;
		}
		if (object == LOADING) {
			return 1;
		}
		if (object == EMPTY) {
			return 2;
		}
		return 3;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		Object object = getItem(position);
		if (object instanceof DSObject) {
			return getCPItemView(position, convertView, parent);
		} else if (object == ERROR) {
			return getFailedView(errorMsg, new LoadingErrorView.LoadRetry() {

				@Override
				public void loadRetry(View v) {
					errorMsg = null;
					notifyDataSetChanged();
				}
			}, parent, convertView);
		} else if (object == LOADING) {
			loadNextData(nextStartIndex);
			return getLoadingView(parent, convertView);
		} else if (object == EMPTY) {
			return getEmptyView(emptyMessage(), parent, convertView);
		}
		return null;
	}

	protected String emptyMessage() {
		return "暂无相关数据";
	}

	public abstract View getCPItemView(int position, View convertView,
			ViewGroup parent);

	public abstract void loadNextData(int startIndex);

	public void appendList(CPObject[] arr) {
		appendList(arr, true, null);
	}

	public void appendList(CPObject[] arr, String errorMsg) {
		appendList(arr, true, errorMsg);
	}
	
	public void appendList(CPObject[] arr, boolean isEnd){
		appendList(arr, isEnd, null);
	}

	public void appendList(CPObject[] arr, boolean isEnd, String errorMsg) {
		this.isEnd = isEnd;
		this.errorMsg = errorMsg;
		this.startIndex += this.dsList.size();
		if (arr != null) {
			this.dsList.addAll(Arrays.asList(arr));
			this.nextStartIndex += this.dsList.size();
		}
		notifyDataSetChanged();
	}
}
