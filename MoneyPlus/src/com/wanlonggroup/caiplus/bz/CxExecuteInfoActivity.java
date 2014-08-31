package com.wanlonggroup.caiplus.bz;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;
import com.wanlonggroup.caiplus.widget.CxDetailHeader;
import com.wanlonggroup.caiplus.widget.CxDetailHeader.HeaderMode;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicSingleItem;

public class CxExecuteInfoActivity extends BaseActivity {

	DSObject dsCaixin;

	int currentDialogId;

	static final int START_DATE_DIALOG_ID = 1;

	static final int END_DATE_DIALOG_ID = 2;

	boolean isEditable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_exec_info);
		dsCaixin = getIntent().getParcelableExtra("caixin");
		setupView();
		updateView();
		queryInfo();
	}

	public void onCreateActionBar(DSActionBar actionBar) {
		if (!isEditable) {
			actionBar.addAction("编辑", "edite", new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					setInfoEditable(true);
				}
			});
		} else {
			actionBar.addAction("提交", "submit", new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					modifyInfo();
				}
			});
		}
	};

	void setInfoEditable(boolean editable) {
		this.isEditable = editable;
		startTimeItem.getSubTitleView().setClickable(editable);
		endTimeItem.getSubTitleView().setClickable(editable);
		locationEditText.setEnabled(editable);
		budgetEditText.setEnabled(editable);
		remarkEditText.setEnabled(editable);
		invalidateActionBar();
	}

	CxDetailHeader cxDetailHeader;
	BasicSingleItem startTimeItem, endTimeItem;
	EditText locationEditText, budgetEditText, remarkEditText;

	void setupView() {
		cxDetailHeader = (CxDetailHeader) findViewById(R.id.detail_header);
		cxDetailHeader.setMode(HeaderMode.exec);

		startTimeItem = (BasicSingleItem) findViewById(R.id.start_time);
		startTimeItem.getSubTitleView().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID);
			}
		});

		endTimeItem = (BasicSingleItem) findViewById(R.id.end_time);
		endTimeItem.getSubTitleView().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(END_DATE_DIALOG_ID);
			}
		});

		locationEditText = (EditText) findViewById(R.id.location);
		budgetEditText = (EditText) findViewById(R.id.budget);
		remarkEditText = (EditText) findViewById(R.id.remark);

		setInfoEditable(false);
	}

	void updateView() {
		if (dsCaixin == null) {
			return;
		}
		cxDetailHeader.setDetail(dsCaixin);
		startTimeItem.setSubTitle(Utils.formate(dsCaixin.getString("startTime")));
		endTimeItem.setSubTitle(Utils.formate(dsCaixin.getString("endTime")));
		locationEditText.setText(dsCaixin.getString("executePlace"));
		budgetEditText.setText(dsCaixin.getString("budget"));
		remarkEditText.setText(dsCaixin.getString("remark"));
	}

	SHPostTaskM queryTask;

	void queryInfo() {
		queryTask = getTask(DEFAULT_API_URL + "queryexecuteinfodetail.do", this);
		queryTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		queryTask.start();
		showProgressDialog();
	}

	SHPostTaskM modifyTask;

	void modifyInfo() {
		String start = startTimeItem.getSubTitleView().getText().toString();
		String end = endTimeItem.getSubTitleView().getText().toString();
		if (TextUtils.isEmpty(start)) {
			showAlert("请选择执行时间");
			return;
		}

		if (TextUtils.isEmpty(end)) {
			showAlert("请选择结束时间");
			return;
		}

		if (Utils.wrapDatetime(start).after(Utils.wrapDatetime(end))) {
			showAlert("结束时间不能早于开始时间");
			return;
		}

		modifyTask = getTask(DEFAULT_API_URL + "miExecuteInfo.do", this);
		modifyTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		modifyTask.getTaskArgs().put("startTime", start);
		modifyTask.getTaskArgs().put("endTime", end);
		modifyTask.getTaskArgs().put("executePlace", locationEditText.getText());
		modifyTask.getTaskArgs().put("budget", budgetEditText.getText());
		modifyTask.getTaskArgs().put("remark", remarkEditText.getText());
		modifyTask.start();
		showProgressDialog("提交中...");
	}
	
	@Override
	public void onProgressDialogCancel() {
		super.onProgressDialogCancel();
		if(queryTask != null){
			finish();
		}
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		if (task == queryTask) {
			queryTask = null;
			dismissProgressDialog();
			dsCaixin = DSObjectFactory.create(CPModeName.CAIXIN_ITEM).fromJson(task.getResult());
			updateView();
		} else if (task == modifyTask) {
			modifyTask = null;
			dismissProgressDialog();
			setInfoEditable(false);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			currentDialogId = START_DATE_DIALOG_ID;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(Utils.wrapDatetime(startTimeItem.getSubTitleView().getText().toString()));
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case END_DATE_DIALOG_ID:
			currentDialogId = END_DATE_DIALOG_ID;
			Calendar calenda = Calendar.getInstance();
			calenda.setTime(Utils.wrapDatetime(endTimeItem.getSubTitleView().getText().toString()));
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
			startTimeItem.setSubTitle(Utils.formate(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append(
				"-").append(mDay).toString()));
		} else if (currentDialogId == END_DATE_DIALOG_ID) {
			endTimeItem.setSubTitle(Utils.formate(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append(
				"-").append(mDay).toString()));
		}
	}

}
