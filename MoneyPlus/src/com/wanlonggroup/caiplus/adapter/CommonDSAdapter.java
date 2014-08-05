package com.wanlonggroup.caiplus.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.view.View;
import android.view.ViewGroup;

import com.xdamon.adapter.BasicAdapter;
import com.xdamon.app.DSObject;

public class CommonDSAdapter extends BasicAdapter {
	protected ArrayList<DSObject> dsList;

	public CommonDSAdapter() {
		dsList = new ArrayList<DSObject>();
	}

	@Override
	public int getCount() {
		return dsList.size();
	}

	public void appendList(DSObject[] arr) {
		if (arr != null) {
			this.dsList.addAll(Arrays.asList(arr));
			notifyDataSetChanged();
		}
	}

	@Override
	public DSObject getItem(int position) {
		return dsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
