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

import com.damon.ds.app.DSObject;
import com.damon.ds.util.DSObjectFactory;
import com.next.net.SHCacheType;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;

public class QueryCxActivity extends BaseActivity implements OnClickListener {

	Spinner cateSpinner;
	EditText executeEditText, titleEditText;
	Button queryButton;
	CateAdapter cateAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_caixin);

		cateSpinner = (Spinner) findViewById(R.id.category);
		executeEditText = (EditText) findViewById(R.id.execute);
		titleEditText = (EditText) findViewById(R.id.title);
		queryButton = (Button) findViewById(R.id.query_btn);
		queryButton.setOnClickListener(this);
		queryButton.setEnabled(false);

		queryCate();
	}

	SHPostTaskM queryCateTask;

	void queryCate() {
		queryCateTask = getTask(DEFAULT_API_URL + "miQueryOppoListInit.do", this);
		queryCateTask.setChacheType(SHCacheType.PERSISTENT);
		queryCateTask.start();
		showProgressDialog();
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		if(task == queryCateTask){
			DSObject dsobj = DSObjectFactory.create(CPModeName.CAIXIN_TYPE_LIST).fromJson(task.getResult());
			cateAdapter = new CateAdapter(this, android.R.layout.simple_spinner_item, dsobj.getArray(CPModeName.CAIXIN_TYPE_LIST,CPModeName.CAIXIN_TYPE_ITEM));
			cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cateSpinner.setAdapter(cateAdapter);
			queryButton.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		if(v == queryButton){
			Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("cp://cxlist"));
			intent.putExtra("oppotype", cateAdapter.getOppoType(cateSpinner.getSelectedItemPosition()));
			intent.putExtra("bossname", executeEditText.getText().toString());
			intent.putExtra("oppotitle", titleEditText.getText().toString());
			startActivity(intent);
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
		
		public String getOppoType(int position){
			return dsCates[position].getString("key");
		}

	}

}
