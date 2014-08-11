package com.wanlonggroup.caiplus.bz;

import java.util.Arrays;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.CommonDSAdapter;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.bz.im.ChatHelper;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.Collection2Utils;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicItem;
import com.xdamon.widget.BasicSingleItem;
import com.xdamon.widget.TableView;

public class CyDetailActivity extends BaseActivity implements View.OnClickListener, TableView.OnItemClickListener {

	String publisherId;
	DSObject dsCyDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		publisherId = getStringParam("publisherid");
		if(TextUtils.isEmpty(publisherId)){
			publisherId = getStringParam("friendid");
		}
		setupView();
		queryDetail();
	}

	TextView concernView, sendcxView, userNameView, techonologyTitleView, personalExperienceView, personalClientsView;
	BasicItem compayItem, jobItem, emailItem, mobileItem, chargeItem, degreeItem, collegeItem;
	TableView personalSkillsTable, successfulCasesTable, myTasksTable;
	ImageView headIcon;

	void setupView() {
		setContentView(R.layout.cy_detail);
		userNameView = (TextView) findViewById(R.id.username);
		headIcon = (ImageView) findViewById(R.id.icon);
		concernView = (TextView) findViewById(R.id.concern);
		concernView.setOnClickListener(this);
		sendcxView = (TextView) findViewById(R.id.send_cx);
		sendcxView.setOnClickListener(this);

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
		personalSkillsTable.setOnItemClickListener(this);

		successfulCasesTable = (TableView) findViewById(R.id.successfulCases);
		successfulCasesTable.setOnItemClickListener(this);

		myTasksTable = (TableView) findViewById(R.id.myTasks);

	}

	void updateView() {
		if (dsCyDetail == null) {
			return;
		}
		userNameView.setText(dsCyDetail.getString("friendName"));
		imageLoader.displayImage(dsCyDetail.getString("friendHeadIcon"), headIcon);
		concernView.setText(dsCyDetail.getInt("isFollowed") == 1 ? "取消关注" : "加关注");

		compayItem.setSubTitle(dsCyDetail.getString("companyName"));
		jobItem.setSubTitle(dsCyDetail.getString("friendTitle"));
		emailItem.setSubTitle(dsCyDetail.getString("friendMail"));
		mobileItem.setSubTitle(dsCyDetail.getString("friendMobile"));
		chargeItem.setSubTitle(dsCyDetail.getString("chargeStandard"));
		degreeItem.setSubTitle(dsCyDetail.getString("maxAttainment"));
		collegeItem.setSubTitle(dsCyDetail.getString("graduatedSchool"));

		DSObject[] tmp = dsCyDetail.getArray("techonologyTitle", "techonologyName");
		techonologyTitleView.setText(null);
		if (!Collection2Utils.isEmpty(tmp)) {
			for (DSObject obj : tmp) {
				if (!TextUtils.isEmpty(techonologyTitleView.getText())) {
					techonologyTitleView.append("\n");
				}
				techonologyTitleView.append(obj.getString("techonologyName"));
			}
		}

		tmp = dsCyDetail.getArray("personalExperience", "experienceName");
		personalExperienceView.setText(null);
		if (!Collection2Utils.isEmpty(tmp)) {
			for (DSObject obj : tmp) {
				if (!TextUtils.isEmpty(personalExperienceView.getText())) {
					personalExperienceView.append("\n");
				}
				personalExperienceView.append(obj.getString("experienceName"));
			}
		}

		tmp = dsCyDetail.getArray("personalClients", "clientName");
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

	public void onClick(View v) {
		if (v == concernView) {
			addOrDelete();
		}else if(v == sendcxView){
			ChatHelper.instance(this).chat2Who(this, dsCyDetail);
		}
	};

	public void onItemClick(TableView parent, View view, int position, long id) {
		if (parent == personalSkillsTable) {
			DSObject obj = (DSObject) personalSkillsTable.getAdapter().getItem(position);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://skilldetail"));
			intent.putExtra("itemtype", 1);
			intent.putExtra("itemid", obj.getString("skillId"));
			intent.putExtra("itemtitle", obj.getString("skillTitle"));
			startActivity(intent);

		} else if (parent == successfulCasesTable) {
			DSObject obj = (DSObject) successfulCasesTable.getAdapter().getItem(position);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://casedetail"));
			intent.putExtra("itemtype", 2);
			intent.putExtra("itemid", obj.getString("caseId"));
			intent.putExtra("itemtitle", obj.getString("caseTitle"));
			startActivity(intent);
		}
	};

	SHPostTaskM detailTask;

	void queryDetail() {
		detailTask = getTask(DEFAULT_API_URL + "miQueryFriendDetail.do", this);
		detailTask.getTaskArgs().put("friendId", publisherId);
		detailTask.start();
		showProgressDialog();
	}

	SHPostTaskM addOrdelTask;

	void addOrDelete() {
		addOrdelTask = getTask(DEFAULT_API_URL + "miFollowerAdd.do", this);
		addOrdelTask.getTaskArgs().put("myUserName", accountService().name());
		addOrdelTask.getTaskArgs().put("followerUserName", dsCyDetail.getString("friendName"));
		addOrdelTask.getTaskArgs().put("addOrDelete", dsCyDetail.getInt("isFollowed") == 0 ? 1 : 0);
		addOrdelTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if (detailTask == task) {
			dsCyDetail = DSObjectFactory.create(CPModeName.CY_DETAIL).fromJson(task.getResult());
			updateView();
		} else if (addOrdelTask == task) {
			if (dsCyDetail.getInt("isFollowed") == 0) {
				dsCyDetail.put("isFollowed", 1);
			} else {
				dsCyDetail.put("isFollowed", 0);
			}
			updateView();
		}
	}

	class Adapter extends CommonDSAdapter {

		public Adapter(DSObject[] data) {
			if (data != null) {
				this.dsList.addAll(Arrays.asList(data));
			}
		}

		public String getTitle(int position) {
			DSObject obj = getItem(position);
			if (obj.isObject("personalSkills")) {
				return obj.getString("skillTitle");
			} else if (obj.isObject("successfulCases")) {
				return obj.getString("caseTitle");
			} else if (obj.isObject("myTasks")) {
				return obj.getString("taskStatus");
			}
			return "";
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
				item.setTitle(getTitle(position));
				item.setClickable(true);
			} else if (obj.isObject("successfulCases")) {
				item.setTitle(getTitle(position));
				item.setClickable(true);
			} else if (obj.isObject("myTasks")) {
				item.setTitle(obj.getString("startTime") + " - " + obj.getString("endTime"));
				item.setSubTitle(getTitle(position));
				item.setSubTitleColor(getResources().getColor(R.color.red));
				item.setIndicator(0);
				item.setClickable(false);
			}
			return convertView;
		}

	}

}
