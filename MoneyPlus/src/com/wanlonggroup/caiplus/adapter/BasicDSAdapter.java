package com.wanlonggroup.caiplus.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdamon.adapter.BasicAdapter;
import com.xdamon.app.DSObject;
import com.xdamon.widget.LoadingErrorView;

public abstract class BasicDSAdapter extends BasicAdapter {
	protected ArrayList<DSObject> dsList;
	protected boolean isEnd;
	protected String errorMsg;
	protected int nextStartIndex;
	protected int recordCount;
	protected int startIndex;

	public BasicDSAdapter() {
		this(null);
	}
	
	public BasicDSAdapter(DSObject[] arr) {
		dsList = new ArrayList<DSObject>();
		if(arr != null){
			dsList.addAll(Arrays.asList(arr));
		}
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

	public abstract View getCPItemView(int position, View convertView, ViewGroup parent);

	public abstract void loadNextData(int startIndex);

	public void remove(DSObject obj){
		dsList.remove(obj);
		notifyDataSetChanged();
	}
	
	public void append(DSObject obj){
		dsList.add(obj);
		notifyDataSetChanged();
	}
	
	public void appendList(DSObject[] arr) {
		appendList(arr, true, true, null);
	}

	public void appendList(DSObject[] arr, String errorMsg) {
		appendList(arr, TextUtils.isEmpty(errorMsg), true, errorMsg);
	}

	public void appendList(DSObject[] arr, boolean isEnd) {
		appendList(arr, isEnd, true, null);
	}
	
	public void appendList(DSObject[] arr, boolean isEnd, boolean clear, String errorMsg) {
		this.isEnd = isEnd;
		this.errorMsg = errorMsg;
		if (arr != null) {
			if (clear) {
				this.dsList.clear();
			}
			this.startIndex += this.dsList.size();
			this.dsList.addAll(Arrays.asList(arr));
			this.nextStartIndex += this.dsList.size();
		}
		notifyDataSetChanged();
	}

	public static class BasicViewHolder {
		public TextView textView1;
		public TextView textView2;
		public TextView textView3;
		public TextView textView4;
		public TextView textView5;
		public ImageView icon1;
		public ImageView icon2;
		public Button button1;
		public Button button2;
	}
}
