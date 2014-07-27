package com.wanlonggroup.caiplus.bz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.model.CPModeName;

public class TeamCqListFragment extends CqListFragment {
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		DSObject dsCqList = DSObjectFactory.create(CPModeName.CQ_TEAM_LIST).fromJson(task.getResult());
		adapter.appendList(dsCqList.getArray(CPModeName.CQ_TEAM_LIST, CPModeName.CQ_TEAM_ITEM));
		listView.onRefreshComplete();
	}

	SHPostTaskM createQueryTask() {
		SHPostTaskM teamListTask = getTask(DEFAULT_API_URL + "miQueryTeam.do", this);
		teamListTask.getTaskArgs().put("ownerUserId", "");
		teamListTask.getTaskArgs().put("ownerUserName", "");
		teamListTask.getTaskArgs().put("teamName", "");
		teamListTask.getTaskArgs().put("memberUserName", "");
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
