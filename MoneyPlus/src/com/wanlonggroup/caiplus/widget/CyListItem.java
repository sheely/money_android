package com.wanlonggroup.caiplus.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.wanlonggroup.caiplus.R;
import com.xdamon.widget.BasicSingleItem;

public class CyListItem extends BasicSingleItem {
	
	public CyListItem(Context context){
		super(context);
	}

	public CyListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void inflateLayout(Context context) {
		inflate(context, R.layout.cy_list_item, this);
	}

}
