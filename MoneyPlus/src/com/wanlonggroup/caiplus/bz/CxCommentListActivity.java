package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicSingleVerticalItem;

public class CxCommentListActivity extends BasePtrListActivity {

	DSObject dsCaixin;
	Adapter adapter;
	DSObject dsComments;
	int commenterType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		if (dsCaixin == null) {
			finish();
			return;
		}

		commenterType = getIntParam("commentertype", -1);
		
		listView.setMode(Mode.DISABLED);
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	SHPostTaskM queryTask;

	void queryComment() {
		queryTask = getTask(DEFAULT_API_URL + "queryCommentsList.do", this);
		queryTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsComments = DSObjectFactory.create(CPModeName.CAIXIN_COMMENT_LIST).fromJson(task.getResult());
		adapter.appendList(dsComments.getArray(CPModeName.CAIXIN_COMMENT_LIST, CPModeName.CAIXIN_COMMENT_ITEM));
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		adapter.appendList(null, task.getRespInfo().getMessage());
	}

	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		if (commenterType == 1 || commenterType == 2) {
			actionBar.addAction("评论", "ping_lun", new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://addcxcomment"));
					intent.putExtra("commentertype", commenterType);
					intent.putExtra("caixin", dsCaixin);
					for (int i = 0; i < adapter.getCount(); i++) {
						if (commenterType == adapter.getCommentType(i)) {
							intent.putExtra("leavecomment", adapter.getComment(i));
							break;
						}
					}
					startActivityForResult(intent, 1);
				}
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			return;
		}
		if(requestCode == 1){
			adapter.reset();
		}
	}

	class Adapter extends BasicDSAdapter {

		public String getCommentTypeName(int position) {
			try {
				DSObject comment = (DSObject) getItem(position);
				if ("1".equals(comment.getString("commenterType"))) {
					return "承接人评论：";
				}
				if ("2".equals(comment.getString("commenterType"))) {
					return "执行人评论：";
				}
			} catch (Exception e) {

			}

			return "";
		}

		public int getCommentType(int position) {
			try {
				DSObject comment = (DSObject) getItem(position);
				if ("1".equals(comment.getString("commenterType"))) {
					return 1;
				}
				if ("2".equals(comment.getString("commenterType"))) {
					return 2;
				}
			} catch (Exception e) {

			}

			return -1;
		}

		public String getComment(int position) {
			try {
				DSObject comment = (DSObject) getItem(position);
				return comment.getString("commentContent");
			} catch (Exception e) {

			}

			return "";
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			DSObject comment = (DSObject) getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_comment_item, parent,
					false);
			}
			BasicSingleVerticalItem commentItem = (BasicSingleVerticalItem) convertView;
			commentItem.setTitle(getCommentTypeName(position));
			commentItem.setSubTitle(comment.getString("commentContent"));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryComment();
		}

	}

}
