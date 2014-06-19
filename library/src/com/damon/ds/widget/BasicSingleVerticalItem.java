package com.damon.ds.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.damon.ds.library.R;

public class BasicSingleVerticalItem extends BasicSingleItem {
	
	public BasicSingleVerticalItem(Context context) {
		super(context, null);
	}

	public BasicSingleVerticalItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void inflateLayout(Context context) {
		inflate(context, R.layout.basic_single_vertical_item, this);
	}

}
