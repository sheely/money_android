package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.damon.ds.widget.BasicSingleVerticalItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;

public class AttachmentListActivity extends BasePtrListActivity {

	String oppoId;
	String attachType;
	DSObject dsAttachments;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oppoId = getStringParam("oppoid");
		attachType = getStringParam("attachtype");

		listView.setMode(Mode.DISABLED);
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	SHPostTaskM queryTask;

	void queryAttachments() {
		queryTask = getTask(DEFAULT_API_URL + "queryAttachment.do", this);
		queryTask.getTaskArgs().put("oppoId", oppoId);
		queryTask.getTaskArgs().put("attachmentType", attachType);
		queryTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsAttachments = DSObjectFactory.create(CPModeName.CAIXIN_LIST).fromJson(task.getResult());
		adapter.appendList(dsAttachments.getArray(CPModeName.CAIXIN_LIST, CPModeName.CAIXIN_ATTACH_ITEM));
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		adapter.appendList(null, task.getRespInfo().getMessage());
	}

	class Adapter extends BasicDSAdapter {

		public String getAttachName(int position) {
			DSObject attach = (DSObject) getItem(position);
			String url = attach.getString("attachmentUrl");
			if (!TextUtils.isEmpty(url)) {
				int index = url.lastIndexOf("/");
				if (index > 0) {
					return url.substring(index + 1);
				}
			}
			return url;
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_basic_single_item,
					parent, false);
			}
			BasicSingleVerticalItem item = (BasicSingleVerticalItem) convertView;
			item.setTitle(getAttachName(position));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryAttachments();
		}

	}

}
