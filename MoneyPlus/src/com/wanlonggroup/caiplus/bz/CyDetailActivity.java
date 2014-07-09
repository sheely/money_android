package com.wanlonggroup.caiplus.bz;

import java.util.Arrays;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.ds.app.DSObject;
import com.damon.ds.util.Collection2Utils;
import com.damon.ds.util.DSObjectFactory;
import com.damon.ds.widget.BasicItem;
import com.damon.ds.widget.BasicSingleItem;
import com.damon.ds.widget.TableView;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.CommonDSAdapter;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;

public class CyDetailActivity extends BaseActivity {

	String publisherId;
	DSObject dsCyDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		publisherId = getStringParam("publisherid");
		setupView();
		queryDetail();
	}

	TextView userNameView, techonologyTitleView, personalExperienceView, personalClientsView;
	BasicItem compayItem, jobItem, emailItem, mobileItem, chargeItem, degreeItem, collegeItem;
	TableView personalSkillsTable, successfulCasesTable, myTasksTable;
	ImageView headIcon;

	void setupView() {
		setContentView(R.layout.cy_detail);
		userNameView = (TextView) findViewById(R.id.username);
		headIcon = (ImageView) findViewById(R.id.icon);

		techonologyTitleView = (TextView) findViewById(R.id.techonologyTitle);
		personalExperienceView = (TextView) findViewById(R.id.personalExperience);
		personalClientsView = (TextView) findViewById(R.id.personalClients);
		compayItem = (BasicItem) findViewById(R.id.company_item);
		jobItem = (BasicItem) findViewById(R.id.job_item);
		emailItem = (BasicItem) findViewById(R.id.email_item);
		mobileItem = (BasicItem) findViewById(R.id.mobile_item);
		chargeItem = (BasicItem) findViewById(R.id.charge_item);
		degreeItem = (BasicItem) findViewById(R.id.degree_item);
		collegeItem = (BasicItem) findViewById(R.id.college_item);

		personalSkillsTable = (TableView) findViewById(R.id.personalSkills);
		successfulCasesTable = (TableView) findViewById(R.id.successfulCases);
		myTasksTable = (TableView) findViewById(R.id.myTasks);

	}

	void updateView() {
		if (dsCyDetail == null) {
			return;
		}
		userNameView.setText(dsCyDetail.getString("friendName"));
		imageLoader.displayImage(dsCyDetail.getString("friendHeadIcon"), headIcon);
		compayItem.setSubTitle(dsCyDetail.getString("companyName"));
		jobItem.setSubTitle(dsCyDetail.getString("friendTitle"));
		emailItem.setSubTitle(dsCyDetail.getString("friendMail"));
		mobileItem.setSubTitle(dsCyDetail.getString("friendMobile"));
		chargeItem.setSubTitle(dsCyDetail.getString("chargeStandard"));
		degreeItem.setSubTitle(dsCyDetail.getString("maxAttainment"));
		collegeItem.setSubTitle(dsCyDetail.getString("graduatedSchool"));

		DSObject[] tmp = dsCyDetail.getArray("techonologyTitle","techonologyName");
		techonologyTitleView.setText(null);
		if (!Collection2Utils.isEmpty(tmp)) {
			for (DSObject obj : tmp) {
				if (!TextUtils.isEmpty(techonologyTitleView.getText())) {
					techonologyTitleView.append("\n");
				}
				techonologyTitleView.append(obj.getString("techonologyName"));
			}
		}

		tmp = dsCyDetail.getArray("personalExperience","experienceName");
		personalExperienceView.setText(null);
		if (!Collection2Utils.isEmpty(tmp)) {
			for (DSObject obj : tmp) {
				if (!TextUtils.isEmpty(personalExperienceView.getText())) {
					personalExperienceView.append("\n");
				}
				personalExperienceView.append(obj.getString("experienceName"));
			}
		}

		tmp = dsCyDetail.getArray("personalClients","clientName");
		personalClientsView.setText(null);
		if (!Collection2Utils.isEmpty(tmp)) {
			for (DSObject obj : tmp) {
				if (!TextUtils.isEmpty(personalClientsView.getText())) {
					personalClientsView.append("\n");
				}
				personalClientsView.append(obj.getString("clientName"));
			}
		}
		
		personalSkillsTable.setAdapter(new Adapter(dsCyDetail.getArray("personalSkills")));
		successfulCasesTable.setAdapter(new Adapter(dsCyDetail.getArray("successfulCases")));
		myTasksTable.setAdapter(new Adapter(dsCyDetail.getArray("myTasks")));
	}

	SHPostTaskM detailTask;

	void queryDetail() {
		detailTask = getTask(DEFAULT_API_URL + "miQueryFriendDetail.do", this);
		detailTask.getTaskArgs().put("friendId", publisherId);
		detailTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsCyDetail = DSObjectFactory.create(CPModeName.CY_DETAIL).fromJson(task.getResult());
		updateView();
	}

	class Adapter extends CommonDSAdapter {

		public Adapter(DSObject[] data) {
			if (data != null) {
				this.dsList.addAll(Arrays.asList(data));
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DSObject obj = getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_basic_single_item,
					parent, false);
			}
			BasicSingleItem item = (BasicSingleItem) convertView;
			if (obj.isObject("personalSkills")) {
				item.setTitle(obj.getString("skillTitle"));
			} else if (obj.isObject("successfulCases")) {
				item.setTitle(obj.getString("caseTitle"));
			} else if (obj.isObject("myTasks")) {
				item.setTitle(obj.getString("startTime") + " - " + obj.getString("endTime"));
				item.setSubTitle(obj.getString("taskStatus"));
				item.setSubTitleColor(getResources().getColor(R.color.red));
			}
			return convertView;
		}

	}

}
