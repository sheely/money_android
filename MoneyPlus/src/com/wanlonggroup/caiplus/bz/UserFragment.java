package com.wanlonggroup.caiplus.bz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.damon.ds.widget.BasicSingleItem;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseFragment;

public class UserFragment extends BaseFragment implements OnClickListener {

	private Button logoutBtn;
	private BasicSingleItem myCalender, myConcern, myTeam;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("我");

		myCalender = (BasicSingleItem) view.findViewById(R.id.my_calender);
		myCalender.setOnClickListener(this);

		myConcern = (BasicSingleItem) view.findViewById(R.id.my_concern);
		myConcern.setOnClickListener(this);

		myTeam = (BasicSingleItem) view.findViewById(R.id.my_team);
		myTeam.setOnClickListener(this);

		logoutBtn = (Button) view.findViewById(R.id.logout_btn);
		logoutBtn.setOnClickListener(this);
	}

	@Override
	public View onSetView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.user_fragment, container, false);
	}

	@Override
	protected boolean hasActionBar() {
		return true;
	}

	@Override
	public void onClick(View v) {
		if(myCalender == v){
			startActivity("cp://mycalender");
		}else if(myConcern == v){
			startActivity("cp://myconcern");
		}else if(myTeam == v){
			startActivity("cp://myteam");
		}else if(logoutBtn == v){
			showAlert("提示", "确认退出?", true, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					accountService().logout();
					startActivity("cp://login");
					finish();
				}
			}, null);
		}
	}

}
