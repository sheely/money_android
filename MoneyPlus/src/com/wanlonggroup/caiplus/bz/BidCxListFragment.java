package com.wanlonggroup.caiplus.bz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.damon.ds.app.DSObject;
import com.next.net.SHPostTaskM;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.util.Utils;

public class BidCxListFragment extends CxListFragment {

	SHPostTaskM createQueryTask() {
		SHPostTaskM cxlistReq = getTask(DEFAULT_API_URL + "miQueryOppoList.do", this);
		cxlistReq.getTaskArgs().put("statusWithMe", 2);
		cxlistReq.getTaskArgs().put("oppoType", "");
		cxlistReq.getTaskArgs().put("bossName", "");
		cxlistReq.getTaskArgs().put("oppoTitle", "");
		cxlistReq.start();
		return cxlistReq;
	}

	@Override
	CXListAdapter createAdapter() {
		return new BidAdapter();
	}

	public class BidAdapter extends CXListAdapter {
		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_cx_list_item, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.type = (TextView) convertView.findViewById(R.id.type);
				holder.pubTime = (TextView) convertView.findViewById(R.id.publish_time);
				holder.status = (TextView) convertView.findViewById(R.id.status);
				holder.button1 = (Button) convertView.findViewById(R.id.comment_btn);
				holder.button2 = (Button) convertView.findViewById(R.id.execute_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final DSObject dsCx = (DSObject) getItem(position);
			holder.title.setText(dsCx.getString("oppoTitle"));
			holder.type.setText("类别：" + dsCx.getString("oppoType"));
			holder.pubTime.setText("发布：" + Utils.formate(dsCx.getString("publishTime")));
			holder.status.setText("状态：" + dsCx.getString("oppoStatus"));
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goCxDetail(dsCx);
				}
			});
			return convertView;
		}
	}

}
