package com.wanlonggroup.caiplus.cx;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;

public class CxDetailActivity extends BaseActivity implements OnClickListener{
	
	Button reviewBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_detail);
		setupView();
	}
	
	void setupView(){
		reviewBtn = (Button) findViewById(R.id.review_btn);
		reviewBtn.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		startActivity("cp://cxreviewdetail");
	}

}
