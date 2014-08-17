package com.wanlonggroup.caiplus.bz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class CyListFragment extends BasePtrListFragment {

	CyListAdapter adapter;

	String oppoType;
	String companyId;
	String friendName;
	String address;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		oppoType = getStringParam("oppotype", "");
		companyId = getStringParam("companyid", "");
		friendName = getStringParam("friendname", "");
		address = getStringParam("address", "");
	};

	public void onActivityCreated(android.os.Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView.setMode(Mode.DISABLED);
		adapter = new CyListAdapter();
		listView.setAdapter(adapter);

	};

	SHPostTaskM queryCyTask;

	void queryCy() {
		queryCyTask = getTask(DEFAULT_API_URL + "miQueryFriend.do", this);
		queryCyTask.getTaskArgs().put("oppoType", oppoType);
		queryCyTask.getTaskArgs().put("companyId", companyId);
		queryCyTask.getTaskArgs().put("friendName", friendName);
		queryCyTask.getTaskArgs().put("address", address);
		queryCyTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		DSObject dsCxList = DSObjectFactory.create(CPModeName.CAIYOU_LIST).fromJson(task.getResult());
		DSObject[] arr = dsCxList.getArray(CPModeName.CAIYOU_LIST, CPModeName.CAIYOU_ITEM);
		adapter.appendList(arr);
		listView.onRefreshComplete();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		adapter.appendList(null, task.getRespInfo().getMessage());
		listView.onRefreshComplete();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (Utils.isDSObject(obj, CPModeName.CAIYOU_ITEM)) {
			if (getBooleanParam("forresult")) {
				Intent intent = new Intent();
				intent.putExtra("caiyou", (DSObject) obj);
				setResult(Activity.RESULT_OK, intent);
				finish();
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cydetail"));
				intent.putExtra("caiyou", (DSObject) obj);
				startActivity(intent);
			}
		}
	}

	class CyListAdapter extends BasicDSAdapter {

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			BasicViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cy_list_query_item, parent,
					false);
				viewHolder = new BasicViewHolder();
				viewHolder.textView1 = (TextView) convertView.findViewById(R.id.username);
				viewHolder.textView2 = (TextView) convertView.findViewById(R.id.add_concern);
				viewHolder.textView3 = (TextView) convertView.findViewById(R.id.send_cx);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (BasicViewHolder) convertView.getTag();
			}
			DSObject dsObj = (DSObject) getItem(position);
			viewHolder.textView1.setText(dsObj.getString("friendName"));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryCy();
		}

	}

}
