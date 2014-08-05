package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;

public class PublishCxListFragment extends CxListFragment {
	
	SHPostTaskM createQueryTask() {
		SHPostTaskM cxlistReq = getTask(DEFAULT_API_URL + "miQueryOppoList.do", this);
		cxlistReq.getTaskArgs().put("statusWithMe", 0);
		cxlistReq.getTaskArgs().put("oppoType", "");
		cxlistReq.getTaskArgs().put("bossName", "");
		cxlistReq.getTaskArgs().put("oppoTitle", "");
		cxlistReq.start();
		return cxlistReq;
	}

	@Override
	CXListAdapter createAdapter() {
		return new PubAdapter();
	}

	public class PubAdapter extends CXListAdapter {
		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pub_cx_list_item, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.type = (TextView) convertView.findViewById(R.id.type);
				holder.pubTime = (TextView) convertView.findViewById(R.id.publish_time);
				holder.status = (TextView) convertView.findViewById(R.id.status);
				holder.button1 = (Button) convertView.findViewById(R.id.toggle_btn);
				holder.button2 = (Button) convertView.findViewById(R.id.comment_btn);
				holder.button3 = (Button) convertView.findViewById(R.id.execute_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final DSObject dsCx = (DSObject) getItem(position);
			holder.title.setText(dsCx.getString("oppoTitle"));
			holder.type.setText("类别：" + dsCx.getString("oppoType"));
			holder.pubTime.setText("发布：" + Utils.formate(dsCx.getString("publishTime")));
			holder.status.setText("状态：" + dsCx.getString("oppoStatus"));
			holder.button1.setTag(dsCx);
			holder.button2.setTag(dsCx);
			holder.button3.setTag(dsCx);
			if ("已关闭".equals(dsCx.getString("oppoStatus"))) {
				holder.button1.setText("打开财信");
				holder.button1.setBackgroundResource(R.drawable.btn_blue);
			} else {
				holder.button1.setText("关闭财信");
				holder.button1.setBackgroundResource(R.drawable.btn_red);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goCxDetail(dsCx);
				}
			});
			
			holder.button1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openOrClose(dsCx);
				}
			});
			holder.button2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxcommentlist"));
					intent.putExtra("caixin", dsCx);
					startActivity(intent);
				}
			});
			holder.button3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxexecuteinfo"));
					intent.putExtra("caixin", dsCx);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	
	DSObject selectedDsCx;
	SHPostTaskM openOrCloseTask;
	void openOrClose(DSObject dsCx){
		openOrCloseTask = getTask(DEFAULT_API_URL+"miOpenOppo.do", this);
		openOrCloseTask.getTaskArgs().put("oppoId", dsCx.getString("oppoId"));
		if ("已关闭".equals(dsCx.getString("oppoStatus"))) {
			openOrCloseTask.getTaskArgs().put("targetStatus", 1);
		}else{
			openOrCloseTask.getTaskArgs().put("targetStatus", 0);
		}
		openOrCloseTask.start();
		selectedDsCx = dsCx;
		showProgressDialog();
	}
	
	@Override
	public void onProgressDialogCancel() {
		if(openOrCloseTask != null){
			openOrCloseTask.cancel(true);
			openOrCloseTask = null;
		}
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if(task == openOrCloseTask){
			openOrCloseTask = null;
			queryList();
		}else{
			super.onTaskFinished(task);
		}
	}
	
	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		if(task == openOrCloseTask){
			openOrCloseTask = null;
			task.getRespInfo().show(getActivity());
		}else{
			super.onTaskFailed(task);
		}
	}
}
