package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.wanlonggroup.caiplus.bz.im.IMessaging;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.util.StringUtils;

import de.greenrobot.event.EventBus;

public class ChatActivity extends BasePtrListActivity implements OnClickListener {

    DSObject dsCaiYou;
    Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsCaiYou = getIntent().getParcelableExtra("caiyou");
        if (dsCaiYou == null) {
            finish();
            return;
        }
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(IMessaging message) {
        if (message.senderUserId.equals(dsCaiYou.getString("friendId"))) {
            DSObject dsMsg = DSObjectFactory.create(CPModeName.CY_CHAT_HISTORY_ITEM);
            dsMsg.put("senderuserid", message.senderUserId);
            dsMsg.put("senderusername", message.senderUserName);
            dsMsg.put("senderheadicon", message.senderHeadIcon);
            dsMsg.put("sendtime", message.sendTime);
            dsMsg.put("receiveruserid", message.receiverUserId);
            dsMsg.put("chatcontent", message.chatContent);

            adapter.append(dsMsg);
            listView.getRefreshableView().setSelection(adapter.getCount() - 1);
            EventBus.getDefault().post(message.toIMessaged());
        }else{
            EventBus.getDefault().post(message.toIMessage());
        }
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
        inputEditText.setFilters(new InputFilter[] { inputFilter });

        listView.setMode(Mode.DISABLED);
        adapter = new Adapter();
        listView.setAdapter(adapter);
    }

    private final InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                int dstart, int dend) {
            StringBuffer buffer = new StringBuffer();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                // 第一个字符为以下时，过滤掉
                if (c == 55356 || c == 55357 || c == 10060 || c == 9749 || c == 9917 || c == 10067
                        || c == 10024 || c == 11088 || c == 9889 || c == 9729 || c == 11093
                        || c == 9924) {
                    i++;
                    continue;
                } else {
                    buffer.append(c);
                }
            }

            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(buffer);
                TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                return sp;
            } else {
                return buffer;
            }
        }
    };

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
        sendTask.getTaskArgs().put("receiveruserid", dsCaiYou.getString("friendId"));
        sendTask.getTaskArgs().put("senderuserid", accountService().id());
        sendTask.getTaskArgs().put("chatcontent", message);
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
            DSObject dsChatMessages = DSObjectFactory.create(CPModeName.CY_CHAT_HISTORY_LIST).fromJson(
                task.getResult());
            adapter.appendList(dsChatMessages.getArray(CPModeName.CY_CHAT_HISTORY_LIST,
                CPModeName.CY_CHAT_HISTORY_ITEM));
            listView.getRefreshableView().setSelection(adapter.getCount() - 1);
        } else if (sendTask == task) {
            dismissProgressDialog();
            DSObject dsMsg = DSObjectFactory.create(CPModeName.CY_CHAT_HISTORY_ITEM);
            dsMsg.put("senderuserid", accountService().id());
            dsMsg.put("senderusername", accountService().name());
            dsMsg.put("senderheadicon", accountService().headIcon());
            dsMsg.put("sendtime", Utils.getCurrentTime(Utils.dateTimeFormat));
            dsMsg.put("receiveruserid", dsCaiYou.getString("friendId"));
            dsMsg.put("chatcontent", message);
            dsMsg.put("issendbyme", 1);

            adapter.append(dsMsg);
            inputEditText.getText().clear();
            listView.getRefreshableView().setSelection(adapter.getCount() - 1);
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
            if (Utils.isDSObject(obj, CPModeName.CY_CHAT_HISTORY_ITEM)) {
                DSObject msg = (DSObject) obj;
                if (msg.getInt("issendbyme", 0) == 1) {
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
                    convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.message_item_l, parent, false);
                } else {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.message_item_r, parent, false);
                }
                viewHolder = new BasicViewHolder();
                viewHolder.icon1 = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.textView1 = (TextView) convertView.findViewById(R.id.time);
                viewHolder.textView2 = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BasicViewHolder) convertView.getTag();
            }
            imageLoader.displayImage(message.getString("senderheadicon"), viewHolder.icon1,
                displayOptions);
            viewHolder.textView1.setText(message.getString("senderusername") + " "
                    + message.getString("sendtime"));
            viewHolder.textView2.setText(message.getString("chatcontent"));
            return convertView;
        }

        @Override
        public void loadNextData(int startIndex) {
            queryMessages();
        }

    }

}
