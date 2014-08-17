package com.wanlonggroup.caiplus.bz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.next.net.SHCacheType;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class QueryCyActivity extends BaseActivity implements OnClickListener {

	Spinner cateSpinner, companySpinner;
	EditText nameText, addressText;
	Button queryButton;
	Adapter cateAdapter, companyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_cy);

		cateSpinner = (Spinner) findViewById(R.id.category);
		companySpinner = (Spinner) findViewById(R.id.company);
		nameText = (EditText) findViewById(R.id.name);
		addressText = (EditText) findViewById(R.id.address);

		queryButton = (Button) findViewById(R.id.query_btn);
		queryButton.setOnClickListener(this);
		queryButton.setEnabled(false);

		initQuery();
	}

	SHPostTaskM initQueryTask;

	void initQuery() {
		initQueryTask = getTask(DEFAULT_API_URL + "miQueryFriendInit.do", this);
		initQueryTask.setChacheType(SHCacheType.PERSISTENT);
		initQueryTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if (task == initQueryTask) {
			DSObject dsobj = DSObjectFactory.create("miQueryFriendInit").fromJson(task.getResult());
			cateAdapter = new Adapter(this, android.R.layout.simple_spinner_item, dsobj.getArray(
				CPModeName.CAIXIN_TYPE_LIST, CPModeName.CAIXIN_TYPE_ITEM));
			cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cateSpinner.setAdapter(cateAdapter);

			companyAdapter = new Adapter(this, android.R.layout.simple_spinner_item, dsobj.getArray(
				CPModeName.CQ_COMPANY_LIST, CPModeName.CQ_COMPANY_ITEM));
			companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			companySpinner.setAdapter(companyAdapter);

			queryButton.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == queryButton) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cylist"));
			intent.putExtra("oppotype", cateAdapter.getKey(cateSpinner.getSelectedItemPosition()));
			intent.putExtra("companyid", companyAdapter.getKey(cateSpinner.getSelectedItemPosition()));
			intent.putExtra("friendname", nameText.getText().toString());
			intent.putExtra("address", addressText.getText().toString());
			intent.putExtra("forresult", getBooleanParam("forresult"));
			startActivityForResult(intent, 1);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 1) {
			setResult(RESULT_OK, arg2);
			finish();
		}
	}

	class Adapter extends ArrayAdapter<String> {

		DSObject[] data;

		public Adapter(Context context, int textViewResourceId, DSObject[] dsCates) {
			super(context, textViewResourceId);
			this.data = dsCates;
		}

		@Override
		public int getCount() {
			if (data != null) {
				return data.length;
			}
			return 0;
		}

		@Override
		public String getItem(int position) {
			return data[position].getString("value");
		}

		public String getKey(int position) {
			return data[position].getString("key");
		}

	}

}
