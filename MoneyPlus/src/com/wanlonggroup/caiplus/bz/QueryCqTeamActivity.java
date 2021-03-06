package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.xdamon.app.DSObject;

public class QueryCqTeamActivity extends BaseActivity implements OnClickListener {

	DSObject dsCyOwner, dsCyMember;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_cq_team);
		setupView();
	}

	TextView ownerTextView, teamNameTextView, memberTextView;
	Button queryBtn;

	void setupView() {
		ownerTextView = (TextView) findViewById(R.id.owner_name);
		ownerTextView.setOnClickListener(this);

		teamNameTextView = (TextView) findViewById(R.id.team_name);

		memberTextView = (TextView) findViewById(R.id.team_member);
		memberTextView.setOnClickListener(this);

		queryBtn = (Button) findViewById(R.id.query_btn);
		queryBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == ownerTextView) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://querycy"));
			intent.putExtra("forresult", true);
			startActivityForResult(intent, 1);
		} else if (v == memberTextView) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://querycy"));
			intent.putExtra("forresult", true);
			startActivityForResult(intent, 2);
		} else if (v == queryBtn) {
			String teamName = teamNameTextView.getText().toString();
			if(TextUtils.isEmpty(teamName)){
				teamName = "";
			}
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cqteamlist"));
			intent.putExtra("owneruserid", dsCyOwner == null ? "" : dsCyOwner.getString("friendId"));
			intent.putExtra("ownerusername", dsCyOwner == null ? "" : dsCyOwner.getString("friendName"));
			intent.putExtra("teamname", teamName);
			intent.putExtra("memberusername", dsCyMember == null ? "" : dsCyMember.getString("friendName"));
			startActivity(intent);
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg2 == null){
			return;
		}
		if(arg0 == 1){
			dsCyOwner = arg2.getParcelableExtra("caiyou");
			ownerTextView.setText(dsCyOwner.getString("friendName"));
		}else if(arg0 == 2){
			dsCyMember = arg2.getParcelableExtra("caiyou");
			memberTextView.setText(dsCyMember.getString("friendName"));
		}
	}

}
