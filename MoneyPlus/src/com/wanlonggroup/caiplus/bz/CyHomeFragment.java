package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;
import com.wanlonggroup.caiplus.bz.im.IMessage;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.widget.CyListItem;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;
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
        adapter = new CyListAdapter();
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
        EventBus.getDefault().register(this);
    };

    public void onEvent(IMessage message) {
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

    class CyListAdapter extends BasicDSAdapter {
        
        public CyListAdapter(){
            isEnd = true;
        }

        @Override
        public View getCPItemView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cy_list_item_view, parent, false);
            }
            CyListItem item = (CyListItem) convertView;
            DSObject dsObject = (DSObject) getItem(position);

            imageLoader.displayImage(dsObject.getString("senderheadicon"), item.getLeftImageView(),
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
