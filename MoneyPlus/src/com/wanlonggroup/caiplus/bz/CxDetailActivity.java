package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.widget.CxDetailHeader;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class CxDetailActivity extends BaseActivity implements OnClickListener {

	DSObject dsCaixin;
	String caixinId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		if (dsCaixin != null) {
			caixinId = dsCaixin.getString("oppoId");
		}
		if (TextUtils.isEmpty(caixinId)) {
			caixinId = getStringParam("oppoId");
		}
		if (TextUtils.isEmpty(caixinId)) {
			showShortToast("缺少必要参数!");
			finish();
			return;
		}
		setContentView(R.layout.cx_detail);
		setupView();
		updateView();
		queryCaixin();
	}

	CxDetailHeader cxDetailHeader;
	TextView desView;
	Button reviewBtn;

	void setupView() {
		cxDetailHeader = (CxDetailHeader) findViewById(R.id.detail_header);
		desView = (TextView) findViewById(R.id.desc);
		reviewBtn = (Button) findViewById(R.id.review_btn);
		reviewBtn.setOnClickListener(this);
	}

	void updateView() {
		if (dsCaixin == null) {
			return;
		}
		cxDetailHeader.setDetail(dsCaixin);
		desView.setText(dsCaixin.getString("oppoContent"));
	}

	SHPostTaskM queryTask;

	void queryCaixin() {
		queryTask = getTask(DEFAULT_API_URL + "miQueryOppoDetail.do", this);
		queryTask.getTaskArgs().put("oppoId", caixinId);
		queryTask.start();
		showProgressDialog();
	}

	@Override
	public void onClick(View v) {
		if(dsCaixin == null){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cxreviewdetail"));
		intent.putExtra("caixin", dsCaixin);
		startActivity(intent);
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsCaixin = DSObjectFactory.create(CPModeName.CAIXIN_ITEM).fromJson(task.getResult());
		updateView();
	}
	
	@Override
	public void onProgressDialogCancel() {
		super.onProgressDialogCancel();
		finish();
	}

}
