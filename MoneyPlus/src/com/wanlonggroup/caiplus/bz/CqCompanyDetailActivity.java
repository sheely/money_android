package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.Collection2Utils;
import com.damon.ds.util.DSObjectFactory;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;

public class CqCompanyDetailActivity extends BaseActivity {

	DSObject dsCompany;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsCompany = getIntent().getParcelableExtra("company");

		setupView();
		updateView();
		queryDetail();
	}

	ImageView companyIcon;
	TextView cpyName, cpyCategory, cpyDesc, cpyServerProduct, cpyServerClient;

	void setupView() {
		setContentView(R.layout.cq_company_detail);
		companyIcon = (ImageView) findViewById(R.id.cpy_icon);
		cpyName = (TextView) findViewById(R.id.cpy_name);
		cpyCategory = (TextView) findViewById(R.id.category_name);
		cpyDesc = (TextView) findViewById(R.id.desc);
		cpyServerProduct = (TextView) findViewById(R.id.server_product);
		cpyServerClient = (TextView) findViewById(R.id.server_client);
	}

	void updateView() {
		if (dsCompany == null) {
			return;
		}
		imageLoader.displayImage(dsCompany.getString("companyHeadIcon"), companyIcon, displayOptions);
		cpyName.setText(dsCompany.getString("companyName"));
		cpyCategory.setText(dsCompany.getString("companyType"));
		cpyDesc.setText(dsCompany.getString("introduction"));
		
		if(dsCompany.hasKey("companyProducts")){
			StringBuffer buffer = new StringBuffer();
			for (DSObject obj : dsCompany.getArray("companyProducts")) {
				if (buffer.length() > 0) {
					buffer.append("\n");
				}
				buffer.append(obj.getString("productName"));
			}
			cpyServerProduct.setText(buffer.toString());
		}
		
		cpyServerClient.setText(Collection2Utils.toStr(dsCompany.getString("mainClients").split(","), "\n"));
	}

	SHPostTaskM detailTask;

	void queryDetail() {
		detailTask = getTask(DEFAULT_API_URL + "miQueryCompanyDetail.do", this);
		detailTask.getTaskArgs().put("companyId", dsCompany.getString("companyId"));
		detailTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		super.onTaskFinished(task);
		dsCompany = DSObjectFactory.create(CPModeName.CQ_COMPANY_ITEM).fromJson(task.getResult());
		updateView();
	}

}
