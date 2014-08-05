package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class CqTeamListFragment extends CqListFragment {
	
	String ownerUserId;
	String ownerUserName;
	String teamName;
	String memberUserName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ownerUserId = getStringParam("owneruserid", "");
		ownerUserName = getStringParam("ownerusername", "");
		teamName = getStringParam("teamname", "");
		memberUserName = getStringParam("memberusername", "");
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		DSObject dsCqList = DSObjectFactory.create(CPModeName.CQ_TEAM_LIST).fromJson(task.getResult());
		adapter.appendList(dsCqList.getArray(CPModeName.CQ_TEAM_LIST, CPModeName.CQ_TEAM_ITEM));
		listView.onRefreshComplete();
	}

	SHPostTaskM createQueryTask() {
		SHPostTaskM teamListTask = getTask(DEFAULT_API_URL + "miQueryTeam.do", this);
		teamListTask.getTaskArgs().put("ownerUserId", ownerUserId);
		teamListTask.getTaskArgs().put("ownerUserName", ownerUserName);
		teamListTask.getTaskArgs().put("teamName", teamName);
		teamListTask.getTaskArgs().put("memberUserName", memberUserName);
		teamListTask.start();
		return teamListTask;
	}

	@Override
	CqListAdapter createAdapter() {
		return new TeamListAdapter();
	}
	
	class TeamListAdapter extends CqListAdapter{
		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cq_company_list_item, parent, false);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			DSObject dsCq = (DSObject) getItem(position);
			holder.title.setText(dsCq.getString("teamName"));
			imageLoader.displayImage(dsCq.getString("teamHeadIcon"), holder.icon, displayOptions);
			return convertView;
		}
	}

}
