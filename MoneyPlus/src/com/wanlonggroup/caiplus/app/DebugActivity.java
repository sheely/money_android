package com.wanlonggroup.caiplus.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.wanlonggroup.caiplus.util.ConfigSwitch.DomainType;

public class DebugActivity extends com.damon.ds.app.DebugActivity implements OnClickListener {

	private Button domainBtn;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		domainBtn = (Button) findViewById(R.id.domain);
		domainBtn.setOnClickListener(this);
		
		updateView();
	}
	
	@Override
	public void onSetContentView() {
		setContentView(R.layout.debug_panel);
	}
	
	private void updateView(){
		domainBtn.setText(ConfigSwitch.instance().getDomainType().toString());
	}

	@Override
	public void onClick(View v) {
		if (domainBtn == v) {
			final String[] items = new String[] { "Germmy", "None"};
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("选择服务器环境");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ConfigSwitch.instance().swithDomain(DomainType.toType(which));
					updateView();
				}

			});
			builder.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
