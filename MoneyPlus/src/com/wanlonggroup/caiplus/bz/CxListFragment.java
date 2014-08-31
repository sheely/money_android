package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public abstract class CxListFragment extends BasePtrListFragment {

    CXListAdapter adapter;
    int statusWithMe;
    String oppoType;
    String bossName;
    String oppoTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusWithMe = getIntParam("statuswithme");
        oppoType = getStringParam("oppotype", "");
        bossName = getStringParam("bossname", "");
        oppoTitle = getStringParam("oppotitle", "");
    }

    public void onActivityCreated(android.os.Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = createAdapter();
        listView.setAdapter(adapter);
    };

    abstract SHPostTaskM createQueryTask();

    abstract CXListAdapter createAdapter();

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
        if (Utils.isDSObject(obj, CPModeName.CAIXIN_ITEM)) {
            goCxDetail((DSObject) obj);
        }
    }

    public void goCxDetail(DSObject dsCaixin) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxdetail"));
        if (statusWithMe == 0) {
            intent.putExtra("commentertype", 2);
        } else if (statusWithMe == 1) {
            intent.putExtra("commentertype", 1);
        }

        intent.putExtra("caixin", dsCaixin);
        startActivity(intent);
    }

    @Override
    public void onTaskFinished(SHTask task) throws Exception {
        DSObject dsCxList = DSObjectFactory.create(CPModeName.CAIXIN_LIST).fromJson(
            task.getResult());
        DSObject[] arr = dsCxList.getArray(CPModeName.CAIXIN_LIST, CPModeName.CAIXIN_ITEM);
        adapter.appendList(arr);
        listView.onRefreshComplete();
    }

    @Override
    public void onTaskFailed(SHTask task) {
        adapter.appendList(null, task.getRespInfo().getMessage());
        listView.onRefreshComplete();
    }

    public class CXListAdapter extends BasicDSAdapter {

        @Override
        public View getCPItemView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cx_list_item, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                holder.pubTime = (TextView) convertView.findViewById(R.id.publish_time);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DSObject dsCx = (DSObject) getItem(position);
            holder.title.setText(dsCx.getString("oppoTitle"));
            holder.type.setText("类别：" + dsCx.getString("oppoType"));
            holder.pubTime.setText("发布：" + Utils.formate(dsCx.getString("publishTime")));
            holder.status.setText("状态：" + dsCx.getString("oppoStatus"));
            return convertView;
        }

        @Override
        public void loadNextData(int startIndex) {
            queryList();
        }
    }

    public static class ViewHolder {
        public TextView title;
        public TextView type;
        public TextView pubTime;
        public TextView status;
        public Button button1, button2, button3;
    }

}
