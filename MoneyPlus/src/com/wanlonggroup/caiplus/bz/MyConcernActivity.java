package com.wanlonggroup.caiplus.bz;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.bz.im.ChatHelper;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class MyConcernActivity extends BasePtrListActivity {

	FollowerAdapter adapter;

	DSObject dsFollowers;
	DSObject dsSelectedFollower;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new FollowerAdapter();
		listView.setAdapter(adapter);
	};

	protected void onPullToRefresh() {
		queryList();
	};

	SHPostTaskM queryTask;

	void queryList() {
		queryTask = getTask(DEFAULT_API_URL + "miQueryFollowers.do", this);
		queryTask.start();
	}

	SHPostTaskM cancelConcernTask;

	void cancelConcern(DSObject follower) {
		cancelConcernTask = getTask(DEFAULT_API_URL + "miFollowerAdd.do", this);
		cancelConcernTask.getTaskArgs().put("myUserName", accountService().name());
		cancelConcernTask.getTaskArgs().put("followerUserName", follower.getString("followerName"));
		cancelConcernTask.getTaskArgs().put("addOrDelete", 0);
		cancelConcernTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if (queryTask == task) {
			dsFollowers = DSObjectFactory.create(CPModeName.MY_CONCERN_LIST, task.getResult());
			adapter.appendList(dsFollowers.getArray(CPModeName.MY_CONCERN_LIST, CPModeName.MY_CONCERN_ITEM));
			listView.onRefreshComplete();
		} else if (cancelConcernTask == task) {
			adapter.remove(dsSelectedFollower);
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		if (queryTask == task) {
			adapter.appendList(null, task.getRespInfo().getMessage());
			listView.onRefreshComplete();
		} else if (cancelConcernTask == task) {
			super.onTaskFailed(task);
		}
	}

	class FollowerAdapter extends BasicDSAdapter {

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			BasicViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(MyConcernActivity.this).inflate(R.layout.my_concern_item, parent,
					false);
				viewHolder = new BasicViewHolder();
				convertView.setTag(viewHolder);
				viewHolder.icon1 = (ImageView) convertView.findViewById(R.id.icon);
				viewHolder.textView1 = (TextView) convertView.findViewById(R.id.username);
				viewHolder.textView2 = (TextView) convertView.findViewById(R.id.cancel_concern);
				viewHolder.textView3 = (TextView) convertView.findViewById(R.id.calendar);
				viewHolder.textView4 = (TextView) convertView.findViewById(R.id.send_cx);
			} else {
				viewHolder = (BasicViewHolder) convertView.getTag();
			}
			final DSObject dsFollower = (DSObject) getItem(position);
			imageLoader.displayImage(dsFollower.getString("followerHeadIcon"), viewHolder.icon1, displayOptions);
			viewHolder.textView1.setText(dsFollower.getString("followerName"));
			viewHolder.textView2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dsSelectedFollower = dsFollower;
					showAlert("提示", "确认取消关注", true, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancelConcern(dsFollower);
						}
					}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
				}
			});
			viewHolder.textView3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://mycalender"));
					intent.putExtra("isowntask", 0);
					intent.putExtra("queryedusername", dsFollower.getString("followerName"));
					startActivity(intent);
				}
			});
			viewHolder.textView4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DSObject dsCy = new DSObject(CPModeName.CAIYOU_ITEM);
					dsCy.put("friendId", dsFollower.getString("followerId"));
					dsCy.put("friendName", dsFollower.getString("followerName"));
					dsCy.put("friendHeadIcon", dsFollower.getString("followerHeadIcon"));
					ChatHelper.instance(MyConcernActivity.this).chat2Who(MyConcernActivity.this, dsCy);
				}
			});
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryList();
		}

	}

}
