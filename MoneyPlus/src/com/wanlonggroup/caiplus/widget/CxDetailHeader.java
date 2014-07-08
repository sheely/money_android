package com.wanlonggroup.caiplus.widget;

import com.damon.ds.app.DSObject;
import com.damon.ds.widget.BasicItem;
import com.wanlonggroup.caiplus.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CxDetailHeader extends LinearLayout {

	Context mContext;

	DSObject dsCaixin;

	public CxDetailHeader(Context context) {
		this(context, null);
	}

	public CxDetailHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	BasicItem titleItem, publishItem, cateItem;
	View lookPub, lookAttach, lookComment, inputSome;

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		titleItem = (BasicItem) findViewById(R.id.title_item);
		publishItem = (BasicItem) findViewById(R.id.publish_item);
		cateItem = (BasicItem) findViewById(R.id.category_item);
		lookPub = findViewById(R.id.look_pub);
		lookAttach = findViewById(R.id.look_attach);
		lookAttach.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxattachlist"));
				intent.putExtra("oppoid", dsCaixin.getString("oppoId"));
				intent.putExtra("attachtype", "2");
				mContext.startActivity(intent);
			}
		});
		
		inputSome = findViewById(R.id.input_some);
		
		lookComment = findViewById(R.id.look_comment);
		lookComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxcommentlist"));
				intent.putExtra("caixin", dsCaixin);
				mContext.startActivity(intent);
			}
		});
	}

	public void setDetail(DSObject dsCaixin) {
		this.dsCaixin = dsCaixin;
		titleItem.setSubTitle(dsCaixin.getString("oppoTitle"));
		publishItem.setSubTitle(dsCaixin.getString("oppoPublisher"));
	}

	public void setMode(HeaderMode mode) {
		if (mode == HeaderMode.exec) {
			lookPub.setVisibility(GONE);
			findViewById(R.id.layer).setVisibility(GONE);
		}
	}

	public enum HeaderMode {
		common, exec
	}

}
