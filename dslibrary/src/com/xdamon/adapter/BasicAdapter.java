package com.xdamon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xdamon.library.R;
import com.xdamon.widget.LoadingErrorView;

public abstract class BasicAdapter extends BaseAdapter {

	public static final Object LOADING = new Object();
	public static final Object ERROR = new Object();
	public static final Object HEAD = new Object();
	public static final Object EMPTY = new Object();
	public static final Object FOOT = new Object();

	protected View getLoadingView(ViewGroup parent, View convertView) {
		View v = convertView == null ? null
				: convertView.getTag() == LOADING ? convertView : null;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			v = inflater.inflate(R.layout.loading_item, parent, false);
			v.setTag(LOADING);
		}
		return v;
	}

	protected View getFailedView(String msg, LoadingErrorView.LoadRetry retry,
			ViewGroup parent, View convertView) {
		View v = convertView == null ? null
				: convertView.getTag() == ERROR ? convertView : null;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			v = inflater.inflate(R.layout.error_item, parent, false);
			v.setTag(ERROR);
		}
		((TextView) v.findViewById(android.R.id.text1)).setText(msg);
		if (!(v instanceof LoadingErrorView))
			return null;
		((LoadingErrorView) v).setCallBack(retry);
		return v;
	}

	protected View getEmptyView(String msg, ViewGroup parent, View convertView) {
		View v = convertView == null ? null
				: convertView.getTag() == EMPTY ? convertView : null;
		if(v == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			v = inflater.inflate(R.layout.empty_item, parent, false);
			v.setTag(EMPTY);
		}
		((TextView) v.findViewById(android.R.id.text1)).setText(msg);
		return v;
	}

}
