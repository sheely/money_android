package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.xdamon.app.DSObject;
import com.xdamon.widget.DSActionBar;

public class ModifyCalenderActivity extends BaseActivity implements OnClickListener {

	DSObject dsTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsTask = getIntent().getParcelableExtra("task");
		setupView();
		updateView();
	}

	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		super.onCreateActionBar(actionBar);
		actionBar.addAction("提交", "mod_calender", this);
	}

	TextView startTimeView, endTimeView;
	EditText contentView;

	void setupView() {
		setContentView(R.layout.modify_calender_layout);
		startTimeView = (TextView) findViewById(R.id.start_time);
		endTimeView = (TextView) findViewById(R.id.end_time);
		contentView = (EditText) findViewById(R.id.content);
	}

	void updateView() {
		if (dsTask == null) {
			return;
		}
		startTimeView.setText(dsTask.getString("startTime"));
		endTimeView.setText(dsTask.getString("endTime"));
		contentView.setText(dsTask.getString("taskContent"));
	}

	SHPostTaskM modifyTask;

	void modifyTask() {
		String content = contentView.getText().toString();
		if (TextUtils.isEmpty(content)) {
			showAlert("内容不能为空");
			return;
		}
		modifyTask = getTask(DEFAULT_API_URL + "miTaskMaintainance.do", this);
		modifyTask.getTaskArgs().put("taskId", dsTask == null ? "" : dsTask.getString("taskId"));
		modifyTask.getTaskArgs().put("operationType", dsTask == null ? 1 : 2);
		modifyTask.getTaskArgs().put("startTime", startTimeView.getText());
		modifyTask.getTaskArgs().put("endTime", endTimeView.getText());
		modifyTask.getTaskArgs().put("taskContent", content);
		modifyTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		super.onTaskFinished(task);
		Toast.makeText(this, dsTask == null ? "新增日历成功" : "修改日历成功", Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View v) {
		if ("mod_calender".equals(v.getTag())) {
			modifyTask();
		}
	}

}
