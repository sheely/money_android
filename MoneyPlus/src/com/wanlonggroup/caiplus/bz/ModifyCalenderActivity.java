package com.wanlonggroup.caiplus.bz;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.util.Utils;
import com.xdamon.app.DSObject;
import com.xdamon.widget.DSActionBar;

public class ModifyCalenderActivity extends BaseActivity implements OnClickListener {

	DSObject dsTask;
	
	int currentDialogId;

	static final int START_DATE_DIALOG_ID = 1;

	static final int END_DATE_DIALOG_ID = 2;

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
		startTimeView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID);
			}
		});
		
		endTimeView = (TextView) findViewById(R.id.end_time);
		endTimeView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(END_DATE_DIALOG_ID);
			}
		});
		
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
		if(Utils.wrapDatetime(startTimeView.getText().toString()).after(Utils.wrapDatetime(endTimeView.getText().toString()))){
			showAlert("开始时间必须晚于结束时间");
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			currentDialogId = START_DATE_DIALOG_ID;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(Utils.wrapDatetime(startTimeView.getText().toString()));
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case END_DATE_DIALOG_ID:
			currentDialogId = END_DATE_DIALOG_ID;
			Calendar calenda = Calendar.getInstance();
			calenda.setTime(Utils.wrapDatetime(endTimeView.getText().toString()));
			mYear = calenda.get(Calendar.YEAR);
			mMonth = calenda.get(Calendar.MONTH);
			mDay = calenda.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	private int mYear;
	private int mMonth;
	private int mDay;
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private void updateDisplay() {
		if (currentDialogId == START_DATE_DIALOG_ID) {
			startTimeView.setText(Utils.formate(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append(
				"-").append(mDay).toString()));
		} else if (currentDialogId == END_DATE_DIALOG_ID) {
			endTimeView.setText(Utils.formate(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append(
					"-").append(mDay).toString()));
		}
	}

}
