package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.DSActionBar;

public class MyCalenderActivity extends BasePtrListActivity implements OnClickListener {

	public static final int REQUEST_MODIFY_CALENDER = 0xff09;

	CalenderAdapter adapter;
	int isOwnTask; // 0其他 1自己
	String queryedUsername;
	DSObject dsTasks;
	DSObject dsSelectedTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isOwnTask = getIntParam("isowntask");
		queryedUsername = getStringParam("queryedusername");

		adapter = new CalenderAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		super.onCreateActionBar(actionBar);
		if (isOwnTask != 1) {
			setTitle(queryedUsername + "的日历");
		}else{
			actionBar.addAction("新增", "add_calender", this);
		}
	}

	@Override
	public void onClick(View v) {
		if ("add_calender".equals(v.getTag())) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://modifycalender"));
			startActivityForResult(intent, REQUEST_MODIFY_CALENDER);
		}
	}

	protected void onPullToRefresh() {
		queryList();
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_MODIFY_CALENDER) {
			adapter.reset();
		}
	};

	SHPostTaskM queryTask;

	void queryList() {
		queryTask = getTask(DEFAULT_API_URL + "miQueryTasks.do", this);
		queryTask.getTaskArgs().put("isOwnTask", isOwnTask);
		queryTask.getTaskArgs().put("queryedUserName", queryedUsername);
		queryTask.start();
	}

	SHPostTaskM deleteTask;

	void deleteTask(DSObject task) {
		deleteTask = getTask(DEFAULT_API_URL + "miTaskMaintainance.do", this);
		deleteTask.getTaskArgs().put("taskId", task.getString("taskId"));
		deleteTask.getTaskArgs().put("operationType", 3);
		deleteTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		if (queryTask == task) {
			dsTasks = DSObjectFactory.create(CPModeName.MY_CALENDER_LIST, task.getResult());
			adapter.appendList(dsTasks.getArray(CPModeName.MY_CALENDER_LIST, CPModeName.MY_CALENDER_ITEM));
			listView.onRefreshComplete();
		} else if (deleteTask == task) {
			adapter.remove(dsSelectedTask);
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		if (queryTask == task) {
			adapter.appendList(null, task.getRespInfo().getMessage());
			listView.onRefreshComplete();
		} else if (deleteTask == task) {
			task.getRespInfo().show(this);
		}
	}

	class CalenderAdapter extends BasicDSAdapter {

		String getStatusText(int status) {
			String statusText = "";
			switch (status) {
			case 0:
				statusText = "未开始";
				break;
			case 1:
				statusText = "完成";
				break;
			case 2:
				statusText = "取消";
				break;
			case 3:
				statusText = "进行中";
				break;
			}
			return statusText;
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			BasicViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(MyCalenderActivity.this).inflate(R.layout.my_calender_item, parent,
					false);
				viewHolder = new BasicViewHolder();
				convertView.setTag(viewHolder);
				viewHolder.textView1 = (TextView) convertView.findViewById(R.id.time);
				viewHolder.textView2 = (TextView) convertView.findViewById(R.id.status);
				viewHolder.textView3 = (TextView) convertView.findViewById(R.id.content);
				viewHolder.button1 = (Button) convertView.findViewById(R.id.edit);
				viewHolder.button2 = (Button) convertView.findViewById(R.id.cancel);
			} else {
				viewHolder = (BasicViewHolder) convertView.getTag();
			}
			final DSObject dsTask = (DSObject) getItem(position);
			viewHolder.textView1.setText(dsTask.getString("startTime") + "至" + dsTask.getString("endTime"));
			viewHolder.textView2.setText(getStatusText(dsTask.getInt("taskStatus")));
			viewHolder.textView3.setText(dsTask.getString("taskContent"));
			viewHolder.button1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dsSelectedTask = dsTask;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://modifycalender"));
					intent.putExtra("task", dsSelectedTask);
					startActivityForResult(intent, REQUEST_MODIFY_CALENDER);
				}
			});
			viewHolder.button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dsSelectedTask = dsTask;
					deleteTask(dsSelectedTask);
				}
			});
			if(isOwnTask != 1){
				convertView.findViewById(R.id.layer).setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryList();
		}

	}
}
