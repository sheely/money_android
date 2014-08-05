package com.xdamon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MeasuredListView extends ListView {

	public MeasuredListView(Context context) {
		super(context);
	}
	
	public MeasuredListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MeasuredListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
