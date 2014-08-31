package com.wanlonggroup.caiplus.bz;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;
import com.wanlonggroup.caiplus.bz.im.ChatHelper;
import com.wanlonggroup.caiplus.bz.im.IMessage;
import com.wanlonggroup.caiplus.bz.im.IMessaged;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.wanlonggroup.caiplus.widget.CyListItem;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;
import com.xdamon.util.Collection2Utils;
import com.xdamon.util.DSObjectFactory;

import de.greenrobot.event.EventBus;

public class CyHomeFragment extends BasePtrListFragment implements View.OnClickListener {

    CyListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasActionBar(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBar().setDisplayHomeAsUpEnabled(false);
        setTitle("财友");

        listView.setMode(Mode.DISABLED);
        adapter = new CyListAdapter(ChatHelper.instance(getActivity()).getChatingList());
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateActionBar(DSActionBar actionBar) {
        actionBar.addAction(R.drawable.ic_search, "ic_search", this);
    }

    @Override
    public void onClick(View v) {
        startActivity("cp://querycy");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().registerSticky(this);
    };

    // 已读消息
    public void onEventMainThread(IMessaged message) {
        DSObject dsMsg = DSObjectFactory.create(CPModeName.CY_CHAT_MESSAGE_ITEM);
        dsMsg.put("senderuserid", message.senderUserId);
        dsMsg.put("senderusername", message.senderUserName);
        dsMsg.put("senderheadicon", message.senderHeadIcon);
        dsMsg.put("sendtime", message.sendTime);
        dsMsg.put("receiveruserid", message.receiverUserId);
        dsMsg.put("chatcontent", message.chatContent);

        adapter.append(dsMsg);
        listView.getRefreshableView().setSelection(adapter.getCount() - 1);
    }

    // 未读消息
    public void onEventMainThread(IMessage message) {
        DSObject dsMsg = DSObjectFactory.create(CPModeName.CY_CHAT_MESSAGE_ITEM);
        dsMsg.put("senderuserid", message.senderUserId);
        dsMsg.put("senderusername", message.senderUserName);
        dsMsg.put("senderheadicon", message.senderHeadIcon);
        dsMsg.put("sendtime", message.sendTime);
        dsMsg.put("receiveruserid", message.receiverUserId);
        dsMsg.put("chatcontent", message.chatContent);

        adapter.append(dsMsg);
        listView.getRefreshableView().setSelection(adapter.getCount() - 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (Utils.isDSObject(obj, CPModeName.CY_CHAT_MESSAGE_ITEM)) {
            DSObject dsObject = (DSObject) obj;
            DSObject dsCy = new DSObject(CPModeName.CAIYOU_ITEM);
            dsCy.put("friendId", dsObject.getString("senderuserid"));
            dsCy.put("friendName", dsObject.getString("senderusername"));
            dsCy.put("friendHeadIcon", dsObject.getString("senderheadicon"));
            ChatHelper.instance(getActivity()).chat2Who(getActivity(), dsCy);
        }
    }

    class CyListAdapter extends BasicDSAdapter {

        public CyListAdapter(ArrayList<DSObject> data) {
            isEnd = true;
            if (!Collection2Utils.isEmpty(data)) {
                dsList.addAll(data);
            }
        }

        @Override
        public void append(DSObject obj) {
            for (DSObject dsMsg : dsList) {
                if (obj.getString("senderuserid").equals(dsMsg.getString("senderuserid"))) {
                    dsList.remove(dsMsg);
                    break;
                }
            }
            dsList.add(0, obj);
            notifyDataSetChanged();
            ChatHelper.instance(getActivity()).saveChatingList(dsList);
        }

        @Override
        public View getCPItemView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cy_list_item_view, parent, false);
            }
            CyListItem item = (CyListItem) convertView;
            DSObject dsObject = (DSObject) getItem(position);

            imageLoader.displayImage(dsObject.getString("senderheadicon"), item.getIcon(),
                displayOptions);
            item.setTitle(dsObject.getString("senderusername") + " "
                    + dsObject.getString("sendtime"));
            item.setSubTitle(dsObject.getString("chatcontent"));
            return convertView;
        }

        @Override
        public void loadNextData(int startIndex) {
        }
    }

}
