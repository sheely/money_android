package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicSingleVerticalItem;

public class QueryExecuteActivity extends BasePtrListActivity {

	DSObject dsCaixin;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		if (dsCaixin == null) {
			finish();
			return;
		}

		listView.setMode(Mode.DISABLED);
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	public void onCreateActionBar(com.xdamon.app.DSActionBar actionBar) {
		actionBar.addAction(R.drawable.ic_search, "search", new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://querycy"));
				intent.putExtra("forresult", true);
				startActivityForResult(intent, 1);
			}
		});
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (data != null) {
			chooseExecute((DSObject) data.getParcelableExtra("caiyou"));
		}
	};

	SHPostTaskM queryExecuteTask;

	void queryExecute() {
		queryExecuteTask = getTask(DEFAULT_API_URL + "miChooseExecuteGuide.do", this);
		queryExecuteTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryExecuteTask.start();
	}

	SHPostTaskM chooseExecuteTask;

	void chooseExecute(DSObject dsObject) {
		chooseExecuteTask = getTask(DEFAULT_API_URL + "miChooseExecute.do", this);
		chooseExecuteTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		chooseExecuteTask.getTaskArgs().put("userName", dsObject.getString("friendId"));
		chooseExecuteTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		if (task == queryExecuteTask) {
			queryExecuteTask = null;
			DSObject dsObject = DSObjectFactory.create("friendList").fromJson(task.getResult());
			adapter.appendList(dsObject.getArray("friendList", "friend"));
		} else if (task == chooseExecuteTask) {
			dismissProgressDialog();
			chooseExecuteTask = null;
			showShortToast("选定执行人成功");
			finish();
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		if (task == queryExecuteTask) {
			queryExecuteTask = null;
			adapter.appendList(null, task.getRespInfo().getMessage());
		} else if (task == chooseExecuteTask) {
			chooseExecuteTask = null;
			super.onTaskFailed(task);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (Utils.isDSObject(obj, "friend")) {
			chooseExecute((DSObject) obj);
		}
	}

	class Adapter extends BasicDSAdapter {

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(QueryExecuteActivity.this).inflate(
					R.layout.common_basic_single_vertical_item, parent, false);
			}
			BasicSingleVerticalItem view = (BasicSingleVerticalItem) convertView;
			DSObject dsObject = (DSObject) getItem(position);
			view.setTitle(dsObject.getString("friendName"));
			view.setSubTitle(dsObject.getString("companyName"));
			if (dsObject.getInt("isExecuter") == 1) {
				view.setIndicator(R.drawable.icon_selected);
			} else {
				view.setIndicator(0);
			}
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryExecute();
		}

	}

}
