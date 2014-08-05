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

public class ChatActivity extends BasePtrListActivity implements OnClickListener {

	DSObject dsCaiYou;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCaiYou = getIntent().getParcelableExtra("caiyou");
		setupView();
	}

	protected void onSetContentView() {
		setContentView(R.layout.chat_activity);
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
		queryTask = getTask(DEFAULT_API_URL + "miQueryChatHistory.do", this);
		queryTask.getTaskArgs().put("anotheruserid", dsCaiYou.getString("friendId"));
		queryTask.getTaskArgs().put("myuserid", accountService().id());
		queryTask.start();
	}

	SHPostTaskM sendTask;
	String message;

	void sendMessage() {
		message = inputEditText.getText().toString();
		if (StringUtils.isEmpty(message)) {
			return;
		}
		sendTask = getTask(DEFAULT_API_URL + "miSendMessage.do", this);
		queryTask.getTaskArgs().put("receiveruserid", dsCaiYou.getString("friendId"));
		queryTask.getTaskArgs().put("senderuserid", accountService().id());
		queryTask.getTaskArgs().put("chatcontent", accountService().id());
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
			DSObject dsChatMessages = DSObjectFactory.create(CPModeName.CY_CHAT_LIST).fromJson(task.getResult());
			adapter.appendList(dsChatMessages.getArray(CPModeName.CY_CHAT_LIST, CPModeName.CY_CHAT_ITEM));
		} else if (sendTask == task) {
			dismissProgressDialog();
			DSObject dsMsg = DSObjectFactory.create(CPModeName.CY_CHAT_ITEM);
			dsMsg.put("senderuserid", accountService().id());
			dsMsg.put("senderusername", accountService().name());
			dsMsg.put("senderheadicon", accountService().headIcon());
			dsMsg.put("sendtime", Utils.getCurrentTime(Utils.dateFormat3));
			dsMsg.put("receiveruserid", dsCaiYou.getString("friendId"));
			dsMsg.put("chatcontent", message);

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
			if (Utils.isDSObject(obj, CPModeName.CAIXIN_LEAVE_MESSAGE_ITEM)) {
				DSObject msg = (DSObject) obj;
				if (accountService().id().equals(msg.getString("senderuserid"))) {
					return 3;
				} else {
					return 4;
				}
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			BasicViewHolder viewHolder;
			DSObject message = (DSObject) getItem(position);
			int viewType = getItemViewType(position);
			if (convertView == null) {
				if (viewType == 3) {
					convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_l, parent,
						false);
				} else {
					convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_r, parent,
						false);
				}
				viewHolder = new BasicViewHolder();
				viewHolder.icon1 = (ImageView) convertView.findViewById(R.id.icon);
				viewHolder.textView1 = (TextView) convertView.findViewById(R.id.time);
				viewHolder.textView2 = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (BasicViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(
				(viewType == 3 ? accountService().headIcon() : message.getString("senderheadicon")), viewHolder.icon1,
				displayOptions);
			viewHolder.textView1.setText((viewType == 3 ? accountService().name() : message.getString("senderusername"))
					+ " " + message.getString("sendtime"));
			viewHolder.textView2.setText(message.getString("chatcontent"));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryMessages();
		}

	}

}
