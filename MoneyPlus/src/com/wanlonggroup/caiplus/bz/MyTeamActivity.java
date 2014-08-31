package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicSingleItem;

public class MyTeamActivity extends BasePtrListActivity {

	MyTeamAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new MyTeamAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	protected void onPullToRefresh() {
		adapter.reset();
	}

	void queryList() {
		SHPostTaskM teamListTask = getTask(DEFAULT_API_URL + "miQueryTeam.do", this);
		teamListTask.getTaskArgs().put("ownerUserId", accountService().id());
		teamListTask.getTaskArgs().put("ownerUserName", "");
		teamListTask.getTaskArgs().put("teamName", "");
		teamListTask.getTaskArgs().put("memberUserName", "");
		teamListTask.start();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if(Utils.isDSObject(obj, CPModeName.CQ_TEAM_ITEM)){
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cqteamdetail"));
			intent.putExtra("team", (DSObject) obj);
			startActivity(intent);
		}
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		DSObject dsTeams = DSObjectFactory.create(CPModeName.CQ_TEAM_LIST, task.getResult());
		adapter.appendList(dsTeams.getArray(CPModeName.CQ_TEAM_LIST, CPModeName.CQ_TEAM_ITEM));
		listView.onRefreshComplete();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		adapter.appendList(null, task.getRespInfo().getMessage());
		listView.onRefreshComplete();
	}

	class MyTeamAdapter extends BasicDSAdapter {

		String getSubTitle(int isCreatedByMe) {
			if (isCreatedByMe == 0) {
				return "我参与的";
			}
			if (isCreatedByMe == 1) {
				return "我发起的";
			}
			return "";
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(MyTeamActivity.this).inflate(R.layout.common_basic_single_item,
					parent, false);
			}
			BasicSingleItem itemView = (BasicSingleItem) convertView;
			DSObject team = (DSObject) getItem(position);
			itemView.setTitle(team.getString("teamName"));
			itemView.setSubTitle(getSubTitle(team.getInt("isCreatedByMe")));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryList();
		}

	}

}
