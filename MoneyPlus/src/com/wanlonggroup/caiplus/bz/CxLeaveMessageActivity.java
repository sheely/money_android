package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.util.StringUtils;

public class CxLeaveMessageActivity extends BasePtrListActivity implements OnClickListener {

	DSObject dsCaixin;
	DSObject dsLeavemessages;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		setupView();
	}

	protected void onSetContentView() {
		setContentView(R.layout.cx_review_detail);
	};

	Button pubButton;
	EditText inputEditText;

	void setupView() {
		pubButton = (Button) findViewById(R.id.pub_btn);
		pubButton.setOnClickListener(this);

		inputEditText = (EditText) findViewById(R.id.input_message);

		listView.setMode(Mode.DISABLED);
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	SHPostTaskM queryTask;

	void queryMessages() {
		queryTask = getTask(DEFAULT_API_URL + "queryLmList.do", this);
		queryTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryTask.start();
	}

	SHPostTaskM sendTask;
	String message;

	void sendMessage() {
		message = inputEditText.getText().toString();
		if (StringUtils.isEmpty(message)) {
			return;
		}
		sendTask = getTask(DEFAULT_API_URL + "lmAdd.do", this);
		sendTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		sendTask.getTaskArgs().put("leaveMessage", message);
		sendTask.start();
		showProgressDialog("消息发送中...");
	}

	@Override
	public void onClick(View v) {
		if (v == pubButton) {
			sendMessage();
		}
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		if (queryTask == task) {
			dsLeavemessages = DSObjectFactory.create(CPModeName.CAIXIN_LEAVE_MESSAGE_LIST).fromJson(task.getResult());
			adapter.appendList(dsLeavemessages.getArray(CPModeName.CAIXIN_LEAVE_MESSAGE_LIST,
				CPModeName.CAIXIN_LEAVE_MESSAGE_ITEM));
		} else if (sendTask == task) {
			dismissProgressDialog();
			DSObject dsMsg = DSObjectFactory.create(CPModeName.CAIXIN_LEAVE_MESSAGE_ITEM);
			dsMsg.put("leaveMessager", accountService().id());
			dsMsg.put("lmTime", Utils.getCurrentTime(Utils.dateTimeFormat));
			dsMsg.put("lmContent", message);
			dsMsg.put("lmHeadIcon", accountService().headIcon());
			adapter.append(dsMsg);
			if (listView.getRefreshableView().isStackFromBottom()) {
				listView.getRefreshableView().setStackFromBottom(false);
			}
			listView.getRefreshableView().setStackFromBottom(true);
			inputEditText.getText().clear();
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		if (queryTask == task) {
			adapter.appendList(null, task.getRespInfo().getMessage());
		} else {
			dismissProgressDialog();
			task.getRespInfo().show(this);
		}
	}

	class Adapter extends BasicDSAdapter {

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}
		
		@Override
		public int getItemViewType(int position) {
			Object obj = getItem(position);
			if(Utils.isDSObject(obj, CPModeName.CAIXIN_LEAVE_MESSAGE_ITEM)){
				DSObject msg = (DSObject) obj;
				if(accountService().id().equals(msg.getString("leaveMessager"))){
					return 3;
				}else{
					return 4;
				}
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			BasicViewHolder viewHolder;
			DSObject message = (DSObject) getItem(position);
			if (convertView == null) {
				if(getItemViewType(position) == 3){
					convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_l, parent, false);
				}else{
					convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_r, parent, false);
				}
				viewHolder = new BasicViewHolder();
				viewHolder.icon1 = (ImageView) convertView.findViewById(R.id.icon);
				viewHolder.textView1 = (TextView) convertView.findViewById(R.id.time);
				viewHolder.textView2 = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (BasicViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(message.getString("lmHeadIcon"), viewHolder.icon1, displayOptions);
			viewHolder.textView1.setText(message.getString("leaveMessager") + " " + message.getString("lmTime"));
			viewHolder.textView2.setText(message.getString("lmContent"));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryMessages();
		}

	}

}
