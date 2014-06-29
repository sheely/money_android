package com.wanlonggroup.caiplus.cx;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.damon.ds.widget.BasicSingleVerticalItem;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.widget.CxDetailHeader;

public class CxReviewDetailActivity extends BasePtrListActivity implements ITaskListener, OnClickListener {

	DSObject dsCaixin;
	DSObject dsLeavemessages;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_review_detail);
		dsCaixin = getIntent().getParcelableExtra("caixin");

		setupView();
	}

	CxDetailHeader cxDetailHeader;
	Button pubButton;

	void setupView() {
		cxDetailHeader = (CxDetailHeader) findViewById(R.id.detail_header);
		pubButton = (Button) findViewById(R.id.pub_btn);
		pubButton.setOnClickListener(this);
		
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	SHPostTaskM queryTask;

	void queryCaixin() {
		queryTask = getTask(DEFAULT_API_URL + "queryLmList.do", this);
		queryTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryTask.start();
		showProgressDialog();
	}

	@Override
	public void onClick(View v) {
		if (v == pubButton) {

		}
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsLeavemessages = DSObjectFactory.create(CPModeName.CAIXIN_LEAVE_MESSAGE_LIST).fromJson(task.getResult());
		adapter.appendList(dsLeavemessages.getArray(CPModeName.CAIXIN_LEAVE_MESSAGE_LIST,
			CPModeName.CAIXIN_LEAVE_MESSAGE_ITEM));
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		adapter.appendList(null, task.getRespInfo().getMessage());
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {

	}

	@Override
	public void onTaskTry(SHTask task) {

	}

	class Adapter extends BasicDSAdapter {
		
		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			DSObject message = (DSObject) getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_message_item, parent,
					false);
			}
			BasicSingleVerticalItem messageItem = (BasicSingleVerticalItem) convertView;
			if (position % 2 == 0) {
				messageItem.getTitleView().setGravity(Gravity.LEFT);
				messageItem.getSubTitleView().setGravity(Gravity.LEFT);
				messageItem.getRightImageView().setVisibility(View.GONE);
				messageItem.getLeftImageView().setVisibility(View.VISIBLE);
				imageLoader.displayImage(message.getString("lmHeadIcon"), messageItem.getLeftImageView(),
					displayOptions);
			} else {
				messageItem.getTitleView().setGravity(Gravity.RIGHT);
				messageItem.getSubTitleView().setGravity(Gravity.RIGHT);
				messageItem.getRightImageView().setVisibility(View.VISIBLE);
				messageItem.getLeftImageView().setVisibility(View.GONE);
				imageLoader.displayImage(message.getString("lmHeadIcon"), messageItem.getRightImageView(),
					displayOptions);
			}
			messageItem.setTitle(message.getString("leaveMessager"));
			messageItem.setSubTitle(message.getString("lmContent"));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryCaixin();
		}

	}

}
