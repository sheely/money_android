package com.xdamon.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.xdamon.library.R;

public class BasicItem extends BasicSingleItem {

	public BasicItem(Context context) {
		super(context);
	}

	public BasicItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void inflateLayout(Context context) {
		inflate(context, R.layout.basic_item, this);
	}

}
