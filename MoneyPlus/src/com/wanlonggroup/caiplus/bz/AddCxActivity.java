package com.wanlonggroup.caiplus.bz;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.next.net.SHCacheType;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class AddCxActivity extends BaseActivity {

	CateAdapter cateAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_cx);

		setupView();
		queryCate();
	}

	public void onCreateActionBar(DSActionBar actionBar) {
		actionBar.addAction("提交", "add_cx", new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cateAdapter == null || cateAdapter.getCount() == 0) {
					return;
				}
				addCaixin();
			}
		});
	};

	Spinner cateSpinner;
	EditText titleEditText, contentEditText;

	void setupView() {
		cateSpinner = (Spinner) findViewById(R.id.category);
		titleEditText = (EditText) findViewById(R.id.title);
		contentEditText = (EditText) findViewById(R.id.content);
	}

	SHPostTaskM queryCateTask;

	void queryCate() {
		queryCateTask = getTask(DEFAULT_API_URL + "miAddNewOppoGuide.do", this);
		queryCateTask.setChacheType(SHCacheType.PERSISTENT);
		queryCateTask.start();
		showProgressDialog();
	}

	SHPostTaskM addCxTask;

	void addCaixin() {
		String title = titleEditText.getText().toString();
		if (TextUtils.isEmpty(title)) {
			showAlert("请填写标题");
			return;
		}
		addCxTask = getTask(DEFAULT_API_URL + "miAddNewOppo.do", this);
		addCxTask.getTaskArgs().put("oppotitle", title);
		addCxTask.getTaskArgs().put("oppotype", cateAdapter.getOppoType(cateSpinner.getSelectedItemPosition()));
		addCxTask.getTaskArgs().put("oppocontent", contentEditText.getText());
		addCxTask.start();
		showProgressDialog("提交中...");
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if (task == queryCateTask) {
			DSObject dsobj = DSObjectFactory.create(CPModeName.CAIXIN_TYPE_LIST).fromJson(task.getResult());
			cateAdapter = new CateAdapter(this, android.R.layout.simple_spinner_item, dsobj.getArray(
				CPModeName.CAIXIN_TYPE_LIST, CPModeName.CAIXIN_TYPE_ITEM));
			cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cateSpinner.setAdapter(cateAdapter);
		} else if (task == addCxTask) {
			showShortToast("新建财信成功");
			finish();
		}
	}

	class CateAdapter extends ArrayAdapter<String> {

		DSObject[] dsCates;

		public CateAdapter(Context context, int textViewResourceId, DSObject[] dsCates) {
			super(context, textViewResourceId);
			this.dsCates = dsCates;
		}

		@Override
		public int getCount() {
			if (dsCates != null) {
				return dsCates.length;
			}
			return 0;
		}

		@Override
		public String getItem(int position) {
			return dsCates[position].getString("value");
		}

		public String getOppoType(int position) {
			return dsCates[position].getString("key");
		}

	}

}
