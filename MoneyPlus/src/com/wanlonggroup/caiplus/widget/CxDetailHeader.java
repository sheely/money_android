package com.wanlonggroup.caiplus.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wanlonggroup.caiplus.R;
import com.xdamon.app.DSObject;

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
	View lookPub, lookAttach, lookComment, executeInfo;

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		titleItem = (BasicItem) findViewById(R.id.title_item);
		publishItem = (BasicItem) findViewById(R.id.publish_item);
		cateItem = (BasicItem) findViewById(R.id.category_item);
		lookPub = findViewById(R.id.look_pub);
		lookPub.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cydetail"));
				intent.putExtra("publisherid", dsCaixin.getString("oppoPublisherId"));
				mContext.startActivity(intent);
			}
		});
		
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
		
		executeInfo = findViewById(R.id.execute_info);
		executeInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxexecuteinfo"));
				intent.putExtra("caixin", dsCaixin);
				mContext.startActivity(intent);
			}
		});
		
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
		cateItem.setSubTitle(dsCaixin.getString("oppoType"));
	}

	public void setMode(HeaderMode mode) {
		if (mode == HeaderMode.exec) {
			findViewById(R.id.layer).setVisibility(GONE);
			titleItem.setVisibility(View.GONE);
			cateItem.setVisibility(View.GONE);
			publishItem.setTitle("执行者");
		}
	}

	public enum HeaderMode {
		common, exec
	}

}
