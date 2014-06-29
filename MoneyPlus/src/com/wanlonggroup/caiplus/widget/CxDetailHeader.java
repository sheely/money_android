package com.wanlonggroup.caiplus.widget;

import com.damon.ds.app.DSObject;
import com.damon.ds.widget.BasicItem;
import com.wanlonggroup.caiplus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CxDetailHeader extends LinearLayout {

	DSObject dsCaixin;

	public CxDetailHeader(Context context) {
		super(context);
	}

	public CxDetailHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	BasicItem titleItem, publishItem, cateItem;
	View lookPub, lookAttach, inputSome;

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		titleItem = (BasicItem) findViewById(R.id.title_item);
		publishItem = (BasicItem) findViewById(R.id.publish_item);
		cateItem = (BasicItem) findViewById(R.id.category_item);
		lookPub = findViewById(R.id.look_pub);
		lookAttach = findViewById(R.id.look_attach);
		inputSome = findViewById(R.id.input_some);
	}

	public void setDetail(DSObject dsCaixin) {
		this.dsCaixin = dsCaixin;
		titleItem.setSubTitle(dsCaixin.getString("oppoTitle"));
		publishItem.setSubTitle(dsCaixin.getString("oppoPublisher"));
	}

}
