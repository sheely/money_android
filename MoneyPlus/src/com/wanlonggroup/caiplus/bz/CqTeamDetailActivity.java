package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;
import com.xdamon.widget.BasicSingleItem;
import com.xdamon.widget.TableView;

public class CqTeamDetailActivity extends BaseActivity {

	DSObject dsTeam;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dsTeam = getIntent().getParcelableExtra("team");
		setupView();
		updateView();
		queryDetail();
	}

	ImageView teamIcon;
	TextView teamName, teamCategory, teamDesc;
	TableView memTable;

	void setupView() {
		setContentView(R.layout.cq_team_detail);
		teamIcon = (ImageView) findViewById(R.id.team_icon);
		teamName = (TextView) findViewById(R.id.team_name);
		teamCategory = (TextView) findViewById(R.id.category_name);
		teamDesc = (TextView) findViewById(R.id.desc);
		memTable = (TableView) findViewById(R.id.member_table);
	}

	void updateView() {
		if (dsTeam == null) {
			return;
		}
		imageLoader.displayImage(dsTeam.getString("teamHeadIcon"), teamIcon, displayOptions);
		teamName.setText(dsTeam.getString("teamName"));
		teamCategory.setText(dsTeam.getString("teamType"));
		teamDesc.setText(dsTeam.getString("teamIntroduction"));

		memTable.removeAllViews();
		if (dsTeam.hasKey("teamMembers")) {
			for (DSObject obj : dsTeam.getArray("teamMembers")) {
				final BasicSingleItem item = (BasicSingleItem) LayoutInflater.from(this).inflate(
					R.layout.common_basic_single_item, memTable, false);
				imageLoader.displayImage(obj.getString("memberHeadIcon"), item.getLeftImageView(), displayOptions,
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String paramString, View paramView, Bitmap paramBitmap) {
							item.getLeftImageView().setVisibility(View.VISIBLE);
						}

					});
				item.setTitle(obj.getString("memberName"));
				if (obj.getInt("isOwner") == 1) {
					item.setSubTitleColor(getResources().getColor(R.color.red));
					item.setSubTitle("发起人");
				}
				item.setOnClickListener(onClickListener);
				item.setTag(obj);
				memTable.addView(item);
			}
		}
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DSObject obj = (DSObject) v.getTag();
			Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("cp://cydetail"));
			intent.putExtra("friendid", obj.getString("memberId"));
			startActivity(intent);
		}
	};

	SHPostTaskM detailTask;

	void queryDetail() {
		detailTask = getTask(DEFAULT_API_URL + "miQueryTeamDetail.do", this);
		detailTask.getTaskArgs().put("teamId", dsTeam.getString("teamId"));
		detailTask.start();
		showProgressDialog();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		super.onTaskFinished(task);
		dsTeam = DSObjectFactory.create(CPModeName.CQ_TEAM_ITEM).fromJson(task.getResult());
		updateView();
	}

}
