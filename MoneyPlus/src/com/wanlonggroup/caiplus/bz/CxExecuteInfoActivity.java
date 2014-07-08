package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.damon.ds.widget.BasicSingleItem;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.widget.CxDetailHeader;
import com.wanlonggroup.caiplus.widget.CxDetailHeader.HeaderMode;

public class CxExecuteInfoActivity extends BaseActivity {
	
	DSObject dsCaixin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_exec_info);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		setupView();
		updateView();
		queryInfo();
	}

	CxDetailHeader cxDetailHeader;
	BasicSingleItem startTimeItem,endTimeItem,locationItem,budgetItem,remarkItem;
	void setupView(){
		cxDetailHeader = (CxDetailHeader) findViewById(R.id.detail_header);
		cxDetailHeader.setMode(HeaderMode.exec);
		
		startTimeItem = (BasicSingleItem) findViewById(R.id.start_time);
		endTimeItem = (BasicSingleItem) findViewById(R.id.end_time);
		locationItem = (BasicSingleItem) findViewById(R.id.location);
		budgetItem = (BasicSingleItem) findViewById(R.id.budget);
		remarkItem = (BasicSingleItem) findViewById(R.id.remark);
	}
	
	void updateView(){
		if(dsCaixin == null){
			return;
		}
		cxDetailHeader.setDetail(dsCaixin);
		
		startTimeItem.setSubTitle(dsCaixin.getString("startTime"));
		endTimeItem.setSubTitle(dsCaixin.getString("endTime"));
		locationItem.setSubTitle(dsCaixin.getString("executePlace"));
		budgetItem.setSubTitle(dsCaixin.getString("budget"));
		remarkItem.setSubTitle(dsCaixin.getString("remark"));
	}
	
	SHPostTaskM queryTask;

	void queryInfo() {
		queryTask = getTask(DEFAULT_API_URL + "queryexecuteinfodetail.do", this);
//		queryTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryTask.getTaskArgs().put("oppoId", "fdsfdsf133131");
		queryTask.start();
		showProgressDialog();
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		super.onTaskFinished(task);
		dsCaixin = DSObjectFactory.create(CPModeName.CAIXIN_ITEM).fromJson(task.getResult());
		updateView();
	}
	
}
