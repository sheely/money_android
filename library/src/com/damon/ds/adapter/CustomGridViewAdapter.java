package com.damon.ds.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.damon.ds.widget.CustomGridView;

public abstract class CustomGridViewAdapter extends BasicAdapter {

	public abstract int getColumnCount();

	public abstract View getItemView(int position, View convertView, ViewGroup parent);

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		View view;
		View itemView;
		if (position % getColumnCount() == 0) {
			view = new TableRow(parent.getContext());
			itemView = getItemView(position, convertView, (TableRow) view);
			((TableRow) view).addView(itemView);
		} else {
			view = getItemView(position, convertView, ((CustomGridView) parent).getCurRow());
			itemView = view;
		}
		if ((position == getCount() - 1) && stretchLastItemView()) {
			TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) itemView.getLayoutParams();
			layoutParams.span = (getColumnCount() - getCount() % getColumnCount()) % getColumnCount() + 1;
			itemView.setLayoutParams(layoutParams);
		}
		return view;
	}
	
	/**
	 * stretch the last item view or not 
	 * @return
	 */
	public boolean stretchLastItemView(){
		return false;
	}

}
