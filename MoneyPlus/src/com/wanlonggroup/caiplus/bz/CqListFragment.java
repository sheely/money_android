package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ds.app.DSObject;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;

public abstract class CqListFragment extends BasePtrListFragment {
	CqListAdapter adapter;

	public void onActivityCreated(android.os.Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = createAdapter();
		listView.setAdapter(adapter);
	};

	abstract SHPostTaskM createQueryTask();

	abstract CqListAdapter createAdapter();

	void queryList() {
		SHPostTaskM task = createQueryTask();
		if (task != null) {
			task.start();
		}
	}

	@Override
	protected void onPullToRefresh() {
		adapter.reset();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (Utils.isDSObject(obj, CPModeName.CQ_COMPANY_ITEM)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cqcompanydetail"));
			intent.putExtra("company", (DSObject) obj);
			startActivity(intent);
		} else if (Utils.isDSObject(obj, CPModeName.CQ_TEAM_ITEM)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cqteamdetail"));
			intent.putExtra("team", (DSObject) obj);
			startActivity(intent);
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		adapter.appendList(null, task.getRespInfo().getMessage());
		listView.onRefreshComplete();
	}

	class CqListAdapter extends BasicDSAdapter {

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryList();
		}

	}
	
	public static class ViewHolder {
		public ImageView icon;
		public TextView title;
	}

}
